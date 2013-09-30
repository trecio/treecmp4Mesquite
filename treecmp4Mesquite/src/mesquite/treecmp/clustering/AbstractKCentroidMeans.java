package mesquite.treecmp.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractKCentroidMeans<TreeType> {	
	public Collection<Collection<Integer>> computeClusters(int numberOfClusters, int numberOfIterations) {
		List<Collection<Integer>> associations = new ArrayList<Collection<Integer>>();
		
		List<TreeType> means = new ArrayList<TreeType>(numberOfClusters);
		
		int[] randomIndices = drawNNumbers(numberOfClusters, getNumberOfTrees()); 
		for (int i=0; i<numberOfClusters; i++)
			means.add(getTree(randomIndices[i]));
		double error = computeAssociations(means, associations), newError;

		int iterations_left = numberOfIterations;
		do {
			List<TreeType> newCenters = computeCentres(associations);
			
			newError = computeAssociations(newCenters, associations);
			
			if (newError < error)
				means = newCenters;
			else
				break;
			
			error = newError;
			
			iterations_left--;
		} while (iterations_left>0);
		
		if (newError > error)
			computeAssociations(means, associations);
		
		return associations;
	}
			
	protected double computeAssociations(List<TreeType> centers,
			List<Collection<Integer>> associations) {
		associations.clear();
		for (int i=0; i<centers.size(); i++)
			associations.add(new ArrayList<Integer>());
		
		double error = Double.MIN_VALUE;
		
		for (int i=0; i<getNumberOfTrees(); i++) {
			int closestCenterIndex = 0;
			double distanceToClosest = getDistanceFromCenterToTree(centers.get(0), getTree(i));
			
			for (int j=1; j<centers.size(); j++) {				
				double distance = getDistanceFromCenterToTree(centers.get(j), getTree(i));
				if (distance < distanceToClosest) {
					distanceToClosest = distance;
					closestCenterIndex = j;
				}
			}
			associations.get(closestCenterIndex).add(i);
			if (distanceToClosest > error)
				error = distanceToClosest;
		}
		return error;
	}

	protected abstract int getNumberOfTrees();
	protected abstract TreeType getTree(int index); 
	protected abstract List<TreeType> computeCentres(List<Collection<Integer>> associations);	
	protected abstract double getDistanceFromCenterToTree(TreeType center, TreeType tree);
	
	private int[] drawNNumbers(int n, int maxValue) {		
		int[] result = new int[n];
		Random r = new Random();
		for (int i=0; i<n; i++) {
			int proposal = r.nextInt(maxValue);
			if (contains(result, proposal))
				i--;
			else
				result[i] = proposal;
		}
		return result;
	}
	
	private boolean contains(int[] array, int value) {
		for (int item : array)
			if (item == value)
				return true;
		return false;
	}
}
