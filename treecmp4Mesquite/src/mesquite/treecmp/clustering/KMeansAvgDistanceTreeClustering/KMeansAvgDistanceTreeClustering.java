package mesquite.treecmp.clustering.KMeansAvgDistanceTreeClustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.Configure;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.IterativeClustering;

public class KMeansAvgDistanceTreeClustering extends GroupsForTreeVector implements IterativeClustering {

	private int numberOfIterations;
	private int numberOfClusters;

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final KMeansAvg clusteringAlgorithm = new KMeansAvg(trees, distance);
		List<Collection<Integer>> treeLists = generateLists(trees.size());
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters(treeLists , numberOfClusters, numberOfIterations);
		return Utils.convertToAssignments(trees.size(), clusterAssignments);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return Configure.iterativeClusteringAlgorithm(this, containerOfModule());
	}

	@Override
	public String getName() {
		return "KMeans centre as average algorithm.";
	}

	public void configure(int numberOfClusters, int numberOfIterations) {
		this.numberOfClusters = numberOfClusters;
		this.numberOfIterations = numberOfIterations;
	}

	private List<Collection<Integer>> generateLists(int size) {
		List<Collection<Integer>> list = new ArrayList<Collection<Integer>>(size);
		for (int i=0; i<size; i++) {
			list.add(Arrays.asList(i));
		}
		return list ;
	}
}
