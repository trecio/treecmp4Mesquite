package org.pr.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmad
 *
 */
public abstract class AbstractPartitioningAlgorithm extends AbstractClusteringAlgorithm {

	int k;
	Vector[] patterns;
	HardPartitioningMembershipMatrix mm;
	
	List<Vector> cluserCenters;

	private ClusteringAlgorithm type;
	
	protected int[] clusterSizes;
	
	protected StoppingCondition stoppingCondition = StoppingCondition.REACHED_STOPPING_CRITRIA;
	
	public AbstractPartitioningAlgorithm(int k, Vector[] patterns, ClusteringAlgorithm type) {
		this.k = k;
		this.patterns = patterns;
		mm = new HardPartitioningMembershipMatrix(patterns.length, k);

		clusterSizes = new int[k];
		for (int i = 0; i < patterns.length; i++) {
			int clusterIndex = mm.getClusterForPattern(i);
			clusterSizes[clusterIndex]++;
		}
		
		this.type = type;
	}
	
	public PatternMembership[] getClusteringResult() {
		PatternMembership[] patternMemberships = new PatternMembership[patterns.length];
		
		for (int i = 0; i < patterns.length; i++) {
			patternMemberships[i] = 
				new PatternMembership(patterns[i], mm.getClusterForPattern(i));
		}
		
		return patternMemberships;
	}
	
	public double getObjectiveFunction() {
		double globalError = 0;
		for (int i = 0; i < k; i++) {
			double clusterError = getClusterMeanSquareError(i, cluserCenters);
			globalError += clusterError;
		}
		
		return globalError;
	}
	
	public ClusteringAlgorithm getType() {
		return type;
	}
	
	//
	// Utility Methods
	//

	/**
	 * calculates cluster centers
	 * @return
	 */
	protected List<Vector> calculateZ() {
		List<Vector> z = new ArrayList<Vector>();
		for (int j = 0; j < k; j++) {
			int[] patternIndexes = mm.getPatternsForCluster(j);
			z.add(Vector.calculateCenter(getPatternsWithIndexes(patternIndexes)));
		}
		
		return z;
	}
	
	protected void updateZ(List<Vector> Z, int... changedClusters) {
		for (int i = 0; i < changedClusters.length; i++) {
			Z.remove(changedClusters[i]);
			Z.add(i, calculateClusterCenter(i));
		}
	}
	
	protected Vector calculateClusterCenter(int clusterIndex) {
		int[] patternIndexes = mm.getPatternsForCluster(clusterIndex);
		return Vector.calculateCenter(getPatternsWithIndexes(patternIndexes));
	}
	
	protected Vector[] getPatternsWithIndexes(int[] indexes) {
		Vector[] patterns = new Vector[indexes.length];
		for (int i = 0; i < patterns.length; i++) {
			patterns[i] = this.patterns[indexes[i]];
		}
		
		return patterns;
	}
	
	protected double getObjectiveFunction(List<Vector> Z) {
		double globalError = 0;
		for (int i = 0; i < k; i++) {
			double clusterError = getClusterMeanSquareError(i, Z);
			globalError += clusterError;
		}
		
		return globalError;
	}
	
	protected double getClusterMeanSquareError(int clusterIndex, List<Vector> Z) {
		int[] clusterPatterns = mm.getPatternsForCluster(clusterIndex);
		
		// foreach pattern in the cluster, calculate its distance
		// from the cluster center
		double clusterError = 0;
		Vector clusterCenter = Z.get(clusterIndex);
		for (int j = 0; j < clusterPatterns.length; j++) {
			double distance = Vector.euclideanDistance(patterns[clusterPatterns[j]], clusterCenter);
			clusterError += (distance * distance);
		}
		
		return clusterError;
	}
	
	public String printResults() {
		StringBuilder sb = new StringBuilder("");
		sb.append("pattern \t\t cluster" + "\n");
		
		List<Integer> clusters = mm.getClusters();
		for (int i = 0; i < patterns.length; i++) {
			sb.append(patterns[i] + "\t" + (clusters.get(i) + 1) + "\n");
		}
		
		return sb.toString();
	}
	
	public StoppingCondition getStoppingCondition() {
		return stoppingCondition;
	}
	
	public enum StoppingCondition {
		REACHED_STOPPING_CRITRIA,
		ABOVE_MAX_ITERATIONS
	}
	
}
