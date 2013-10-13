package mesquite.treecmp.clustering.TreeClustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteProject;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class TreeClustering extends MesquiteModule {
	private final Map<Tree, Integer> clusterAssignment = new HashMap<Tree, Integer>();

	@Override
	public boolean canHireMoreThanOnce() {
		return false;
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeSourceDefinite treeSource = Utils.findColleagueOrHireNew(this, TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}

		final DistanceBetween2Trees distance = Utils.findColleagueOrHireNew(this, DistanceBetween2Trees.class, "Choose the tree distance measure you want to use:");
		if (distance == null) {
			return sorry("No tree distance measure has been chosen.");
		}
		

		final Taxa taxa = Utils.getOrChooseTaxa(this);
		
		final GroupsForTreeVector groupBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		
		if (groupBuilder != null) {
			calculateClusters(groupBuilder, treeSource, taxa, distance);
			return true;
		} 
		return false;
	}

	@Override
	public Class<?> getDutyClass() {
		return TreeClustering.class;
	}

	@Override
	public String getName() {
		return "Phylogenetic tree clustering";
	}

	private void calculateClusters(GroupsForTreeVector groupBuilder, TreeSourceDefinite treeSource, Taxa taxa,
			DistanceBetween2Trees distance) {
		final Trees trees = Utils.getTrees(treeSource, taxa);
		
		final List<Integer> clusters = groupBuilder.calculateClusters(trees, distance);
		clusterAssignment.clear();
		for (int i=0; i<trees.size(); i++) {
			clusterAssignment.put(trees.getTree(i), clusters.get(i));
		}
	}
	
	public Integer getClusterNumberFor(Tree tree) {
		return clusterAssignment.get(tree);
	}
}
