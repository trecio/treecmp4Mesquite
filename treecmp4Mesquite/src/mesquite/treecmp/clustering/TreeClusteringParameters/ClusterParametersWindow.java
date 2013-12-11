package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.awt.Dimension;

import mesquite.lib.MesquiteCommand;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteTool;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.MousePanel;
import mesquite.lib.table.MesquiteTable;
import mesquite.lib.table.TableTool;
import mesquite.treecmp.clustering.AwtUtils;

public class ClusterParametersWindow extends MesquiteWindow {
	private final MesquiteTool arrowTool;
	private final TableView mainTableView;
	private final TableView summaryTableView;
	private TableView currentTable = null;
	private static final String SELECT_EXPLANATION = "Select items in table.";

	public ClusterParametersWindow(MesquiteModule ownerModule, Table mainTable, Table summaryParametersTable) {
		super(ownerModule, true);		
		setShowExplanation(true);

		mainTableView = new TableView(mainTable, getWidth(), getHeight(), 100, this);
		mainTableView.setColumnWidthsUniform(100);
		mainTableView.onTableTouched(new SetCurrentTable(mainTableView));
		AwtUtils.fillComponent(getGraphicsArea(), mainTableView);
		
		final MousePanel sidePanel = new MousePanel();
		summaryTableView = new TableView(summaryParametersTable, 200, getHeight(), 50, this);
		summaryTableView.setColumnWidthsUniform(100);
		summaryTableView.onTableTouched(new SetCurrentTable(summaryTableView));
		AwtUtils.fillComponent(sidePanel, summaryTableView);
		
		arrowTool = new TableTool(this, "arrow", MesquiteModule.getRootImageDirectoryPath(), "arrow.gif", 4, 2, "Select", SELECT_EXPLANATION , null, null, null);

		addSidePanel(sidePanel, 200);
		
		toFront();
		resetMenus();
	}
	
	@Override
	public void resetTitle() {
		setTitle("Cluster parameters");
	}
	
	@Override
	public MesquiteTool getCurrentTool() {
		return arrowTool;
	}

	@Override
	public void copyGraphicsPanel() {
		if (currentTable != null) {
			MesquiteCommand copyCommand = currentTable.getCopyCommand();
			copyCommand.doItMainThread("", null, this);
		}
	}
	
	class SetCurrentTable implements Runnable {
		private final TableView target;

		public SetCurrentTable(TableView target) {
			this.target = target;
		}

		public void run() {
			currentTable = target;
		}

	}
}