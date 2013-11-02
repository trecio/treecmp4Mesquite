package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.EmployerEmployee;
import mesquite.lib.Taxa;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lists.lib.ListAssistant;
import mesquite.lists.lib.ListModule;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.SummaryParametersListModule.SummaryParametersListModule;
import mesquite.treecmp.clustering.TreeClustering.TreeClustering;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Column;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.TreeClusteringParametersListAssistant;

public final class TreeClusteringParameters extends ListModule {
	private static final String DOUBLE_FORMAT = "%.3G";
	private Row[] rows;
	
	@Override
	public Class<? extends ListAssistant> getAssistantClass() {
		return TreeClusteringParametersListAssistant.class;
	}

	@Override
	public int getNumberOfRows() {
		return rows.length;
	}

	@Override
	public Object getMainObject() {
		return rows;
	}

	@Override
	public String getItemTypeName() {
		return "cluster";
	}

	@Override
	public String getItemTypeNamePlural() {
		return "clusters";
	}

	@Override
	public String getAnnotation(int row) {
		return null;
	}

	@Override
	public void setAnnotation(int row, String s, boolean notify) {
	}

	@Override
	public boolean deleteRow(int row, boolean notify) {
		return false;
	}

	@Override
	public void showListWindow(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean showing(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private final Column averageDistanceColumn = new Column("Avg. distance", "avgDistance");
	private final Column diameterColumn = new Column("Diameter", "diameter");
	private final Column specificityColumn = new Column("Specificity", "specificity");
	private final Column densityColumn = new Column("Density", "density");
	private final Column separation = new Column("Separation", "separation");
	private final Column sizeColumn = new Column("Size", "size");
	private final Iterable<Column> columnModel = Arrays.asList(sizeColumn, averageDistanceColumn, diameterColumn, specificityColumn, densityColumn, separation);

	private Column summaryColumn = new Column("Value");

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeClustering treeClustering = (TreeClustering) findNearestColleagueWithDuty(TreeClustering.class);
		final Collection<TreeVector> clusters = treeClustering.getClusters();
		final Trees allTrees = treeClustering.allTrees(); 
		final DistanceBetween2Trees distance = treeClustering.getDistance();
		final Taxa taxa = treeClustering.getTaxa();
		final ClustersParameters parameters = TreeClusteringParametersCalculator.getParameters(allTrees, clusters, distance, taxa);
		
		rows = buildRows(parameters);
		final Row[] summaryRows = buildSummaryRows(parameters);		
		final SummaryParametersListModule summaryParametersModule = Utils.hireExactImplementation(this, SummaryParametersListModule.class);
		summaryParametersModule.setMainObject(summaryRows);

		final ClusterParametersWindow window = new ClusterParametersWindow(this, summaryParametersModule);		
		for (final Column column : columnModel) {
			final TreeClusteringParametersListAssistant assistant = getAssistantForColumn(this, column);
			window.addListAssistant(assistant);
		}
		
		final TreeClusteringParametersListAssistant assistant = getAssistantForColumn(summaryParametersModule, summaryColumn);
		window.addSummaryListAssistant(assistant);
		
		window.show();
		return true;
	}

	@Override
	public String getName() {
		return "Tree clustering parameters";
	}
	
	private Row[] buildRows(ClustersParameters parameters) {
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
		minValuesRow.set(separation.field, formatDouble(parameters.separation));
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
		
		return rows;
	}

	private Row[] buildSummaryRows(ClustersParameters parameters) {
		final Row[] rows = new Row[] {
			new Row("K-L distance", formatDouble(parameters.informationLoss.KL)),
			new Row("L1 norm", formatDouble(parameters.informationLoss.L1)),
			new Row("L2 norm", formatDouble(parameters.informationLoss.L2)),
			new Row("L-inf norm", formatDouble(parameters.informationLoss.Linf))
		};
		
		return rows;
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
	
	private TreeClusteringParametersListAssistant getAssistantForColumn(
			final EmployerEmployee me,
			final Column column) {
		final TreeClusteringParametersListAssistant assistant = Utils.hireExactImplementation(me, TreeClusteringParametersListAssistant.class);
		assistant.setColumnModel(column);
		return assistant;
	}
}
