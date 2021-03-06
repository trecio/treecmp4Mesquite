package mesquite.treecmp.clustering.KCentroidsTreeClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.treecmp.clustering.ClusterCentresCalculation;

class KCentroids implements ClusterCentresCalculation<Integer> {
	public KCentroids(double[][] matrix) {
		this.distances = matrix;
	}
	
	public List<Integer> computeCentres(List<Collection<Integer>> associations) {
		List<Integer> centers = new ArrayList<Integer>(associations.size());
		
		for (int i=0; i<associations.size(); i++) {
			double distance = Double.MAX_VALUE;
			int bestChoice = 0;
			
			for (int centerId=0; centerId<distances.length; centerId++) {
				double maxDistance = Double.MIN_VALUE;
				
				for (int treeId : associations.get(i)) {
					double currentDistance = getDistanceFromCenterToTree(centerId, treeId);
					if (maxDistance < currentDistance)
						maxDistance = currentDistance;					
				}
				
				if (maxDistance < distance) {
					distance = maxDistance;
					bestChoice = centerId;
				}
			}			
			centers.add(bestChoice);			
		}
		
		return centers;
	}	

	public double getDistanceFromCenterToTree(Integer centerIndex, Integer treeIndex) {
		return distances[centerIndex][treeIndex];
	}

	private final double[][] distances;
}
