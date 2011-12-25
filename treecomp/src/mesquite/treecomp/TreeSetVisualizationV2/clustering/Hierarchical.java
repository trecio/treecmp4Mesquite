package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import mesquite.treecomp.TreeSetVisualizationV2.DiffMatrix;

import org.pr.clustering.hierarchical.Cluster;
import org.pr.clustering.hierarchical.LinkageCriterion;

public class Hierarchical implements IClusteringAlgorithm {
	
	private int numberOfClusters;
	public int getNumberOfClusters() {
		return numberOfClusters;
	}
	public void setNumberOfClusters(int noClusters) {
		this.numberOfClusters = noClusters;
	}
	
	private LinkageCriterion linkage;
	public LinkageCriterion getLinkage() {
		return linkage;
	}
	public void setLinkage(LinkageCriterion linkage) {
		this.linkage = linkage;
	}

	public Collection<Collection<Integer>> computeClusters(DiffMatrix matrix) {
		org.pr.clustering.hierarchical.Hierarchical algorithm = new org.pr.clustering.hierarchical.Hierarchical(matrix, getLinkage());		
		
		algorithm.partition();
		
		return getResultFromRootCluster(algorithm.getRootCluster());
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
