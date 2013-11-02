package mesquite.treecmp.clustering.SummaryParametersListModule;

import mesquite.lists.lib.ListModule;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.TreeClusteringParametersListAssistant;

public class SummaryParametersListModule extends ListModule {

	private Row[] rows;

	@Override
	public Class<?> getAssistantClass() {
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
		return "parameter";
	}

	@Override
	public String getItemTypeNamePlural() {
		return "parameters";
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
	}

	@Override
	public boolean showing(Object obj) {
		return false;
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "Tree clustering summary parameters"; 
	}

	public void setMainObject(Row[] rows) {
		this.rows = rows;
	}

}
