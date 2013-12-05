package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.MousePanel;
import mesquite.treecmp.clustering.AwtUtils;

public class ClusterParametersWindow extends MesquiteWindow {
	public ClusterParametersWindow(MesquiteModule ownerModule, Table mainTable, Table summaryParametersTable) {
		super(ownerModule, true);		
		setShowExplanation(true);

		final TableView mainTableView = new TableView(mainTable, getWidth(), getHeight(), 100, this);
		mainTableView.setColumnWidthsUniform(100);
		AwtUtils.fillComponent(getGraphicsArea(), mainTableView);
		
		final MousePanel sidePanel = new MousePanel();
		final TableView summaryTableView = new TableView(summaryParametersTable, 200, getHeight(), 50, this);
		summaryTableView.setColumnWidthsUniform(100);
		AwtUtils.fillComponent(sidePanel, summaryTableView);

		addSidePanel(sidePanel, 200);
		
		toFront();
	}
	
	@Override
	public void resetTitle() {
		setTitle("Cluster parameters");
	}
}