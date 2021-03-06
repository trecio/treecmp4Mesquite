package mesquite.treecmp.clustering.TreeClustering;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteCommand;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteModule;
import mesquite.lib.Notification;
import mesquite.lib.Parser;
import mesquite.lib.StringArray;
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
	private final MesquiteCommand createSelectionsFromClusters = new MesquiteCommand("createSelectionsFromClusters", this);
	private final MesquiteCommand showClusterMetrics = new MesquiteCommand("showClusterMetrics", this);
	private PrecomputedDistanceBetween2Trees distance;
	private Taxa taxa;
	private List<TreeVector> clusters;
	private TreeSourceDefinite treeSource;
	private TreeVector trees;

	@Override
	public boolean canHireMoreThanOnce() {
		return false;
	}	

	@Override
	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {
	 	if (checker.compare(getClass(), null, null, commandName, createSelectionsFromClusters.getName())) {
	 		final Parser parser = new Parser(arguments);
	 		final int clusterNumber = Integer.parseInt(parser.getTokenNumber(2));
	 		updateMesquiteSelection(clusterNumber);
	 		return Boolean.TRUE;
		} else if (checker.compare(getClass(), null, null, commandName, showClusterMetrics.getName())) {
			final TreeClusteringParameters parameters = (TreeClusteringParameters) hireEmployee(TreeClusteringParameters.class, "Select tree clustering parameters calculator.");
			if (parameters == null) {
				return sorry("Oops, something went wrong. Please reinstall treecmp package.");
			}
			return Boolean.TRUE;
		} else {
			return super.doCommand(commandName, arguments, checker);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		treeSource = Utils.findColleagueOrHireNew(this, TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}

		final GroupsForTreeVector groupBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		
		if (groupBuilder != null) {
			final DistanceBetween2Trees hiredDistance = Utils.findColleagueOrHireNew(this, DistanceBetween2Trees.class, "Choose the tree distance measure you want to use:");
			if (hiredDistance == null) {
				return sorry("No tree distance measure has been chosen.");
			}
			taxa = Utils.getOrChooseTaxa(this);		
			trees = Utils.getTrees(treeSource, taxa);
			distance = new PrecomputedDistanceBetween2Trees();
			distance.prepare(getProject(), hiredDistance, trees);

			calculateClusters(groupBuilder, distance);
			initializeMenu();
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

	public Trees allTrees() {
		return trees;
	}

	public Integer getClusterNumberFor(Tree tree) {
		return clusterAssignment.get(tree);
	}
	
	public Collection<TreeVector> getClusters() {
		return Collections.unmodifiableCollection(clusters);
	}
	
	public DistanceBetween2Trees getDistance() {
		return distance;
	}

	public Taxa getTaxa() {
		return taxa;
	}
	
	private void calculateClusters(GroupsForTreeVector groupBuilder,
			DistanceBetween2Trees distance) {
		final List<Integer> assignments = groupBuilder.calculateClusters(trees, distance);
		clusterAssignment.clear();
		for (int i=0; i<trees.size(); i++) {
			final int clusterNumber = assignments.get(i);
			clusterAssignment.put(trees.getTree(i), clusterNumber);
		}
		clusters = Utils.inverseClusterAssignments(assignments, trees);
	}
	
	private void initializeMenu() {
		addMenuLine();
		addMenuItem("Show clusters quality metrics", showClusterMetrics);
		
		int clusterId = 0;
		final StringArray clusterNames = new StringArray(getClusters().size());
		for (final TreeVector cluster : getClusters()) {
			final String menuText = getClusterLabel(clusterId, cluster);
			clusterNames.setValue(clusterId, menuText);
			clusterId++;
		}
		addSubmenu(null, "Create selection from", createSelectionsFromClusters, clusterNames);
	}

	private static String getClusterLabel(int clusterId, final TreeVector cluster) {
		return "Cluster " + clusterId + " (" + cluster.size() + " elements)";
	}
	
	private void updateMesquiteSelection(int clusterNumber) {
		final TreeVector cluster = clusters.get(clusterNumber);

		final TreeVector selectable = (TreeVector) treeSource.getSelectionable();
		if (selectable != null) {
			selectable.deselectAll();
			for (int j=0; j<cluster.size(); j++) {
				final int index = cluster.getTree(j).getFileIndex();
				selectable.setSelected(index, true);
			}
			selectable.notifyListeners((Object)this, new Notification(MesquiteListener.SELECTION_CHANGED));
		}
		
	}
}
