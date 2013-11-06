package mesquite.treecmp.clustering.TreeClusteringParameters;

public class ClustersParameters {
	public final double avgDistanceBetween; 
	public final double separation;
	public final ClusterParameters[] cluster;
	public final ClusterParameters allTrees;
	public final InformationLoss informationLoss;
	
	public ClustersParameters(double avgDistanceBetween, double separation, ClusterParameters[] cluster, ClusterParameters all, InformationLoss informationLoss) {
		this.avgDistanceBetween = avgDistanceBetween;
		this.separation = separation;
		this.cluster = cluster;
		this.allTrees = all;
		this.informationLoss = informationLoss;
	}
}
