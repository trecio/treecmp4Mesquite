package mesquite.treecmp.clustering;

import java.awt.Choice;
import java.util.ArrayList;

import org.pr.clustering.hierarchical.LinkageCriterion;

import mesquite.lib.ExtensibleDialog;
import mesquite.lib.IntegerField;
import mesquite.lib.MesquiteInteger;

class AglomerativeClusteringOptionsDialog extends ExtensibleDialog {
	private static final String DIALOG_TITLE = "Clustering options";
	
	private final IntegerField numberOfClustersField;
	private final Choice linkageMethodField;

	public AglomerativeClusteringOptionsDialog(Object parent, MesquiteInteger buttonPressed) {
		super(parent, DIALOG_TITLE, buttonPressed);
		
		numberOfClustersField = addIntegerField("Number of clusters", Default.NUMBER_OF_CLUSTERS, 10);
		AwtUtils.numericTextFieldBehaviour(numberOfClustersField.getTextField());
		
		final ArrayList<String> linkageCriteriaNames = new ArrayList<String>();
		for (LinkageCriterion linkageCriterion : LinkageCriterion.values())
			linkageCriteriaNames.add(linkageCriterion.name());
		String[] linkageCriteria = linkageCriteriaNames.toArray(new String[0]);
		linkageMethodField = addPopUpMenu("Linkage criterion", linkageCriteria, 0);
	}
	
	public int getNumbeOfClusters() {
		return numberOfClustersField.getValue();
	}
	
	public LinkageCriterion getLinkageCriterion() {
		return LinkageCriterion.valueOf(linkageMethodField.getSelectedItem());
	}
}
