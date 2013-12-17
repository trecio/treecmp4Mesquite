package mesquite.treecmp.clustering.TreeClusteringParameters;

public final class SummaryTableColumns {
	private SummaryTableColumns() { }
	
	public static final Column separation = new Column("Separation", "separation", "Minimum distance between two trees belonging to different clusters.");
	public static final Column avgDistanceBetween = new Column("Average distance between", "avgDistanceBetween", "Average distance between two trees belonging to different clusters.");
	public static final Column klDistance = new Column("K-L distance", "klDistance", "Kullback-Leibler (KL) distance of cluster's and tree set uniform distributions.");
	public static final Column l1Norm = new Column("L1 norm", "l1Norm", "L1 distance of cluster's and tree set uniform distributions.");
	public static final Column l2Norm = new Column("L2 norm", "l2Norm", "L2 distance of cluster's and tree set uniform distributions.");
	public static final Column lInfNorm = new Column("L-inf norm", "lInfNorm", "Linf distance of cluster's and tree set uniform distributions.");
}
