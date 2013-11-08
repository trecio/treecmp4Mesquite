package mesquite.treecmp.clustering.KMeansTreeClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.lib.Tree;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.Configure;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.IterativeClustering;
import mesquite.treecmp.clustering.IterativeClusteringAlgorithm;

public class KMeansTreeClustering extends GroupsForTreeVector implements IterativeClustering {
	private int numberOfClusters;
	private int numberOfIterations;

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final KMeans kmeans = new KMeans(trees, distance);
		final IterativeClusteringAlgorithm<Tree> clusteringAlgorithm = new IterativeClusteringAlgorithm<Tree>(kmeans);
		final List<Tree> listOfTrees = getTreeList(trees);
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters(listOfTrees , numberOfClusters, numberOfIterations);
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
	
	private List<Tree> getTreeList(Trees trees) {
		final int numberOfTrees = trees.size(); 
		final List<Tree> list = new ArrayList<Tree>(numberOfTrees);
		for (int i=0; i<numberOfTrees; i++) {
			list.add(trees.getTree(i));
		}
		return list;
	}
}
