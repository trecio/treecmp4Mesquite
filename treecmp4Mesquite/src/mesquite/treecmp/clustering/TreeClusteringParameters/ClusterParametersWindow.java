package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lists.lib.ListModule;
import mesquite.lists.lib.ListWindow;

public class ClusterParametersWindow extends ListWindow {
	final private ListModule listModule;

	public ClusterParametersWindow(ListModule ownerModule) {
		super(ownerModule);
		listModule = ownerModule;
	}

	@Override
	public void resetTitle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRowName(int row, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRowName(int row) {
		if (listModule != null) {
			final ClustersParameters parameters = (ClustersParameters) listModule.getMainObject();
			final ClusterParameters cluster = parameters.cluster[row];
			return "Cluster " + row + ", size: " + cluster.size;
		} else {
			return null;
		}
	}	
}
