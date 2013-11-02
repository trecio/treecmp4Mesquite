package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.awt.BorderLayout;

import mesquite.lib.MousePanel;
import mesquite.lists.lib.ListModule;
import mesquite.lists.lib.ListWindow;
import mesquite.treecmp.clustering.SummaryParametersListModule.SummaryParametersListModule;
import mesquite.treecmp.clustering.SummaryParametersListModule.SummaryParametersWindow;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.TreeClusteringParametersListAssistant;

class ClusterParametersWindow extends ListWindow {
	final private ListModule listModule;
	private SummaryParametersWindow summaryTable;

	public ClusterParametersWindow(ListModule ownerModule, SummaryParametersListModule summaryParametersModule) {
		super(ownerModule);
		listModule = ownerModule;
		table.setCellDimmed(2, 2, true);		
		
		MousePanel sidePanel = new MousePanel();
		summaryTable = new SummaryParametersWindow(summaryParametersModule);
		sidePanel.setLayout(new BorderLayout());
		sidePanel.add("Center", summaryTable.getTable());
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

	public void addSummaryListAssistant(
			TreeClusteringParametersListAssistant assistant) {
		summaryTable.addListAssistant(assistant);
	}	
}
