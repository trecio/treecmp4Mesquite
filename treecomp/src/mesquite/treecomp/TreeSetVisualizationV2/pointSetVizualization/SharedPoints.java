package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import mesquite.treecomp.TreeSetVisualizationV2.mds.MDSPoint;

/**
 * This is a class I wrote to encapsulate all of the interaction between the mds
 * thread and the display (main) thread. The two threads communicate through
 * shared data (a set of points that represents the current embedding). There is
 * no other shared resouce. (Except for processor time on a single-processor
 * system. The two threads will have the same priority and yield to each other
 * after each loop iteration to make sure that one of them doesn't hog the CPU.)
 * 
 *@author Jeff Klingner
 */
public class SharedPoints {

	private MDSPoint[] points;
	private float[][] bare_points;
	private int n_dims;
	private float stress;

	public SharedPoints(int number_of_points, int number_of_dimensions) {
		n_dims = number_of_dimensions;
		points = new MDSPoint[number_of_points];
		bare_points = new float[number_of_points][];
		for (int i = 0; i < number_of_points; i++) {
			points[i] = new MDSPoint(number_of_dimensions);
			bare_points[i] = new float[number_of_dimensions];
		}
	}

	public synchronized void resetNumberOfPoints(int newNumberOfPoints) {
		points = new MDSPoint[newNumberOfPoints];
		bare_points = new float[newNumberOfPoints][];
		for (int i = 0; i < newNumberOfPoints; i++) {
			points[i] = new MDSPoint(n_dims);
			bare_points[i] = new float[n_dims];
		}
	}

	public synchronized void setPoints(MDSPoint[] new_points) {
		// At entry (when called by the mds thread) this class is locked and
		// the display window can't access the points.
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < n_dims; j++) {
				points[i].setComponent(j, new_points[i].getComponent(j));
			}
		}
		// At exit, the lock is released (by the mds thread) and getPoints
		// can be called by the main thread (to do display).
	}

	public synchronized void setStress(float stress) {
		this.stress = stress;
	}

	public synchronized float getStress() {
		return stress;
	}

	/** Called by the display/interaction class to get the current embedding */
	public synchronized float[][] getPoints() {
		// At entry (when called by the main thread to get the points for
		// display)
		// a lock is acquired. If the mds thread wants to write these points, it
		// will block until the lock is released.
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < n_dims; j++) {
				bare_points[i][j] = points[i].getComponent(j);
			}
		}
		return bare_points;
		// At exit, the lock held by the display thread is released and the
		// mds thread is free to write to the points again.
	}
}


