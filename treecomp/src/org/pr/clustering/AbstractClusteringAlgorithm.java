package org.pr.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.pr.clustering.hierarchical.Hierarchical;
import org.pr.clustering.hierarchical.LinkageCriterion;
import org.pr.clustering.soft.FuzzyCMeans;
import org.pr.clustering.soft.SoftKMeans;

public abstract class AbstractClusteringAlgorithm {
	
	public abstract List<Integer> partition();
	
	public abstract String printResults();
	
	public static Vector[] loadPatterns(String filename, String delimiter, boolean lastColumnIsLable) {
		Vector[] patterns = null;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			List<Vector> patternList = new ArrayList<Vector>();
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				String[] strValues = line.split(delimiter);
				int dimCount = lastColumnIsLable
					? strValues.length - 1
					: strValues.length;
				double[] values = new double[dimCount];
				for (int i = 0; i < dimCount; i++) {
					values[i] = Double.valueOf(strValues[i]);
				}
				patternList.add(new Vector(values));
			}
			patterns = new Vector[patternList.size()];
			for (int i = 0; i < patternList.size(); i++) {
				patterns[i] = patternList.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		return patterns;
	}
	
	//
	// Factory
	//
	
	public static class Factory {
		public static AbstractClusteringAlgorithm create
			(ClusteringAlgorithm clusteringAlgorithm, 
			int k,
			double m,
			double alpha,
			Vector... patterns) {
			
			if (clusteringAlgorithm.equals(ClusteringAlgorithm.KMeans))
				return new KMeans(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.DHB))
				return new DHBest(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.DHF))
				return new DHFirst(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.ABF))
				return new ABF(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.AFB))
				return new AFB(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.SINGLE))
				return new Hierarchical(patterns, LinkageCriterion.SINGLE);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.COMPLETE))
				return new Hierarchical(patterns, LinkageCriterion.COMPLETE);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.UPGMA))
				return new Hierarchical(patterns, LinkageCriterion.UPGMA);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.WPGMA))
				return new Hierarchical(patterns, LinkageCriterion.WPGMA);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.UPGMC))
				return new Hierarchical(patterns, LinkageCriterion.UPGMC);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.WPGMC))
				return new Hierarchical(patterns, LinkageCriterion.WPGMC);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.Ward))
				return new Hierarchical(patterns, LinkageCriterion.Ward);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.FuzzyKMeans))
				return new FuzzyCMeans(patterns, k, m);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.Ward))
				return new SoftKMeans(patterns, k, m, alpha);
			else
				throw new IllegalArgumentException
					("Algorithm " + clusteringAlgorithm.getName() + " is not supported (yet)");
			
		}
		
		public static AbstractPartitioningAlgorithm createHardPartitioningAlgorithm
			(ClusteringAlgorithm clusteringAlgorithm, 
			int k,
			Vector... patterns) {
		
			if (clusteringAlgorithm.equals(ClusteringAlgorithm.KMeans))
				return new KMeans(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.DHB))
				return new DHBest(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.DHF))
				return new DHFirst(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.ABF))
				return new ABF(k, patterns);
			else if (clusteringAlgorithm.equals(ClusteringAlgorithm.AFB))
				return new AFB(k, patterns);
			else
				throw new IllegalArgumentException
					("Algorithm " + clusteringAlgorithm.getName() + " is not supported (yet)");
		}
		
		public static Hierarchical createHierachhicalAlgorithm
			(LinkageCriterion linkageCriterion,
			Vector... patterns) {
		
			return new Hierarchical(patterns, linkageCriterion);
		}
		
		public static AbstractClusteringAlgorithm create
			(ClusteringAlgorithm clusteringAlgorithm, 
			int k,
			double m,
			double alpha,
			String filename, String delimiter, boolean lastColumnIsLable) 
			throws IllegalArgumentException {
			
			Vector[] patterns = loadPatterns(filename, delimiter, lastColumnIsLable);
			return create(clusteringAlgorithm, k, m, alpha, patterns);
		}
		
	}
	
}
