/*
 * This software is part of the Tree Set Visualization module for Mesquite,
 * written by Jeff Klingner, Fred Clarke, and Denise Edwards.
 *
 * Copyright (c) 2002 by the University of Texas
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted under the GNU Lesser General 
 * Public License, as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version, 
 * provided that this entire notice is included in all copies of any 
 * software which are or include a copy or modification of this software
 * and in all copies of the supporting documentation for such software.
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR THE UNIVERSITY OF TEXAS
 * AT AUSTIN MAKE ANY REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE 
 * MERCHANTABILITY OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 * IN NO CASE WILL THESE PARTIES BE LIABLE FOR ANY SPECIAL, INCIDENTAL, 
 * CONSEQUENTIAL, OR OTHER DAMAGES THAT MAY RESULT FROM USE OF THIS SOFTWARE.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	Last change:  DE   15 Apr 2003    2:35 pm
 */

package mesquite.treecomp.TreeSetVisualizationV2.mds;

/* This file isolates everything involved with MDS, primarily to keep file size
 * down.  It includes classes for MDS and its supporting data structures: the
 * difference matrix and the point.
 */

/**
 * Implements Multi-Dimensional Scaling. some methods are synchronized because
 * an mds object can be affected by two threads at once: by the MDSThread doing
 * the calculations, and by the user interface code responding to user requests
 * that the points be scrambled or changing the step size or the sampling
 * policy.
 */
public class MDS {

	/** The goal distances for the dimensional scaling. ("big D") */
	private SampledDiffMatrix targetDistances;
	/** Number of nodes (points) for the dimensional scaling. */
	private int n_nodes;
	/**
	 * Dimensionality of the Euclidian space into which the nodes will be
	 * embedded.
	 */
	private int n_dims;
	/** Current locations of the embedded points. */
	private MDSPoint[] nodes;
	/** The "nudge" for each point that will improve the embedding. */
	private MDSPoint[] changes;
	/** A scratch variable used in doOneIteration */
	private MDSPoint d;
	/** Global state needed by the xgvis version of mds */
	private float stress, stress_dd, stress_dx, stress_xx;
	/**
	 * How agressively we persue an embedding. The value of this is very
	 * important and can be adjusted by the user.
	 */
	private float stepSize;

	// Embedding re-centering constants
	/**
	 * how far the average point location must drift from the origin before
	 * re-centering is triggered
	 */
	protected static final float CENTERING_THRESHOLD = 1e5f; // aribtrary


	private MDSPoint nudgeVector;
	private float nudgeVectorLength;
	private static final float NUDGE_VECTOR_SIZE = 0.0001f;

	/**
	 * Constructor for the MDS object
	 * 
	 * @param targetDistances
	 *            Difference Matrix containing the goal of the embedding
	 * @param n_dims
	 *            Number of dimensions to perform the embedding in
	 */
	public MDS(SampledDiffMatrix targetDistances, int n_dims, float stepSize) {
		this.targetDistances = targetDistances;
		this.n_dims = n_dims;
		this.stepSize = stepSize;
		n_nodes = targetDistances.getNumberOfItems();
		// allocate memory for data structures
		changes = new MDSPoint[n_nodes];
		nodes = new MDSPoint[n_nodes];
		for (int i = 0; i < n_nodes; i++) {
			changes[i] = new MDSPoint(n_dims);
			nodes[i] = new MDSPoint(n_dims);
		}
		d = new MDSPoint(n_dims);
		randomize_nodes();

		nudgeVector = new MDSPoint(n_dims);
		nudgeVector.zero();
		nudgeVector.setComponent(0, NUDGE_VECTOR_SIZE);
		nudgeVectorLength = nudgeVector.magnitude();
	}

	public void resetNumberOfItems(int newNumberOfItems) {
		n_nodes = newNumberOfItems;
		changes = new MDSPoint[n_nodes];
		nodes = new MDSPoint[n_nodes];
		for (int i = 0; i < n_nodes; i++) {
			changes[i] = new MDSPoint(n_dims);
			nodes[i] = new MDSPoint(n_dims);
		}
		randomize_nodes();
	}

	/**
	 * Sets the step size. The caller is trusted to make sure this is a sane
	 * value.
	 * 
	 * @param newStepSize
	 *            The new stepSize value
	 */
	public synchronized void setStepSize(float newStepSize) {
		stepSize = newStepSize;
	}

	/**
	 * Gets the current positions of all the points
	 * 
	 * @return The current embedding
	 */
	public synchronized MDSPoint[] getEmbedding() {
		return nodes;
	}

	/**
	 * Gets the stress of the current MDS configuration
	 * 
	 * @return The stress of the current configuration
	 */
	public synchronized float getStress() {
		return stress;
	}

	/**
	 * Gets the current step size. Called in response to user action to
	 * mannually adjust the step size, because the step size is not kept as
	 * state anywhere else.
	 * 
	 * @return The current step size
	 */
	public synchronized float getStepSize() {
		return stepSize;
	}

	/**
	 * Recenters the points at the origin. Only changes things if the points are
	 * more than CENTERING_THRESHOLD off center.
	 */
	public synchronized void center_embedding() {
		float[] node_means = new float[n_dims];
		// First, compute the "average point" that is the center of the
		// embedding.
		for (int i = 0; i < n_nodes; i++) {
			for (int j = 0; j < n_dims; j++) {
				node_means[j] += nodes[i].getComponent(j);
			}
		}
		for (int j = 0; j < n_dims; j++) {
			node_means[j] /= n_nodes;
		}

		// Second, subtract the "average point" from every node to recenter them
		// at the origin.
		MDSPoint p = new MDSPoint(node_means);
		if (p.magnitude() > CENTERING_THRESHOLD) {
			for (int i = 0; i < n_nodes; i++) {
				nodes[i].subtract(p);
			}
			System.out.println("MDS recentered it's embedding.");
		}
	}

	/** Perform one iteration of MDS */
	public synchronized void doOneIteration() {
		boolean sampling = targetDistances.getSampling();
		// System.out.println("MDS did an iteration. sampling = " + sampling);
		float resid;
		float d_length;
		float stress_diff_accum = 0;
		float stress_norm_accum = 0;

		// First, zero out the change vectors
		for (int i = 0; i < n_nodes; ++i) {
			changes[i].zero();
		}

		// Compute how much each point is pushed or pulled on by each other
		// point
		for (int i = 0; i < n_nodes; ++i) {
			for (int j = 0; j < i; ++j) {
				if (!sampling || targetDistances.diffInSample(i, j) // short
																	// circuiting
																	// of the ||
																	// operator
						|| targetDistances.diffInSample(j, i)) { // avoids
																	// unneccesary
																	// calls to
																	// diffInSample
					d.setToDifference(nodes[j], nodes[i]);
					d_length = d.magnitude();
					// If two points are on top of each other, we need to pick
					// an arbitrary direction to nudge them apart.
					if (d_length == 0) {
						d.add(nudgeVector);
						d_length = nudgeVectorLength;
					}
					resid = d_length - targetDistances.getElement(i, j);
					d.scale(resid / d_length);
					if (!sampling || targetDistances.diffInSample(i, j)) {
						changes[i].add(d); // accumulate the changes for point i
					}
					if (!sampling || targetDistances.diffInSample(j, i)) {
						changes[j].subtract(d); // similarly for j, in the
												// opposite direction
					}
					// accumulate sums for stress calculations
					stress_diff_accum += resid * resid;
					stress_norm_accum += d_length * d_length;
				}
			}
		}

		// Finally, apply the changes
		for (int i = 0; i < n_nodes; ++i) {
			changes[i].scale(stepSize);
			nodes[i].add(changes[i]);
		}

		/* Compute stress (this is a normalized stress, called Kruskal-1) */
		if (stress_norm_accum > 0) {
			stress = (float) Math.sqrt(stress_diff_accum / stress_norm_accum);
		} else {
			System.out.println("Stress calculation problem");
		}
		// System.out.println("normalized stress = " + stress);
	}

	/** Compute the current stress */
	public synchronized void compute_stress() {
		float resid;
		float d_length;
		float stress_diff_accum = 0;
		float stress_norm_accum = 0;

		for (int i = 0; i < n_nodes; ++i) {
			for (int j = 0; j < i; ++j) {
				d.setToDifference(nodes[j], nodes[i]);
				d_length = d.magnitude();
				resid = d_length - targetDistances.getElement(i, j);

				// accumulate sums for stress calculations
				stress_diff_accum += resid * resid;
				stress_norm_accum += d_length * d_length;
			}
		}

		// Compute stress (this is a normalized stress, called Kruskal-1)
		if (stress_norm_accum > 0) {
			stress = (float) Math.sqrt(stress_diff_accum / stress_norm_accum);
		} else {
			System.out.println("Stress calculation problem");
		}
	}

	/**
	 * Randomizes the embedding. Gives every coordinate of every point a random
	 * value between -1 and 1.
	 */
	public synchronized void randomize_nodes() {
		java.util.Random random_source = new java.util.Random();
		for (int i = 0; i < n_nodes; i++) {
			for (int j = 0; j < n_dims; j++) {
				nodes[i].setComponent(j, random_source.nextFloat());
			}
		}
	}
}

