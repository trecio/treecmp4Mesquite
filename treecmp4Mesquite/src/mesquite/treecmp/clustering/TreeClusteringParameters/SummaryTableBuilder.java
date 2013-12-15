package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mesquite.treecmp.Utils;

public class SummaryTableBuilder {
	private Column summaryColumn = new Column("Value");
	private Column sampleNumberColumn = new Column("Sample", "sampleNumber", "Number of the sample");
	private final List<Row> rows = new ArrayList<Row>();
	private final boolean showSampleNumber;
	private int sampleNumber = 1;
	
	public SummaryTableBuilder() {
		this(false);
	}
	
	public SummaryTableBuilder(boolean showSampleNumber) {
		this.showSampleNumber = showSampleNumber;
	}
	
	public SummaryTableBuilder add(ClustersParameters parameters) {
		rows.add(createRow("Separation", Utils.formatDouble(parameters.separation), "Minimum distance between two trees belonging to different clusters."));
		rows.add(createRow("Average distance between", Utils.formatDouble(parameters.avgDistanceBetween), "Average distance between two trees belonging to different clusters."));
		rows.add(createRow("K-L distance", Utils.formatDouble(parameters.informationLoss.KL), "Kullback-Leibler (KL) distance of cluster's and tree set uniform distributions."));
		rows.add(createRow("L1 norm", Utils.formatDouble(parameters.informationLoss.L1), "L1 distance of cluster's and tree set uniform distributions."));
		rows.add(createRow("L2 norm", Utils.formatDouble(parameters.informationLoss.L2), "L2 distance of cluster's and tree set uniform distributions."));
		rows.add(createRow("L-inf norm", Utils.formatDouble(parameters.informationLoss.Linf), "Linf distance of cluster's and tree set uniform distributions."));

		sampleNumber += 1;
		return this;
	}

	public Table getTable() {		
		final List<Column> columnModel = showSampleNumber 
				? Arrays.asList(sampleNumberColumn, summaryColumn)
				: Arrays.asList(summaryColumn);
		return new Table(columnModel , rows.toArray(new Row[0]));
	}

	private Row createRow(String name, String value, String explanation) {
		final Row row = new Row(name, explanation);
		row.set(summaryColumn.field, value);
		if (showSampleNumber) {
			row.set(sampleNumberColumn.field, Integer.toString(sampleNumber));
		}
		return row;
	}

}
