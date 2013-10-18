package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

import mesquite.lib.table.MesquiteTable;
import mesquite.lists.lib.ListAssistant;

public class TreeClusteringParametersListAssistant extends ListAssistant {
	private Row[] rows;
	private Column columnModel;

	@Override
	public void setTableAndObject(MesquiteTable table, Object object) {
		rows = (Row[]) object;
	}

	@Override
	public String getStringForRow(int i) {
		return columnModel.getString(rows[i]);
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

	public void setColumnModel(Column column) {
		columnModel = column;
	}
}