package mesquite.treecmp.clustering.KMeansTreeClustering;

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
import mesquite.treecmp.clustering.AbstractKCentroidMeans;

class KMeans extends AbstractKCentroidMeans<Tree> {

	public KMeans(Trees trees, NumberFor2Trees metric) {
		this.metric = metric;
		this.largerIsFurther = metric.largerIsFurther();
		this.trees = trees;
	}

	private final NumberFor2Trees metric;
	private final boolean largerIsFurther;
	private final Trees trees;

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

	private final Consenser consenser = new MajRuleTree();

	private final MesquiteNumber numberReference = new MesquiteNumber();
	private final MesquiteString stringReference = new MesquiteString();
}