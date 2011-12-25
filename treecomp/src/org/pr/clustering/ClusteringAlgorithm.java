package org.pr.clustering;

import java.util.ArrayList;
import java.util.List;

public enum ClusteringAlgorithm {
		
	// partitioning
	KMeans("K-means"), 
	DHF("DHF"), 
	DHB("DHB"), 
	AFB("AFB"),
	ABF("ABF"), 
	
	// hierarchical
	SINGLE("SINGLE"),
	COMPLETE("COMPLETE"),
	UPGMA("UPGMA"),
	WPGMA("WPGMA"),
	UPGMC("UPGMC"),
	WPGMC("WPGMC"),
	Ward("Ward"),
	
	FuzzyKMeans("Fuzzy K-means"),
	SoftKMeans("Soft K-means");
	
	String name;
	
	ClusteringAlgorithm(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static String[] getAllAlgorithms() {
		List<String> algorithms = new ArrayList<String>();
		for (int i = 0; i < ClusteringAlgorithm.values().length; i++) {
			algorithms.add(ClusteringAlgorithm.values()[i].name);
		}
		return algorithms.toArray(new String[algorithms.size()]);
	}
	
	public static ClusteringAlgorithm create(String name) {
		for (int i = 0; i < ClusteringAlgorithm.values().length; i++) {
			if (ClusteringAlgorithm.values()[i].name.equals(name))
				return ClusteringAlgorithm.values()[i];
		}
		
		return KMeans;
	}

}
