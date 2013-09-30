package mesquite.treecmp.clustering.KCentroidsTreeClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.treecmp.clustering.AbstractKCentroidMeans;

class KCentroids extends AbstractKCentroidMeans<Integer> {
	public KCentroids(double[][] matrix) {
		this.distances = matrix;
	}
	
	@Override
	protected List<Integer> computeCentres(List<Collection<Integer>> associations) {
		List<Integer> centers = new ArrayList<Integer>(associations.size());
		
		for (int i=0; i<associations.size(); i++) {
			double distance = Double.MAX_VALUE;
			int bestChoice = 0;
			
			for (int centerId=0; centerId<getNumberOfTrees(); centerId++) {
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
			centers.add(getTree(bestChoice));			
		}
		
		return centers;
	}	

	@Override
	protected double getDistanceFromCenterToTree(Integer centerIndex, Integer treeIndex) {
		return distances[centerIndex][treeIndex];
	}

	@Override
	protected int getNumberOfTrees() {
		return distances.length;
	}

	@Override
	protected Integer getTree(int index) {
		return index;
	}

	private final double[][] distances;
}
