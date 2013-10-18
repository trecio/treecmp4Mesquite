package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lists.lib.ListModule;
import mesquite.lists.lib.ListWindow;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;

class ClusterParametersWindow extends ListWindow {
	final private ListModule listModule;

	public ClusterParametersWindow(ListModule ownerModule) {
		super(ownerModule);
		listModule = ownerModule;
	}

	@Override
	public void resetTitle() {
		setTitle("Cluster parameters");
	}

	@Override
	public void setRowName(int row, String name) {
	}

	@Override
	public String getRowName(int i) {
		if (listModule != null) {
			final Row[] rows = (Row[]) listModule.getMainObject();
			return rows[i].name;
		} else {
			return null;
		}
	}	
}
