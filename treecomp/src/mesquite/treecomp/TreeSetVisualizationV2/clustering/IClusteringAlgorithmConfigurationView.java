package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import mesquite.treecomp.common.AwtUtils;

import org.pr.clustering.hierarchical.LinkageCriterion;

public interface IClusteringAlgorithmConfigurationView {
	public Object getConfiguration();
}

class HierarchicalClusteringConfigurationView extends Panel implements IClusteringAlgorithmConfigurationView {
	public HierarchicalClusteringConfigurationView() {
		setLayout(new FlowLayout());
		
		add(new Label("Linkage criterion: "));
		chLinkageCriterion = new Choice();
		for (LinkageCriterion linkageCriterion : LinkageCriterion.values())
			chLinkageCriterion.add(linkageCriterion.name());
		
		add(chLinkageCriterion);
		chLinkageCriterion.select(LinkageCriterion.COMPLETE.name());
	}

	public Object getConfiguration() {	
		return Enum.valueOf(LinkageCriterion.class, chLinkageCriterion.getSelectedItem());
	}
	
	private Choice chLinkageCriterion;
}

class IterativeClusteringConfigurationView extends Panel implements IClusteringAlgorithmConfigurationView {
	
	public IterativeClusteringConfigurationView() {
		add(new Label("Number of iterations:"));
		
		tfIterations = AwtUtils.NumericTextFieldBehaviour(new TextField(10));
		
		tfIterations.setText(""+DEFAULT_ITERATIONS);
		
		add(tfIterations);
	}

	public Object getConfiguration() {
		return Integer.parseInt(tfIterations.getText());
	}
	
	private TextField tfIterations;
	
	private final int DEFAULT_ITERATIONS = 100;	

}
