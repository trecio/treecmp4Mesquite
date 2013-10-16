package mesquite.treecmp.clustering.TreeClusteringParameters;

public class ClustersParameters {
	public final double minDistanceBetween;
	public final ClusterParameters[] cluster;
	
	public ClustersParameters(double minDistanceBetween, ClusterParameters[] cluster) {
		this.minDistanceBetween = minDistanceBetween;
		this.cluster = cluster;
	}
}
