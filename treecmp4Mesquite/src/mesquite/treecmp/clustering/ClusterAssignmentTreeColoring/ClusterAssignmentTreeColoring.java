package mesquite.treecmp.clustering.ClusterAssignmentTreeColoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteString;
import mesquite.lib.Snapshot;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.NumberForTree;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treeSetViz.TreeSetVisualization.TreeSetVisualization;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class ClusterAssignmentTreeColoring extends NumberForTree {
	private final Map<Tree, Integer> clusterAssignment = new HashMap<Tree, Integer>();
	private final int VALUE_IF_NOT_IN_DATA_SET = -1;

	@Override
	public void calculateNumber(Tree tree, MesquiteNumber result,
			MesquiteString resultString) {
		final Integer assignedCluster = clusterAssignment.get(tree);
		result.setValue(assignedCluster != null
				? assignedCluster
				: VALUE_IF_NOT_IN_DATA_SET);
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final Snapshot tsvSnapshot = getTreeSetVisualizationSnapshotIfEmployer();
		final MesquiteProject project = employer.getProject();
		DistanceBetween2Trees distance = null;
		TreeSourceDefinite treeSource = null;
		Taxa taxa = null;
		
		if (tsvSnapshot != null) {	//using snapshot is a hack but allows not making big changes in Tree Set Visualization module 
			for (int i=0; i<tsvSnapshot.getNumLines(); i++) {
				final MesquiteModule module = tsvSnapshot.getModule(i); 
				if (module instanceof DistanceBetween2Trees) {
					distance = (DistanceBetween2Trees) module;
				}
				if (module instanceof TreeSourceDefinite) {
					treeSource = (TreeSourceDefinite) module;  
				}
			}
			//TODO Accept projects with more than one set of taxa. I have a method to get taxa directly from TSV yet.
			if (project.getTaxas().size() == 1) {
				taxa = project.getTaxa(0);
			} else {
				return sorry("Only projects with one set of taxa are supported.");
			}
		}
		
		if (distance==null || treeSource==null || taxa==null) {
			return sorry("Cluster assignments work only under Tree Set Visualization module.");	//TODO implement standard way of obtaining tree source and distance metric
		}
		
		final GroupsForTreeVector groupBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		
		calculateClusters(groupBuilder, treeSource, taxa, distance);
		return true;
	}

	@Override
	public String getName() {
		return "Cluster Assignments";
	}
	
	private void calculateClusters(GroupsForTreeVector groupBuilder, TreeSourceDefinite treeSource, Taxa taxa,
			DistanceBetween2Trees distance) {
		final List<Tree> trees = Utils.getTrees(treeSource, taxa);
		
		final List<Integer> clusters = groupBuilder.calculateClusters(trees, distance);
		clusterAssignment.clear();
		for (int i=0; i<trees.size(); i++) {
			clusterAssignment.put(trees.get(i), clusters.get(i));
		}
	}

	private Snapshot getTreeSetVisualizationSnapshotIfEmployer() {
		final Object employer = getEmployer();
		//TODO to make this work even with no TSV first try to check class presence using reflection then access module from different class (to avoid imports in this file)
		if (employer instanceof TreeSetVisualization) { 
			final TreeSetVisualization tsv = (TreeSetVisualization) employer;
			return tsv.getSnapshot(null);
		}
		return null;
	}
}
