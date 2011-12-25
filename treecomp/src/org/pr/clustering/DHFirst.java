package org.pr.clustering;

public class DHFirst extends IterativeMinimumSquareError {

	public DHFirst(int k, Vector[] patterns) {
		super(k, patterns, ClusteringAlgorithm.DHF);
	}

	@Override
	protected boolean shouldStop(int m) {
		return true;
	}

}
