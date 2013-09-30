package mesquite.treecmp.clustering;

import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteThread;

public class Configure {
	public static boolean aglomerativeClusteringAlgorithm(AglomerativeClustering algorithm, Object parentWindow) {
		if (!MesquiteThread.isScripting()) {
			final MesquiteInteger buttonPressed = new MesquiteInteger(AglomerativeClusteringOptionsDialog.defaultCANCEL);
			final AglomerativeClusteringOptionsDialog optionsDialog = new AglomerativeClusteringOptionsDialog(parentWindow, buttonPressed);
			try {
				optionsDialog.completeAndShowDialog(true);
				if (buttonPressed.getValue() == AglomerativeClusteringOptionsDialog.defaultOK) {
					algorithm.configure(optionsDialog.getNumbeOfClusters(), optionsDialog.getLinkageCriterion());
					return true;
				}
			} finally {
				optionsDialog.dispose();
			}
		}
		return false;
	}
	
	public static boolean iterativeClusteringAlgorithm(IterativeClustering algorithm, Object parentWindow) {
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
