package mesquite.treecmp.clustering.ClusterAssignment;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberForTree;
import mesquite.lib.duties.TreeSource;
import mesquite.treeSetViz.TreeSetVisualization.TreeSetVisualization;

public class ClusterAssignment extends NumberForTree {
	private int counter;

	@Override
	public void calculateNumber(Tree tree, MesquiteNumber result,
			MesquiteString resultString) {
		counter++;
		result.setValue(counter);
		if (resultString != null) {
			resultString.setValue("Cluster " + counter);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeSource source = getTreeSource();
		if (source == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Cluster Assignments";
	}

	private TreeSource getTreeSource() {
		final Object employer = getEmployer();
		if (employer instanceof TreeSetVisualization) {
			final TreeSetVisualization tsv = (TreeSetVisualization) employer;
			final Object result = tsv.doCommand("getTreeSource", null);
			if (result instanceof TreeSource) {
				return (TreeSource)result;
			}
		}
		
		return null;
	}
}
