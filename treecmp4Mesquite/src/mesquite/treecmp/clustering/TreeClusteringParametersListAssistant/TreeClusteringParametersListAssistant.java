package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

import mesquite.lib.table.MesquiteTable;
import mesquite.lists.lib.ListAssistant;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClusterParameters;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClustersParameters;

public class TreeClusteringParametersListAssistant extends ListAssistant {
	private ClustersParameters clustersParameters;
	private Column<ClusterParameters> columnModel;

	@Override
	public void setTableAndObject(MesquiteTable table, Object object) {
		clustersParameters = (ClustersParameters)object;
	}

	@Override
	public String getStringForRow(int i) {
		return columnModel.valueAccessor.get(clustersParameters.cluster[i]).toString();
	}

	@Override
	public String getTitle() {
		return columnModel.title;
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return columnModel != null
				? columnModel.title
				: "Tree clustering parameter.";
	}

	@Override
	public String getWidestString() {
		return "88888888";
	}

	@Override
	public boolean canHireMoreThanOnce() {
		return true;
	}

	public void setColumnModel(Column<ClusterParameters> column) {
		columnModel = column;
	}
}