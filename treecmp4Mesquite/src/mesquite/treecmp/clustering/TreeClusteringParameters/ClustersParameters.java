package mesquite.treecmp.clustering.TreeClusteringParameters;

public class ClustersParameters {
	public final double separation;
	public final ClusterParameters[] cluster;
	
	public ClustersParameters(double separation, ClusterParameters[] cluster) {
		this.separation = separation;
		this.cluster = cluster;
	}
}
