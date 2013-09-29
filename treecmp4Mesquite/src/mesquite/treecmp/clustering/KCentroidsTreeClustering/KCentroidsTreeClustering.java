package mesquite.treecmp.clustering.KCentroidsTreeClustering;

import java.util.Collection;
import java.util.List;

import mesquite.lib.ProgressIndicator;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class KCentroidsTreeClustering extends GroupsForTreeVector {

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final ProgressIndicator progressMeter = new ProgressIndicator(getProject(), "Calculating Tree Differences");
		final double[][] distances = Utils.calculateDistanceMatrix(distance, trees, progressMeter);
		final KCentroids clusteringAlgorithm = new KCentroids(distances);
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters();
		return Utils.convertToAssignments(trees.size(), clusterAssignments);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "KCentroids clustering algorithm.";
	}

}
