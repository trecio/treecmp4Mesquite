package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.treecomp.TreeSetVisualizationV2.DiffMatrix;

public class KCentroids extends AbstractKCentroidMeans<Integer> implements IClusteringAlgorithm {
	@Override
	public Collection<Collection<Integer>> computeClusters(DiffMatrix matrix) {
		distances = matrix;
		return super.computeClusters(matrix);
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
		return distances.getElement(centerIndex, treeIndex);
	}

	@Override
	protected int getNumberOfTrees() {
		return distances.getNumberOfItems();
	}

	@Override
	protected Integer getTree(int index) {
		return index;
	}

	private DiffMatrix distances;
}
