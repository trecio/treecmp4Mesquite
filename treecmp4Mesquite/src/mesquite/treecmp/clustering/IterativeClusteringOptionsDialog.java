package mesquite.treecmp.clustering;

import mesquite.lib.ExtensibleDialog;
import mesquite.lib.IntegerField;
import mesquite.lib.MesquiteInteger;

class IterativeClusteringOptionsDialog extends ExtensibleDialog {
	private static final String DIALOG_TITLE = "Clustering options";
	private static final String HELP_STRING = "Please set up clustering options:"; 

	private final IntegerField numberOfClustersField;
	private final IntegerField numberOfIterationsField;
	
	public IterativeClusteringOptionsDialog(Object parent, MesquiteInteger buttonPressed) {
		super(parent, DIALOG_TITLE, buttonPressed);
		
		numberOfClustersField = addIntegerField("Number of clusters", Default.NUMBER_OF_CLUSTERS, 10);
		AwtUtils.numericTextFieldBehaviour(numberOfClustersField.getTextField());
		numberOfIterationsField = addIntegerField("Number of iterations", Default.NUMBER_OF_ITERATIONS, 10);
		AwtUtils.numericTextFieldBehaviour(numberOfIterationsField.getTextField());
		
		appendToHelpString(HELP_STRING);
	}
	
	public int getNumberOfClusters() {
		return numberOfClustersField.getValue();
	}
	
	public int getNumberOfIterations() {
		return numberOfIterationsField.getValue();
	}
}
