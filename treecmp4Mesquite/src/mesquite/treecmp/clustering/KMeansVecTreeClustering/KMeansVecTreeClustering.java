package mesquite.treecmp.clustering.KMeansVecTreeClustering;

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

public class KMeansVecTreeClustering extends GroupsForTreeVector implements IterativeClustering {
	private int numberOfClusters;
	private int numberOfIterations;

	public void configure(int numberOfClusters, int numberOfIterations) {
		this.numberOfClusters = numberOfClusters;
		this.numberOfIterations = numberOfIterations;
	}

	@Override
	public List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance) {
		final List<Bipartitions> listOfBipartitions = getBipartitions(trees);
		final KMeansVec kmeans = new KMeansVec(listOfBipartitions);
		final IterativeClusteringAlgorithm<Bipartitions> clusteringAlgorithm = new IterativeClusteringAlgorithm<Bipartitions>(kmeans);
		final Collection<Collection<Integer>> clusterAssignments = clusteringAlgorithm.computeClusters(listOfBipartitions , numberOfClusters, numberOfIterations);
		return Utils.convertToAssignments(trees.size(), clusterAssignments);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return Configure.iterativeClusteringAlgorithm(this, containerOfModule());
	}

	@Override
	public String getName() {
		return "KMeans on biparition vectors algorithm.";
	}

	private List<Bipartitions> getBipartitions(Trees trees) {
		final int numberOfTrees = trees.size();
		final List<Bipartitions> bipartitions = new ArrayList<Bipartitions>(numberOfTrees);
		for (int i=0; i<numberOfTrees; i++) {
			final Bipartitions bipartition = new Bipartitions(trees.getTree(i));
			bipartitions.add(bipartition);
		}
		return bipartitions;
	}
}
