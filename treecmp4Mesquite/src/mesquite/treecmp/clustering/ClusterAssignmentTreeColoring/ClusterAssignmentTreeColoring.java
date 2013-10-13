package mesquite.treecmp.clustering.ClusterAssignmentTreeColoring;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberForTree;
import mesquite.treecmp.clustering.TreeClustering.TreeClustering;

public class ClusterAssignmentTreeColoring extends NumberForTree {
	TreeClustering clustering;
	private final int VALUE_IF_NOT_IN_DATA_SET = -1;

	@Override
	public void calculateNumber(Tree tree, MesquiteNumber result,
			MesquiteString resultString) {
		final Integer assignedCluster = clustering.getClusterNumberFor(tree);
		result.setValue(assignedCluster != null
				? assignedCluster
				: VALUE_IF_NOT_IN_DATA_SET);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		clustering = (TreeClustering) hireEmployee(TreeClustering.class, "Choose clusters generator.");
		
		if (clustering == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Cluster Assignments";
	}
}
