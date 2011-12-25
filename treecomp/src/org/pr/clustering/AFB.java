package org.pr.clustering;

/**
 * First on odd & Best on even
 * 
 * @author Ahmad
 */
public class AFB extends IterativeMinimumSquareError {

	public AFB(int k, Vector[] patterns) {
		super(k, patterns, ClusteringAlgorithm.AFB);
	}

	@Override
	protected boolean shouldStop(int m) {
		if (m % 2 == 1) // odd
			return true;
		return false;
	}

}
