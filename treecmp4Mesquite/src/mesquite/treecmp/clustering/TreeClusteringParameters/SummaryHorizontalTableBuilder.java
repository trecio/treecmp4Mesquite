package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mesquite.treecmp.Utils;

public class SummaryHorizontalTableBuilder {
	private Column summaryColumn = new Column("Value");
	private final List<Column> columnModel = Arrays.asList(summaryColumn); 
	private final List<Row> rows = new ArrayList<Row>();
	
	public SummaryHorizontalTableBuilder add(ClustersParameters parameters) {
		rows.add(createRow(SummaryTableColumns.separation, parameters.separation));
		rows.add(createRow(SummaryTableColumns.avgDistanceBetween, parameters.avgDistanceBetween));
		rows.add(createRow(SummaryTableColumns.klDistance, parameters.informationLoss.KL));
		rows.add(createRow(SummaryTableColumns.l1Norm, parameters.informationLoss.L1));
		rows.add(createRow(SummaryTableColumns.l2Norm, parameters.informationLoss.L2));
		rows.add(createRow(SummaryTableColumns.lInfNorm, parameters.informationLoss.Linf));

		return this;
	}

	private Row createRow(Column field, double value) {
		final Row row = new Row(field.title, field.explanation);
		final String valueCaption = Utils.formatDouble(value);
		row.set(summaryColumn.field, valueCaption);
		return row;
	}

	public Table getTable() {
		return new Table(columnModel, rows.toArray(new Row[0]));
	}
}
