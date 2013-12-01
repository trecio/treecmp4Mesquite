package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mesquite.lib.MesquiteModule;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.clustering.TreeClustering.TreeClustering;

public final class TreeClusteringParameters extends MesquiteModule {	
	private static final String DOUBLE_FORMAT = "%.3G";
		
	private final Column averageDistanceColumn = new Column("Avg. distance", "avgDistance", "Average distance between trees in a cluster.");
	private final Column diameterColumn = new Column("Diameter", "diameter", "Maximum distance between trees in a cluster.");
	private final Column specificityColumn = new Column("Specificity", "specificity", "Normalized number of internal edges of the cluster's strict consensus tree.");
	private final Column densityColumn = new Column("Density", "density", "Number of unique tree topologies in the cluster divided by the number of all trees compatible with the cluster's strict consensus.");
	private final Column sizeColumn = new Column("Size", "size", "Number of trees belonging to a cluster.");
	private final List<Column> columnModel = Arrays.asList(sizeColumn, averageDistanceColumn, diameterColumn, specificityColumn, densityColumn);

	private Column summaryColumn = new Column("Value");

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeClustering treeClustering = (TreeClustering) findNearestColleagueWithDuty(TreeClustering.class);
		final Collection<TreeVector> clusters = treeClustering.getClusters();
		final Trees allTrees = treeClustering.allTrees(); 
		final DistanceBetween2Trees distance = treeClustering.getDistance();
		final ClustersParameters parameters = TreeClusteringParametersCalculator.getParameters(allTrees, clusters, distance);
		
		final Table mainTable = buildRows(parameters);
		final Table summaryTable = buildSummaryRows(parameters);		

		final ClusterParametersWindow window = new ClusterParametersWindow(this, mainTable, summaryTable);		
		window.show();	
		return true;
	}

	@Override
	public Class<?> getDutyClass() {
		return TreeClusteringParameters.class;
	}

	@Override
	public String getName() {
		return "Tree clustering parameters";
	}
	
	private Table buildRows(ClustersParameters parameters) {
		final int EXTRA_ROWS = 5;
		final int NUMBER_OF_CLUSTERS = parameters.cluster.length; 
		final Row[] rows = new Row[NUMBER_OF_CLUSTERS+EXTRA_ROWS];
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

			rows[i] = createRow("Cluster " + i + ":", clusterParameters);
		}
		
		final Row minValuesRow = new Row("Minimum: ");
		minValuesRow.set(densityColumn.field, formatDouble(minDensity));
		minValuesRow.set(specificityColumn.field, formatDouble(minSpecificity));
		rows[i++] = minValuesRow;
		
		final Row maxValuesRow = new Row("Maximum: ");
		maxValuesRow.set(averageDistanceColumn.field, formatDouble(maxAvgDistance));
		maxValuesRow.set(diameterColumn.field, formatDouble(maxDiameter));
		rows[i++] = maxValuesRow;
		
		final Row weightedAveragesRow = new Row("Weighted avg: ");
		weightedAveragesRow.set(averageDistanceColumn.field, formatDouble(wtdAvgDistance / numberOfTrees));
		weightedAveragesRow.set(diameterColumn.field, formatDouble(wtdDiameter / numberOfTrees));
		weightedAveragesRow.set(densityColumn.field, formatDouble(wtdDensity / numberOfTrees));
		weightedAveragesRow.set(specificityColumn.field, formatDouble(wtdSpecificity / numberOfTrees));
		rows[i++] = weightedAveragesRow;
		
		rows[i++] = new Row("");
		rows[i++] = createRow("All trees:", parameters.allTrees);
		
		return new Table(columnModel, rows);
	}

	private Table buildSummaryRows(ClustersParameters parameters) {
		final Row[] rows = new Row[] {
			new Row("Separation", formatDouble(parameters.separation), "Minimum distance between two trees belonging to different clusters."),
			new Row("Average distance between", formatDouble(parameters.avgDistanceBetween), "Average distance between two trees belonging to different clusters."),
			new Row("K-L distance", formatDouble(parameters.informationLoss.KL), "Kullback-Leibler (KL) distance of cluster's and tree set uniform distributions."),
			new Row("L1 norm", formatDouble(parameters.informationLoss.L1), "L1 distance of cluster's and tree set uniform distributions."),
			new Row("L2 norm", formatDouble(parameters.informationLoss.L2), "L2 distance of cluster's and tree set uniform distributions."),
			new Row("L-inf norm", formatDouble(parameters.informationLoss.Linf), "Linf distance of cluster's and tree set uniform distributions.")
		};
		
		return new Table(Arrays.asList(summaryColumn), rows);
	}

	private Row createRow(String name, final ClusterParameters clusterParameters) {
		final Row row = new Row(name);
		row.set(averageDistanceColumn.field, formatDouble(clusterParameters.avgDistance));
		row.set(densityColumn.field, formatDouble(clusterParameters.density));
		row.set(diameterColumn.field, formatDouble(clusterParameters.diameter));
		row.set(sizeColumn.field, Integer.toString(clusterParameters.size));
		row.set(specificityColumn.field, formatDouble(clusterParameters.specificity));
		return row;
	}
	
	private static String formatDouble(double value) {
		return String.format(DOUBLE_FORMAT, value);
	}
}
