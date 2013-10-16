package mesquite.treecmp.clustering.TreeClustering;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteCommand;
import mesquite.lib.MesquiteModule;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.TreeClusteringParameters.TreeClusteringParameters;

public final class TreeClustering extends MesquiteModule {
	private final Map<Tree, Integer> clusterAssignment = new HashMap<Tree, Integer>();
	private final MesquiteCommand showClusterMetrics = new MesquiteCommand("showClusterMetrics", this);
	private DistanceBetween2Trees distance;
	private Taxa taxa;

	@Override
	public boolean canHireMoreThanOnce() {
		return false;
	}	

	@Override
	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {
		if (checker.compare(getClass(), null, null, commandName, showClusterMetrics.getName())) {
			final TreeClusteringParameters parameters = (TreeClusteringParameters) hireEmployee(TreeClusteringParameters.class, "Select tree clustering parameters calculator.");
			if (parameters == null) {
				return sorry("Oops, something went wrong. Please reinstall treecmp package.");
			}
			return true;
		} else {
			return super.doCommand(commandName, arguments, checker);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeSourceDefinite treeSource = Utils.findColleagueOrHireNew(this, TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}

		distance = Utils.findColleagueOrHireNew(this, DistanceBetween2Trees.class, "Choose the tree distance measure you want to use:");
		if (distance == null) {
			return sorry("No tree distance measure has been chosen.");
		}		

		taxa = Utils.getOrChooseTaxa(this);		
		final GroupsForTreeVector groupBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		
		if (groupBuilder != null) {
			initializeMenu();
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

	public Integer getClusterNumberFor(Tree tree) {
		return clusterAssignment.get(tree);
	}
	
	public Collection<TreeVector> getClusters() {
		return inverse(clusterAssignment, taxa);
	}
	
	public DistanceBetween2Trees getDistance() {
		return distance;
	}

	private void calculateClusters(GroupsForTreeVector groupBuilder, TreeSourceDefinite treeSource, Taxa taxa,
			DistanceBetween2Trees distance) {
		final Trees trees = Utils.getTrees(treeSource, taxa);
		
		final List<Integer> clusters = groupBuilder.calculateClusters(trees, distance);
		clusterAssignment.clear();
		for (int i=0; i<trees.size(); i++) {
			final int clusterNumber = clusters.get(i);
			clusterAssignment.put(trees.getTree(i), clusterNumber);
		}
	}
	
	private void initializeMenu() {
		addMenuLine();
		addMenuItem("Show clusters quality metrics", showClusterMetrics);
	}
	
	private static Collection<TreeVector> inverse(
			Map<Tree, Integer> clusterAssignment, Taxa taxa) {
		final Map<Integer, TreeVector> assignments = new HashMap<Integer, TreeVector>();
		for (final Map.Entry<Tree, Integer> entry : clusterAssignment.entrySet()) {
			TreeVector cluster = assignments.get(entry.getValue());
			if (cluster == null) {
				cluster = new TreeVector(taxa);
				assignments.put(entry.getValue(), cluster);
			}
			cluster.addElement(entry.getKey(), false);
		}
		return assignments.values();
	}
}
