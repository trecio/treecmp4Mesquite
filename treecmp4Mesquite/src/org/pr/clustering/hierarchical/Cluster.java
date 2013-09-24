package org.pr.clustering.hierarchical;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmad
 */
public class Cluster {

	public Cluster left = null;
	public Cluster right = null;
	public List<Integer> patternIndexes;
	
	public double distanceBetweenLeftAndRightClusters;
	
	public String name;
	
	private static int counter = 0;
	
	public Cluster(String name, int patternIndex) {
		this.name = name;
		this.distanceBetweenLeftAndRightClusters = 0;
		patternIndexes = new ArrayList<Integer>();
		patternIndexes.add(patternIndex);
	}
	
	public Cluster(Cluster left, Cluster right, double distanceBetweenLeftAndRightClusters) {
		this.left = left;
		this.right = right;
		// this.name = "[" + left.name + "," + right.name + "]";
		this.name = "" + counter++;
		this.distanceBetweenLeftAndRightClusters = distanceBetweenLeftAndRightClusters;
		
		patternIndexes = new ArrayList<Integer>();
		for (Integer index : left.getPatternIndexes()) {
			patternIndexes.add(index);
		}
		for (Integer index : right.getPatternIndexes()) {
			patternIndexes.add(index);
		}
	}
	
	List<Integer> getPatternIndexes() {
		return patternIndexes;
	}
	
	public void scale(double factor) {
		distanceBetweenLeftAndRightClusters /= factor;
		if(left != null)
			left.scale(factor);
		if(right != null)
			right.scale(factor);
	}
	
	@Override
	public String toString() {
		String leftName = left != null ? left.name : "";
		String rightName = right != null ? right.name : "";
		return this.name + " = [" + leftName + "," + rightName + ": " + distanceBetweenLeftAndRightClusters + "]";
	}
}
