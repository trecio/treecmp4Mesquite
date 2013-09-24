package mesquite.treecmp.clustering.HierarchicalTreeClustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.pr.clustering.hierarchical.Cluster;
import org.pr.clustering.hierarchical.Hierarchical;
import org.pr.clustering.hierarchical.LinkageCriterion;

import mesquite.lib.ProgressIndicator;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class HierarchicalTreeClustering extends GroupsForTreeVector {
	private final LinkageCriterion linkageCriterion = LinkageCriterion.COMPLETE;
	private final int numberOfClusters = 5;

	@Override
	public List<Integer> calculateClusters(List<Tree> trees,
			DistanceBetween2Trees distance) {
		final ProgressIndicator progressMeter = new ProgressIndicator(getProject(), "Calculating Tree Differences");
		final double[][] distances = Utils.calculateDistanceMatrix(distance, trees, progressMeter);
		
		final Hierarchical clusteringAlgorithm = new Hierarchical(distances, linkageCriterion);
		clusteringAlgorithm.partition();
		final Collection<Collection<Integer>> partitioning = getResultFromRootCluster(clusteringAlgorithm.getRootCluster());
		return convertToAssignments(trees.size(), partitioning);
	}

	private List<Integer> convertToAssignments(
			int n, Collection<Collection<Integer>> partitioning) {
		final Integer[] results = new Integer[n];
		int partitionNumber = 1;
		for (final Collection<Integer> partition : partitioning) {
			for (final Integer idx : partition) {
				results[idx] = partitionNumber;
			}
			partitionNumber++;
		}
		
		return Arrays.asList(results);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "Hierarchical clustering algorithm.";
	}
	
	private class ClusterComparator implements Comparator<Cluster> {
		public int compare(Cluster arg0, Cluster arg1) {
			if (arg0.distanceBetweenLeftAndRightClusters < arg1.distanceBetweenLeftAndRightClusters) return 1;
			if (arg0.distanceBetweenLeftAndRightClusters > arg1.distanceBetweenLeftAndRightClusters) return -1;
			return 0;
		}		
	}

	private Collection<Collection<Integer>> getResultFromRootCluster(Cluster root) {
		Set<Cluster> resultClusters = new HashSet<Cluster>();
		
		PriorityQueue<Cluster> queue = new PriorityQueue<Cluster>(1, new ClusterComparator());
		
		queue.add(root);		
		resultClusters.add(root);
		
		double maxDistance = root.distanceBetweenLeftAndRightClusters;
		
		while (resultClusters.size() < numberOfClusters || numberOfClusters == -1) {
			Cluster widest = queue.poll();
			Cluster left = widest.left;
			Cluster right = widest.right;
			
			if (numberOfClusters == -1 && 2*widest.distanceBetweenLeftAndRightClusters < maxDistance)
				break;
			
			queue.add(left);
			queue.add(right);
			
			resultClusters.remove(widest);
			resultClusters.add(left);
			resultClusters.add(right);
		}
		
		Collection<Collection<Integer>> result = new ArrayList<Collection<Integer>>(resultClusters.size());
		for (Cluster cluster : resultClusters)
			result.add(cluster.patternIndexes);
		
		return result;
	}
}
