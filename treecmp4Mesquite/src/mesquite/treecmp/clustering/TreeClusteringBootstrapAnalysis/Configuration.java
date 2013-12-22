package mesquite.treecmp.clustering.TreeClusteringBootstrapAnalysis;

class Configuration {
	public final int iterations;
	public final int minClusters;
	public final int maxClusters;
	public final boolean dump;

	public Configuration(int iterations, int minClusters, int maxClusters, boolean dump) {
		this.iterations = iterations;
		this.minClusters = minClusters;
		this.maxClusters = maxClusters;
		this.dump = dump;
	}

}
