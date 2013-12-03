package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mesquite.treecmp.Utils;

public class SummaryTableBuilder {
	private Column summaryColumn = new Column("Value");
	private final List<Row> rows = new ArrayList<Row>();

	public SummaryTableBuilder add(ClustersParameters parameters) {
		rows.add(new Row("Separation", Utils.formatDouble(parameters.separation), "Minimum distance between two trees belonging to different clusters."));
		rows.add(new Row("Average distance between", Utils.formatDouble(parameters.avgDistanceBetween), "Average distance between two trees belonging to different clusters."));
		rows.add(new Row("K-L distance", Utils.formatDouble(parameters.informationLoss.KL), "Kullback-Leibler (KL) distance of cluster's and tree set uniform distributions."));
		rows.add(new Row("L1 norm", Utils.formatDouble(parameters.informationLoss.L1), "L1 distance of cluster's and tree set uniform distributions."));
		rows.add(new Row("L2 norm", Utils.formatDouble(parameters.informationLoss.L2), "L2 distance of cluster's and tree set uniform distributions."));
		rows.add(new Row("L-inf norm", Utils.formatDouble(parameters.informationLoss.Linf), "Linf distance of cluster's and tree set uniform distributions."));

		return this;
	}

	public Table getTable() {
		return new Table(Arrays.asList(summaryColumn), rows.toArray(new Row[0]));
	}

}
