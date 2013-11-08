package mesquite.treecmp.clustering.KMeansTreeClustering;

import java.util.Collection;
import java.util.List;

import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.Configure;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.IterativeClustering;

public class KMeansTreeClustering extends GroupsForTreeVector implements IterativeClustering {
	private int numberOfClusters;
	private int numberOfIterations;

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final KMeans clusteringAlgorithm = new KMeans(trees, distance);
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters(numberOfClusters, numberOfIterations);
		return Utils.convertToAssignments(trees.size(), clusterAssignments);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return Configure.iterativeClusteringAlgorithm(this, containerOfModule());
	}

	@Override
	public String getName() {
		return "KMeans centre as consensus algorithm.";
	}

	public void configure(int numberOfClusters, int numberOfIterations) {
		this.numberOfClusters = numberOfClusters;
		this.numberOfIterations = numberOfIterations;
	}

}
