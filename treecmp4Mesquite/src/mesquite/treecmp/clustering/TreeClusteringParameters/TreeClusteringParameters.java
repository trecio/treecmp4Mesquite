package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.TreeVector;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lists.lib.ListAssistant;
import mesquite.lists.lib.ListModule;
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
	
	private final Column diameterColumn = new Column("Diameter", "diameter");
	private final Column specificityColumn = new Column("Specificity", "specificity");
	private final Column densityColumn = new Column("Density", "density");
	private final Column separation = new Column("Separation", "separation");
	private final Column sizeColumn = new Column("Size", "size");
	private final Iterable<Column> columnModel = Arrays.asList(sizeColumn, diameterColumn, specificityColumn, densityColumn, separation);

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeClustering treeClustering = (TreeClustering) findNearestColleagueWithDuty(TreeClustering.class);
		final Collection<TreeVector> clusters = treeClustering.getClusters();
		DistanceBetween2Trees distance = treeClustering.getDistance();
		final ClustersParameters parameters = TreeClusteringParametersCalculator.getParameters(clusters, distance);
		rows = buildRows(parameters);
		final ClusterParametersWindow window = new ClusterParametersWindow(this);
		
		final String assistantName = '#' + TreeClusteringParametersListAssistant.class.getSimpleName();
		for (final Column column : columnModel) {
			final TreeClusteringParametersListAssistant assistant = (TreeClusteringParametersListAssistant) hireNamedEmployee(TreeClusteringParametersListAssistant.class, assistantName);
			assistant.setColumnModel(column);
			window.addListAssistant(assistant);
		}
		window.show();
		return true;
	}

	private Row[] buildRows(ClustersParameters parameters) {
		final int EXTRA_ROWS = 2;
		final int NUMBER_OF_CLUSTERS = parameters.cluster.length; 
		final Row[] rows = new Row[NUMBER_OF_CLUSTERS+EXTRA_ROWS];
		double maxDiameter = 0;
		double minDensity = Double.MAX_VALUE;
		double minSpecificity = Double.MAX_VALUE;
		
		for (int i=0; i<parameters.cluster.length; i++) {
			final ClusterParameters clusterParameters = parameters.cluster[i];
			final Row row = new Row("Cluster " + i + ":");

			maxDiameter = Math.max(maxDiameter, clusterParameters.diameter);
			minDensity = Math.min(minDensity, clusterParameters.density);
			minSpecificity = Math.min(minSpecificity, clusterParameters.specificity);

			row.set(densityColumn.field, String.format(DOUBLE_FORMAT, clusterParameters.density));
			row.set(diameterColumn.field, String.format(DOUBLE_FORMAT, clusterParameters.diameter));
			row.set(sizeColumn.field, Integer.toString(clusterParameters.size));
			row.set(specificityColumn.field, String.format(DOUBLE_FORMAT, clusterParameters.specificity));			
			rows[i] = row;
		}
		
		final Row minValuesRow = new Row("Minimum: ");
		minValuesRow.set(densityColumn.field, String.format(DOUBLE_FORMAT, minDensity));
		minValuesRow.set(separation.field, String.format(DOUBLE_FORMAT, parameters.separation));
		minValuesRow.set(specificityColumn.field, String.format(DOUBLE_FORMAT, minSpecificity));
		rows[NUMBER_OF_CLUSTERS] = minValuesRow;
		
		final Row maxValuesRow = new Row("Maximum: ");
		maxValuesRow.set(diameterColumn.field, String.format(DOUBLE_FORMAT, maxDiameter));
		rows[NUMBER_OF_CLUSTERS+1] = maxValuesRow;
		
		return rows;
	}

	@Override
	public String getName() {
		return "Tree clustering parameters";
	}
	
	
}
