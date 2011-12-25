package org.pr.clustering;

public class DHBest extends IterativeMinimumSquareError {

	public DHBest(int k, Vector[] patterns) {
		super(k, patterns, ClusteringAlgorithm.DHB);
	}
	
	@Override
	protected boolean shouldStop(int m) {
		return false;
	}

}
