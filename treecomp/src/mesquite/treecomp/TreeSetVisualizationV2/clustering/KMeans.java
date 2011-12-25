package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.consensus.MajRuleTree.MajRuleTree;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.NumberFor2Trees;

public class KMeans extends AbstractKCentroidMeans<Tree> implements IClusteringAlgorithm {
	private NumberFor2Trees metric;	
	private boolean largerIsFurther;
	public NumberFor2Trees getMetric() {
		return metric;
	}

	public void setMetric(NumberFor2Trees metric) {
		this.metric = metric;
		largerIsFurther = metric.largerIsFurther();
	}
	
	private Trees trees;	
	public Trees getTrees() {
		return trees;
	}

	public void setTrees(Trees trees) {
		this.trees = trees;
	}	

	@Override
	protected List<Tree> computeCentres(List<Collection<Integer>> associations) {
		List<Tree> centers = new ArrayList<Tree>(associations.size());
		
		for (int i=0; i<associations.size(); i++) {
			TreeVector associatedTrees = new TreeVector(trees.getTaxa());
			for (Integer index : associations.get(i))
				associatedTrees.addElement(trees.getTree(index), false);
			centers.add(consenser.consense(associatedTrees));
		}
		return centers;
	}
	
	
	@Override
	protected double getDistanceFromCenterToTree(Tree center, Tree tree) {
		metric.initialize(center, tree);
		metric.calculateNumber(center, tree, numberReference, stringReference);
		double distanceToClosest = largerIsFurther ? numberReference.getDoubleValue() : -numberReference.getDoubleValue();
		return distanceToClosest;
	}	
	
	@Override
	protected int getNumberOfTrees() {
		return trees.size();
	}

	@Override
	protected Tree getTree(int index) {
		return trees.getTree(index);
	}

	private Consenser consenser = new MajRuleTree();

	private final MesquiteNumber numberReference = new MesquiteNumber();
	private final MesquiteString stringReference = new MesquiteString();
}