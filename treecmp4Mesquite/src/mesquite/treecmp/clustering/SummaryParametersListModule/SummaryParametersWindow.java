package mesquite.treecmp.clustering.SummaryParametersListModule;

import mesquite.lists.lib.ListModule;
import mesquite.lists.lib.ListWindow;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Row;

public class SummaryParametersWindow extends ListWindow {
	final private ListModule listModule;

	public SummaryParametersWindow(ListModule ownerModule) {
		super(ownerModule);
		listModule = ownerModule;
		ownerModule.getProject().getFrame().removePage(this);
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
	public String getRowName(int i) {
		if (listModule != null) {
			final Row[] rows = (Row[]) listModule.getMainObject();
			return rows[i].name;
		} else {
			return null;
		}
	}

}
