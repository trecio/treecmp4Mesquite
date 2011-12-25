/*
 * This software is part of the Tree Set Visualization module for Mesquite,
 * written by Jeff Klingner and Silvio Neris
 *
 * Copyright (c) 2002 by the University of Texas
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee under the GNU Public License is hereby granted, 
 * provided that this entire notice  is included in all copies of any software 
 * which is or includes a copy or modification of this software and in all copies
 * of the supporting documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR THE UNIVERSITY OF TEXAS
 * AT AUSTIN MAKE ANY REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE 
 * MERCHANTABILITY OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

package mesquite.treecomp.common;

import mesquite.lib.Tree;

/**
 * This class implements the postorder sequence with weights representation of
 * trees. This representation is the starting point for computing Day's
 * "cluster tables" The main advantage is in being able to look up the next node
 * visited after a given node in a postorder traversal of the tree in constant
 * time.
 */
public class PSWTree {
	int n; /* number of leaves */
	int enumerator; /* used in calls to enumerate vertices */
	int j; /* total number of verticies (leaves+internal) in the tree */
	int[][] vw; /*
				 * array of vertex/weight pairs; indexed by the order
				 * encountered in a post-order traversal
				 */
	double[] branchLengths; /* indexed by vertex */
	Tree t; /* The Mesquite tree this PSWtree represents */
	public static final int VERTEX = 0;
	public static final int WEIGHT = 1;
	public static final double defaultBranchLength = 1.0;

	public PSWTree(Tree t) {
		this.t = t;
		n = t.getNumTaxa();
		enumerator = 0;
		vw = new int[n * 2][2];
		branchLengths = new double[n * 3 + 1];
		for (int i = 0; i < branchLengths.length; ++i) {
			branchLengths[i] = -1;
		}
		j = 0; /* index for building the array */
		int firstNode = t.nodeOfTaxonNumber(0);
		constructionRecursor(firstNode, t.motherOfNode(firstNode));
		/* insert taxon zero in list at second-to-last entry */
		vw[j][VERTEX] = 0; // taxon zero
		vw[j][WEIGHT] = 0; // leaves hove weight zero
		branchLengths[0] = t.getBranchLength(firstNode, defaultBranchLength);
		j++;
		/*
		 * insert pseudo-root (node on the branch incident on firstNode) at last
		 * entry
		 */
		vw[j][VERTEX] = n + 1; // actual value here doesn't matter
		vw[j][WEIGHT] = vw[j - 2][WEIGHT] + 2; /* This one does. */
		/* This new root has no parent and so has no branch length */
		j++;
		/* barfPSW(); */
	}

	private int constructionRecursor(int a, int v) {
		if (t.nodeIsTerminal(v)) {
			vw[j][VERTEX] = t.taxonNumberOfNode(v);
			vw[j][WEIGHT] = 0;
			branchLengths[vw[j][VERTEX]] = t.getBranchLength(v,
					defaultBranchLength);
			j++;
			return 1;
		} else {
			int w = 0;
			int numChildren = 0;
			for (int d = t.firstDaughterOfNodeUR(a, v); d > 0; d = t
					.nextSisterOfNodeUR(a, v, d)) { /* recurse, baby! */
				w += constructionRecursor(v, d);
				numChildren++;
			}
			if (numChildren > 1) {
				vw[j][VERTEX] = j + n + 1; /*
											 * to ensure that interior labels
											 * are > n
											 */
				vw[j][WEIGHT] = w;
				branchLengths[vw[j][VERTEX]] = t.getBranchLength(t
						.nodeOfBranchUR(a, v), defaultBranchLength);
				j++;
				return w + 1;
			} else {
				/* interior node of degree 2. Treat as if non-existent */
				return w;
			}
		}
	}

	public int getN() {
		return n;
	}

	public void prepareForEnumeration() {
		enumerator = 0;
	}

	public int[] nextVertex() {
		if (enumerator < j) {
			return vw[enumerator++];
		} else {
			return null;
		}
	}

	/**
	 * Returns the leftmost leaf of the vertex that returned by the last call of
	 * nextVertex
	 */
	public int leftmostLeaf() {
		return vw[(enumerator - 1 - vw[enumerator - 1][1])][0];
	}

	/** returns the length of the branch between vertex and its parent */
	public double getLength(int vertex) {
		if (branchLengths[vertex] == -1) {
			System.out.println("Error: unassigned branch length requested");
		}
		return branchLengths[vertex];
	}

	/** dumps a textual copy of the PSW table to the screen for debugging */
	private void barfPSW() {
		System.out.println("PSW for " + t.getName());
		for (int i = 0; i < j; i++) {
			System.out.println(i + ": " + vw[i][VERTEX] + "," + vw[i][WEIGHT]);
		}
		System.out.println();
	}
}