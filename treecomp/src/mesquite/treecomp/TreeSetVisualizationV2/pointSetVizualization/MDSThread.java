package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import mesquite.treecomp.TreeSetVisualizationV2.mds.MDS;

public class MDSThread extends Thread {
	/** how many mds iterations occure between each re-centering check */
	private static final int CENTERING_CHECK_PERIOD = 15; // translational
															// drifting
															// shouldn't really
															// be a problem
	
	/**
	 * set by MDSWindow in response to a press of the "Stop" button. If set, the
	 * thread will call wait() on itself to block until notified. Whoever sets
	 * this to true needs to reset it to false before notification.
	 */
	public boolean waitFlag;
	/**
	 * set by MDSWindow during exit and cleanup. Causes thread to return and
	 * die.
	 */
	public boolean exitFlag;

	/** the object in which the calculations happen */
	private MDS mds;
	/** for communication with the display and user interface thread */
	private SharedPoints sharedPoints;
	/** the object handling user interface and embedding display */
	private PointSetVisualization mdsWindow;

	public MDSThread(MDS mds, SharedPoints sharedPoints,
			PointSetVisualization mdsWindow) {
		super();
		this.mds = mds;
		this.sharedPoints = sharedPoints;
		this.mdsWindow = mdsWindow;
		waitFlag = false;
		exitFlag = false;
	}

	/** Main processing method for the MDSThread object */
	public void run() {
		int centering_check_counter = 0;

		while (true) {// loop forever. Thread will terminate when exitFlag is
						// set by MDSWindow
			try {
				// sleeping is a good idea inside any hard loop
				sleep(5); // milliseconds

				if (waitFlag) {
					synchronized (this) {// required by the language to avoid
											// race conditions on wait/notify
						while (waitFlag) {// while loop so that if we are
											// notified by something else
											// (unexpected), we won't resume
							wait();// the MDS thread suspends itself
						}
					}
				}
			} catch (InterruptedException e) { // can get interrupted during a
												// wait() call.
				// An interrupt is unexpected. Should be woken instead by a call
				// to notify()
				System.out
						.println("MDS thread wait() was interrupted!  What happened?!");
			}
			// If notify() is called at a point where waitFlag is false,
			// execution resumes here.

			// Check for exit. Do this right after wait() so that if the user
			// wants to exit while
			// the MDS thread is suspended, no useless computation will be done
			// before this thread
			// dies.
			if (exitFlag) {
				return;// stop execution, ending the life of this MDSThread
			}

			// Do a computational step.
			mds.doOneIteration();

			// Every CENTERING_CHECK_PERIOD iterations, recenter (recentering
			// only has an effect if
			// the embedding has drifted beyond a given threshold.
			centering_check_counter = (centering_check_counter + 1)
					% CENTERING_CHECK_PERIOD;
			if (centering_check_counter == 0) {
				mds.center_embedding();
			}

			// Compute the stress of the current embedding
			/*
			 * Stress calculation is now integrated into the main
			 * doOneIteration() method
			 */
			// mds.compute_stress();

			// Write the results to the shared data area
			sharedPoints.setPoints(mds.getEmbedding());
			sharedPoints.setStress(mds.getStress());

			// Tell the main window that an iteration has been completed.
			// This will induce a redraw of the embedding display and the stress
			// display
			mdsWindow.mds_iteration_complete();

			// System.out.println("MDS thread ran an iteration. Priority = " +
			// Thread.currentThread().getPriority());
		}
	}
}