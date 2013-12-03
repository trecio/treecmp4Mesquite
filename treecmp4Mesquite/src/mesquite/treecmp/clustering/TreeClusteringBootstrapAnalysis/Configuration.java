package mesquite.treecmp.clustering.TreeClusteringBootstrapAnalysis;

class Configuration {

	public final int iterations;
	public final int minClusters;
	public final int maxClusters;

	public Configuration(int iterations, int minClusters, int maxClusters) {
		this.iterations = iterations;
		this.minClusters = minClusters;
		this.maxClusters = maxClusters;
	}

}
