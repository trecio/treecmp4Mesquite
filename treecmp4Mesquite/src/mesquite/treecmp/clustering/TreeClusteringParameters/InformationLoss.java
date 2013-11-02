package mesquite.treecmp.clustering.TreeClusteringParameters;

public class InformationLoss {
	public final double Linf;
	public final double L1;
	public final double L2;
	public final double KL;
	
	public InformationLoss(double Linf, double L1, double L2, double KL) {
		this.Linf = Linf;
		this.L1 = L1;
		this.L2 = L2;
		this.KL = KL;
	}
}
