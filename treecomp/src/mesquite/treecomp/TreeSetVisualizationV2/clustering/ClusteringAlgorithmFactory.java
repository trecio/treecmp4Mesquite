package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import org.pr.clustering.hierarchical.LinkageCriterion;

import mesquite.lib.MesquiteWindow;
import mesquite.lib.Taxa;
import mesquite.lib.TreeVector;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.lib.duties.TreeSource;

public class ClusteringAlgorithmFactory {
	public ClusteringAlgorithmFactory(NumberFor2Trees metric, TreeSource trees, Taxa taxa) {
		this.metric = metric;
		
		this.trees = new TreeVector(taxa);
		for (int i=0; i<trees.getNumberOfTrees(taxa); i++)
			this.trees.addElement(trees.getTree(taxa, i), false);
	}
	
	public IClusteringAlgorithm create() {
		ClusteringAlgorithmSelecionView view = new ClusteringAlgorithmSelecionView(MesquiteWindow.dialogAnchor);
		view.setVisible(true);
		numberOfClusters = view.getNumberOfClusters();
		Class<?> algorithm = view.getClusteringAlgorithm();		
		if (algorithm == null)
			return null;
		
		Object configuration = view.getAlgorithmConfiguration();
		
		if (ClusteringAlgorithmSelecionView.HIERARCHICAL.equals(algorithm))
			return createHierarchical(configuration);
		else if (ClusteringAlgorithmSelecionView.KMEANS.equals(algorithm))
			return createKMeans(configuration);
		else if (ClusteringAlgorithmSelecionView.KCENTROIDS.equals(algorithm))
			return createKCentroids(configuration);
		else
			throw new RuntimeException("Unsuported clustering algorithm: " + algorithm);
	}
	
	public static IClusteringAlgorithmConfigurationView getAlgorithmConfigurationView(Class<?> algorithm) {
		if (algorithm == ClusteringAlgorithmSelecionView.HIERARCHICAL)
			return new HierarchicalClusteringConfigurationView();
		else
			return new IterativeClusteringConfigurationView();
	}
		
	private NumberFor2Trees metric;
	private TreeVector trees;
	private int numberOfClusters;

	private Hierarchical createHierarchical(Object options) {
		Hierarchical result = new Hierarchical();
		result.setLinkage((LinkageCriterion)options);
		result.setNumberOfClusters(numberOfClusters);
		return result;
	}
	
	private KCentroids createKCentroids(Object options) {
		KCentroids result = new KCentroids();
		result.setIterations((Integer) options);
		result.setNumberOfClusters(numberOfClusters);
		return result;
	}
	
	private KMeans createKMeans(Object options) {
		KMeans result = new KMeans();
		result.setIterations((Integer) options);
		result.setMetric(metric);
		result.setNumberOfClusters(numberOfClusters);
		result.setTrees(trees);
		return result;
	}
}
