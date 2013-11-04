package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.awt.BorderLayout;

import mesquite.lib.MousePanel;
import mesquite.lists.lib.ListModule;
import mesquite.lists.lib.ListWindow;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;

class ClusterParametersWindow extends ListWindow {
	final private ListModule listModule;
	private TableView summaryTable;

	public ClusterParametersWindow(ListModule ownerModule, Table summaryParametersTable) {
		super(ownerModule);
		listModule = ownerModule;		
		
		MousePanel sidePanel = new MousePanel();
		summaryTable = new TableView(summaryParametersTable, 100, getHeight(), 50, this);
		sidePanel.setLayout(new BorderLayout());
		sidePanel.add("Center", summaryTable);
		addSidePanel(sidePanel , 100);		
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
