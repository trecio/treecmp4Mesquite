package mesquite.treecmp.AsymmetricYuleSimulatedTrees;

import mesquite.lib.DoubleField;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;

class AsymmetricYuleOptionsDialog extends ExtensibleDialog {
	private static final String DIALOG_TITLE = "Parametrized Yule tree generation options";
	private final DoubleField imbalanceField;
	private final DoubleField totalTreeDepthField;

	public AsymmetricYuleOptionsDialog(Object parent, MesquiteInteger buttonPressed) {
		super(parent, DIALOG_TITLE, buttonPressed);
		
		imbalanceField = addDoubleField("Imbalance [-1;1]", 0, 10, -1, 1);
		totalTreeDepthField = addDoubleField("Total tree depth:", 10, 1, 0, MesquiteDouble.infinite);
	}
	
	public double getImbalance() {
		return imbalanceField.getValue();
	}
	
	public double getTotalTreeDepth() {
		return totalTreeDepthField.getValue();
	}
}
