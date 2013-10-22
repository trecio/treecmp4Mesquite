package mesquite.treecmp.clustering.TreeClusteringParameters;


public class ClusterParameters {
	public final int size;
	public final double avgDistance;
	public final double density;
	public final double diameter;
	public final double specificity;
	
	public ClusterParameters(int size, double avgDistance, double diameter, double specificity, double density) {
		this.size = size;
		this.avgDistance = avgDistance;
		this.diameter = diameter;
		this.specificity = specificity;
		this.density = density;
	}
}
