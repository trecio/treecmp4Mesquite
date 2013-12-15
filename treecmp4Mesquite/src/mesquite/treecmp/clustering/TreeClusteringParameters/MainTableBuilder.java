package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mesquite.treecmp.Utils;

public class MainTableBuilder {
	private final Column averageDistanceColumn = new Column("Avg. distance", "avgDistance", "Average distance between trees in a cluster.");
	private final Column diameterColumn = new Column("Diameter", "diameter", "Maximum distance between trees in a cluster.");
	private final Column specificityColumn = new Column("Specificity", "specificity", "Normalized number of internal edges of the cluster's strict consensus tree.");
	private final Column densityColumn = new Column("Density", "density", "Number of unique tree topologies in the cluster divided by the number of all trees compatible with the cluster's strict consensus.");
	private final Column sizeColumn = new Column("Size", "size", "Number of trees belonging to a cluster.");
	private final Column sampleNumberColumn = new Column("Sample number", "sampleNumber", "Number of sample");
	private final List<Column> mainColumnModel = Arrays.asList(sizeColumn, averageDistanceColumn, diameterColumn, specificityColumn, densityColumn);
	private final ArrayList<Row> rows = new ArrayList<Row>();
	
	private final boolean showSummaryRows;
	private final boolean showSampleNumber;
	
	private int sampleNumber = 1;
	
	public MainTableBuilder() {
		this(true, false);
	}
	
	public MainTableBuilder(boolean showSummaryRows, boolean showSampleNumber) {
		this.showSummaryRows = showSummaryRows;
		this.showSampleNumber = showSampleNumber;
	}

	public MainTableBuilder add(ClustersParameters parameters) {
		final int numberOfTrees = parameters.allTrees.size;
		double maxAvgDistance = 0;
		double maxDiameter = 0;
		double minDensity = Double.MAX_VALUE;
		double minSpecificity = Double.MAX_VALUE;
		double wtdAvgDistance = 0;
		double wtdDensity = 0;
		double wtdDiameter = 0;
		double wtdSpecificity = 0;
		
		int i;
		
		for (i=0; i<parameters.cluster.length; i++) {
			final ClusterParameters clusterParameters = parameters.cluster[i];

			maxAvgDistance = Math.max(maxAvgDistance, clusterParameters.avgDistance);
			maxDiameter = Math.max(maxDiameter, clusterParameters.diameter);
			minDensity = Math.min(minDensity, clusterParameters.density);
			minSpecificity = Math.min(minSpecificity, clusterParameters.specificity);
			wtdAvgDistance += clusterParameters.avgDistance * clusterParameters.size;
			wtdDensity += clusterParameters.density * clusterParameters.size;
			wtdDiameter += clusterParameters.diameter * clusterParameters.size;
			wtdSpecificity += clusterParameters.specificity * clusterParameters.size;

			rows.add(createRow("Cluster " + i + ":", clusterParameters));
		}
		
		if (showSummaryRows) {		
			final Row minValuesRow = new Row("Minimum: ");
			minValuesRow.set(densityColumn.field, Utils.formatDouble(minDensity));
			minValuesRow.set(specificityColumn.field, Utils.formatDouble(minSpecificity));
			rows.add(minValuesRow);
			
			final Row maxValuesRow = new Row("Maximum: ");
			maxValuesRow.set(averageDistanceColumn.field, Utils.formatDouble(maxAvgDistance));
			maxValuesRow.set(diameterColumn.field, Utils.formatDouble(maxDiameter));
			rows.add(maxValuesRow);
			
			final Row weightedAveragesRow = new Row("Weighted avg: ");
			weightedAveragesRow.set(averageDistanceColumn.field, Utils.formatDouble(wtdAvgDistance / numberOfTrees));
			weightedAveragesRow.set(diameterColumn.field, Utils.formatDouble(wtdDiameter / numberOfTrees));
			weightedAveragesRow.set(densityColumn.field, Utils.formatDouble(wtdDensity / numberOfTrees));
			weightedAveragesRow.set(specificityColumn.field, Utils.formatDouble(wtdSpecificity / numberOfTrees));
			rows.add(weightedAveragesRow);
			
			rows.add(new Row(""));
			rows.add(createRow("All trees:", parameters.allTrees));
		}
		
		sampleNumber += 1;
		
		return this;
	}

	public Table getTable() {		
		final List<Column> columnModel;
		if (showSampleNumber) {
			columnModel = new ArrayList<Column>(Arrays.asList(sampleNumberColumn));
			columnModel.addAll(mainColumnModel);
		} else {
			columnModel = mainColumnModel;
		}
		return new Table(columnModel, rows.toArray(new Row[0]));
	}

	private Row createRow(String name, final ClusterParameters clusterParameters) {
		final Row row = new Row(name);
		row.set(averageDistanceColumn.field, Utils.formatDouble(clusterParameters.avgDistance));
		row.set(densityColumn.field, Utils.formatDouble(clusterParameters.density));
		row.set(diameterColumn.field, Utils.formatDouble(clusterParameters.diameter));
		row.set(sizeColumn.field, Integer.toString(clusterParameters.size));
		row.set(specificityColumn.field, Utils.formatDouble(clusterParameters.specificity));
		if (showSampleNumber) {
			row.set(sampleNumberColumn.field, Integer.toString(sampleNumber));
		}
		return row;
	}
}
