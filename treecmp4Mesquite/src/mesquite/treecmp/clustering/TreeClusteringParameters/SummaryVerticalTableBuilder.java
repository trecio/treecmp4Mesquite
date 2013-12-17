package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mesquite.treecmp.Utils;

public class SummaryVerticalTableBuilder {
	private List<Row> rows = new ArrayList<Row>();
	

	public void add(ClustersParameters parameters) {
		final Row row = new Row("");
		row.set(SummaryTableColumns.separation, Utils.formatDouble(parameters.separation));
		row.set(SummaryTableColumns.avgDistanceBetween, Utils.formatDouble(parameters.avgDistanceBetween));
		row.set(SummaryTableColumns.klDistance, Utils.formatDouble(parameters.informationLoss.KL));
		row.set(SummaryTableColumns.l1Norm, Utils.formatDouble(parameters.informationLoss.L1));
		row.set(SummaryTableColumns.l2Norm, Utils.formatDouble(parameters.informationLoss.L2));
		row.set(SummaryTableColumns.lInfNorm, Utils.formatDouble(parameters.informationLoss.Linf));
		
		rows.add(row);
	}

	public Table getTable() {
		final List<Column> columnModel = Arrays.asList(
				SummaryTableColumns.separation,
				SummaryTableColumns.avgDistanceBetween,
				SummaryTableColumns.klDistance,
				SummaryTableColumns.l1Norm,
				SummaryTableColumns.l2Norm,
				SummaryTableColumns.lInfNorm);
		return new Table(columnModel, rows.toArray(new Row[0]));
	}

}
