package org.pr.clustering;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmad
 *
 */
public class KMeans extends AbstractPartitioningAlgorithm {

	public KMeans(int k, Vector... patterns) {
		super(k, patterns, ClusteringAlgorithm.KMeans);
	}
	
	/**
	 * returns an array of integers, the ith elements represents the cluster 
	 * index of the ith pattern
	 */
	public List<Integer> partition() {
		// we need to randomize this step
		List<Vector> newZ = new ArrayList<Vector>();
		SecureRandom rand = new SecureRandom();
		List<Integer> usedPatterns = new ArrayList<Integer>();
		for (int i = 0; i < k; i++) {
			Integer pattern;
			do {
				// pattern = rand.nextInt(patterns.length); 
				pattern = (int) (rand.nextDouble() * patterns.length);
			} while (usedPatterns.contains(pattern));
			
			usedPatterns.add(pattern);
			newZ.add(patterns[pattern]);
		}

		List<Vector> oldZ;
		
		for (int m = 0; ; m++) {
			oldZ = newZ;
			
			// for each pattern, find the cluster center that is closest
			// to that pattern, and then put that pattern into that cluster
			int[] clusterIndexes = new int[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				int nearestCluster = patterns[i].getNearestPointIndex(newZ);
				clusterIndexes[i] = nearestCluster;
			}
			mm.moveAllPatterns(clusterIndexes);
			
			newZ = calculateZ();
			
			if (newZ.equals(oldZ))
				break;
		}
		
		cluserCenters = newZ;
		
		return mm.getClusters();	
	}
	
}
