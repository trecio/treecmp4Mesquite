package org.pr.clustering;

/**
 * Best on odd & First on even
 * 
 * @author Ahmad
 */
public class ABF extends IterativeMinimumSquareError {

	public ABF(int k, Vector[] patterns) {
		super(k, patterns, ClusteringAlgorithm.ABF);
	}

	@Override
	protected boolean shouldStop(int m) {
		if (m % 2 == 1) // odd
			return false;
		return true;
	}

}
