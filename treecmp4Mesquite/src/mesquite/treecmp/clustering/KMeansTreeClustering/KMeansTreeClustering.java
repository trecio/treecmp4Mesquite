package mesquite.treecmp.clustering.KMeansTreeClustering;

import java.util.Collection;
import java.util.List;

import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class KMeansTreeClustering extends GroupsForTreeVector {

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final KMeans clusteringAlgorithm = new KMeans(trees, distance);
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
		return "KMeans clustering algorithm.";
	}

}
