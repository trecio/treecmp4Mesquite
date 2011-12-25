package org.pr.clustering;

import java.util.List;

import org.pr.clustering.util.DoubleUtils;

public abstract class IterativeMinimumSquareError extends AbstractPartitioningAlgorithm {
	
	public static int MAX_ITERATIONS = 10000;
	
	public IterativeMinimumSquareError(int k, Vector[] patterns, ClusteringAlgorithm type) {
		super(k, patterns, type);
	}
	
	@Override
	public List<Integer> partition() {
		List<Vector> Z = calculateZ();
		
		Vector newMovedToCenter;
		Vector newOriginalCenter;
		
		double newGlobalObjFun = getObjectiveFunction(Z);
		double oldGlobalObjFun;
		int numberOfStableIterations = 0;
		int m = 0;
		for (; m < MAX_ITERATIONS; m++) {
		// for (int m = 0; ; m++) {
			oldGlobalObjFun = newGlobalObjFun;
			
			// for each pattern, find its cluster
			// then calc the gain of moving it out of that cluster
			// and the cost of moving it to any of the other clusters
			for (int l = 0; l < patterns.length; l++) {
				int originalCluster = mm.getClusterForPattern(l);
				if (clusterSizes[originalCluster] > 1) {
					double gain = (((double)clusterSizes[originalCluster]) / (double)(clusterSizes[originalCluster] - 1))
						* Vector.euclideanDistance(patterns[l], Z.get(originalCluster));
					
					double minCost = Double.MAX_VALUE;
					int clusterToMoveTo = -1;
					// search for a cluster to move this pattern to
					// to decrease the objective function
					for (int j = 0; j < k; j++) {
						if (j != originalCluster) {// exclude ith cluster that we are trying to take a pattern from
							double cost = (((double)clusterSizes[j]) / (double)(clusterSizes[j] + 1))
								* Vector.euclideanDistance(patterns[l], Z.get(j));
							if (cost < gain) { // there's an improvement, but is this is the best improvement
								if (cost < minCost) { // better than all before
									minCost = cost;
									clusterToMoveTo = j;
								}
								if (shouldStop(m)) {
									break;
								}
							}
						}
					}
					if (clusterToMoveTo > -1) { // move pattern i to cluster clusterToMoveTo
						mm.movePattern(l, clusterToMoveTo);
						
						newGlobalObjFun += minCost - gain;
						
						// we need to update the centers with min effort
						{
							Vector originalCenter = Z.get(originalCluster);
							double[] values = new double[originalCenter.getDimensionCount()];
							for (int i = 0; i < originalCenter.getDimensionCount(); i++) {
								values[i] 
								    = (originalCenter.values[i] * (double)clusterSizes[originalCluster] - patterns[l].values[i]) 
									    / (double)(clusterSizes[originalCluster] - 1); 
							}
							
							newOriginalCenter = new Vector(values);
							Z.remove(originalCluster);
							Z.add(originalCluster, newOriginalCenter);
						}
						
						{
							Vector movedToCenter = Z.get(clusterToMoveTo);
							double[] values = new double[movedToCenter.getDimensionCount()];
							for (int i = 0; i < movedToCenter.getDimensionCount(); i++) {
								values[i] 
								    = (movedToCenter.values[i] * (double)clusterSizes[clusterToMoveTo] + patterns[l].values[i]) 
									    / (double)(clusterSizes[clusterToMoveTo] + 1); 
							}
							
							newMovedToCenter = new Vector(values);
							Z.remove(clusterToMoveTo);
							Z.add(clusterToMoveTo, newMovedToCenter);
						}
						 
						// update cluster sizes
						clusterSizes[originalCluster]--;
						clusterSizes[clusterToMoveTo]++;
					}
				}
			}
			
			// newGlobalObjFun = getObjectiveFunction(Z);
			
			// if the objective function is the same
			// advance stability count
			if (DoubleUtils.equal(newGlobalObjFun, oldGlobalObjFun)) {
				numberOfStableIterations++;
				if (numberOfStableIterations == patterns.length) { // we're done
				// if (numberOfStableIterations == 1) { // we're done
					break;
				}
			} else
				numberOfStableIterations = 0; // reset, stability has been broken
		}
		
		cluserCenters = Z;

		if (m == MAX_ITERATIONS)
			stoppingCondition = StoppingCondition.ABOVE_MAX_ITERATIONS;
		
		return mm.getClusters();	
	}
	
	/**
	 * Returns true when the implementing class wants to execute the core DHF, 
	 * and false if the implementing class wants to execute the core DHF.
	 */
	protected abstract boolean shouldStop(int m);
	
}
