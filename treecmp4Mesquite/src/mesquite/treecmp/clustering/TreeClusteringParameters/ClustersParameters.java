package mesquite.treecmp.clustering.TreeClusteringParameters;

public class ClustersParameters {
	public final double separation;
	public final ClusterParameters[] cluster;
	public final ClusterParameters allTrees;
	
	public ClustersParameters(double separation, ClusterParameters[] cluster, ClusterParameters all) {
		this.separation = separation;
		this.cluster = cluster;
		this.allTrees = all;
	}
}
