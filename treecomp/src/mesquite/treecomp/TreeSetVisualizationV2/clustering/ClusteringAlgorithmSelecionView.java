package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import mesquite.treecomp.common.AwtUtils;

public class ClusteringAlgorithmSelecionView extends Dialog {
	public static final Class<?> HIERARCHICAL = Hierarchical.class;
	public static final Class<?> KCENTROIDS = KCentroids.class;
	public static final Class<?> KMEANS = KMeans.class;	
	
	private static final List<Class<?>> ALGORITHM_CLASSES = Arrays.asList(
			HIERARCHICAL,
			KCENTROIDS,
			KMEANS
			);
	
	private Class<?> clusteringAlgorithm;
	public Class<?> getClusteringAlgorithm() {
		return clusteringAlgorithm;
	}
	private int numberOfClusters;
	public int getNumberOfClusters() {
		return numberOfClusters;
	}
	
	private IClusteringAlgorithmConfigurationView algorithmConfigurationView;
	public Object getAlgorithmConfiguration() {
		if (algorithmConfigurationView == null) 
			return null;
		return algorithmConfigurationView.getConfiguration();
	}
	
	public ClusteringAlgorithmSelecionView(Window w) {
		super(w);
		setModal(true);
		setTitle("Select clustering algorithm");
	
		Panel topPanel = new Panel();
		topPanel.add(new Label("Clustering algorithm: "));		
		algorithmChoice = new Choice();
		for (Class<?> clazz : ALGORITHM_CLASSES)
			algorithmChoice.addItem(clazz.getSimpleName());
		algorithmChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateAlgorithConfigurationView();
				pack();
			}
		});
		topPanel.add(algorithmChoice);		
		add(topPanel, BorderLayout.NORTH);
		
		Panel centerPanel = new Panel();
		centerPanel.setLayout(new GridLayout(0, 1));
		
		Panel numberOfClustersPanel = new Panel();
		Label label = new Label("Number of clusters:");
		numberOfClustersText = new TextField(10);
		numberOfClustersText = AwtUtils.NumericTextFieldBehaviour(numberOfClustersText);
		numberOfClustersText.setEnabled(false);
		defaultClusters = new Checkbox("Default");
		defaultClusters.addItemListener(new ItemListener() {			
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1)
					numberOfClustersText.setEnabled(false);
				else
					numberOfClustersText.setEnabled(true);
			}
		});
		defaultClusters.setState(true);
		numberOfClustersPanel.add(label);
		numberOfClustersPanel.add(defaultClusters);
		numberOfClustersPanel.add(numberOfClustersText);
		centerPanel.add(numberOfClustersPanel);
		algorithmConfigurationPanel = new Panel();
		algorithmConfigurationPanel.setLayout(new BorderLayout());
		centerPanel.add(algorithmConfigurationPanel);
		updateAlgorithConfigurationView();		
		
		add(centerPanel);
		
		Panel bottomPanel = new Panel();
		Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				clusteringAlgorithm = ALGORITHM_CLASSES.get(algorithmChoice.getSelectedIndex());
				try {
					numberOfClusters = !defaultClusters.getState() ? Integer.parseInt(numberOfClustersText.getText()) : -1;
				} catch (NumberFormatException ex) {
					numberOfClusters = -1;
				}
				setVisible(false);
			}
		});		
		Button cancelButton = new Button("Cancel");
		cancelButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				clusteringAlgorithm = null;
				setVisible(false);
			}
		});
		bottomPanel.add(okButton);
		bottomPanel.add(cancelButton);
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
	private void updateAlgorithConfigurationView() {
		Class<?> algorithm = ALGORITHM_CLASSES.get(algorithmChoice.getSelectedIndex());
		algorithmConfigurationView = ClusteringAlgorithmFactory.getAlgorithmConfigurationView(algorithm);
		algorithmConfigurationPanel.removeAll();
		if (algorithmConfigurationView instanceof Component)
			algorithmConfigurationPanel.add((Component)algorithmConfigurationView);		
	}
	
	private Choice algorithmChoice;
	private Checkbox defaultClusters;
	private TextField numberOfClustersText;
	private Panel algorithmConfigurationPanel;
}