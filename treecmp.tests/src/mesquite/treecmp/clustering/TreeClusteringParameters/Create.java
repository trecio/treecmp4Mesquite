package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.treecmp.metrics.TreeConverter;

public class Create {

	public static TreeVector treeVector(String... treeDescriptions) {
		final Taxa taxa = new Taxa(0);
		final TreeVector treeVector = new TreeVector(taxa);
		for (final String treeDescription : treeDescriptions) {
			final Tree tree = TreeConverter.getMesquiteFrom(treeDescription, taxa);
			treeVector.addElement(tree, false);
		}
		return treeVector;
	}
	
	public static TreeVector treeVector(Trees source, int... indexes) {
		final TreeVector treeVector = new TreeVector(source.getTaxa());
		for (final int idx : indexes) {
			treeVector.addElement(source.getTree(idx), false);
		}
		return treeVector;
	}
}
