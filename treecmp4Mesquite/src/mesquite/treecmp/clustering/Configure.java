package mesquite.treecmp.clustering;

import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteThread;
import mesquite.lib.MesquiteWindow;

public final class Configure {
	private Configure() {}
	
	public static boolean aglomerativeClusteringAlgorithm(AgglomerativeClustering algorithm, MesquiteWindow parentWindow) {
		if (!MesquiteThread.isScripting()) {
			final MesquiteInteger buttonPressed = new MesquiteInteger(AgglomerativeClusteringOptionsDialog.defaultCANCEL);
			final AgglomerativeClusteringOptionsDialog optionsDialog = new AgglomerativeClusteringOptionsDialog(parentWindow, buttonPressed);
			try {
				optionsDialog.completeAndShowDialog(true);
				if (buttonPressed.getValue() == AgglomerativeClusteringOptionsDialog.defaultOK) {
					algorithm.configure(optionsDialog.getNumbeOfClusters(), optionsDialog.getLinkageCriterion());
					return true;
				}
			} finally {
				optionsDialog.dispose();
			}
		}
		return false;
	}
	
	public static boolean iterativeClusteringAlgorithm(IterativeClustering algorithm, MesquiteWindow parentWindow) {
		if (!MesquiteThread.isScripting()) {
			final MesquiteInteger buttonPressed = new MesquiteInteger(IterativeClusteringOptionsDialog.defaultCANCEL);
			final IterativeClusteringOptionsDialog optionsDialog = new IterativeClusteringOptionsDialog(parentWindow, buttonPressed);
			try {
				optionsDialog.completeAndShowDialog(true);
				if (buttonPressed.getValue() == IterativeClusteringOptionsDialog.defaultOK) {
					algorithm.configure(optionsDialog.getNumberOfClusters(), optionsDialog.getNumberOfIterations());
					return true;
				}
			} finally {
				optionsDialog.dispose();
			}
		}
		return false;
	}
}
