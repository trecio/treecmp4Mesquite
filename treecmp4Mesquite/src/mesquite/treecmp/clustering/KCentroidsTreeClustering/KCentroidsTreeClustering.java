package mesquite.treecmp.clustering.KCentroidsTreeClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.Configure;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.IterativeClustering;
import mesquite.treecmp.clustering.IterativeClusteringAlgorithm;

public class KCentroidsTreeClustering extends GroupsForTreeVector implements IterativeClustering  {

	private int numberOfClusters;
	private int numberOfIterations;

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final double[][] distances = Utils.calculateDistanceMatrix(distance, trees, getProject());
		final KCentroids kCentroids = new KCentroids(distances);
		final IterativeClusteringAlgorithm<Integer> clusteringAlgorithm = new IterativeClusteringAlgorithm<Integer>(kCentroids);
		final List<Integer> treeIndices = range(trees.size());
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters(treeIndices, numberOfClusters, numberOfIterations);
		return Utils.convertToAssignments(trees.size(), clusterAssignments);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return Configure.iterativeClusteringAlgorithm(this, containerOfModule());
	}

	@Override
	public String getName() {
		return "KCentroids algorithm.";
	}

	public void configure(int numberOfClusters, int numberOfIterations) {
		this.numberOfClusters = numberOfClusters;
		this.numberOfIterations = numberOfIterations;
	}

	private List<Integer> range(int size) {
		final List<Integer> list = new ArrayList<Integer>(size);
		for (int i=0; i<size; i++) {
			list.add(i);
		}
		return list;
	}
}
