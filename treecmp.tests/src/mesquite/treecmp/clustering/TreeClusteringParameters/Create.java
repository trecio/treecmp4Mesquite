package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
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

}
