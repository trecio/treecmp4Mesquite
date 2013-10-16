package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mesquite.consensus.StrictConsensusTree.StrictConsensusTree;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lists.lib.ListAssistant;
import mesquite.lists.lib.ListModule;
import mesquite.treecmp.clustering.TreeClustering.TreeClustering;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.Column;
import mesquite.treecmp.clustering.TreeClusteringParametersListAssistant.TreeClusteringParametersListAssistant;

public final class TreeClusteringParameters extends ListModule {

	private ClustersParameters parameters;

	@Override
	public Class<? extends ListAssistant> getAssistantClass() {
		return TreeClusteringParametersListAssistant.class;
	}

	@Override
	public int getNumberOfRows() {
		return parameters.cluster.length;
	}
	
	

	@Override
	public Object getMainObject() {
		return parameters;
	}

	@Override
	public String getItemTypeName() {
		return "cluster";
	}

	@Override
	public String getItemTypeNamePlural() {
		return "clusters";
	}

	@Override
	public String getAnnotation(int row) {
		return null;
	}

	@Override
	public void setAnnotation(int row, String s, boolean notify) {
	}

	@Override
	public boolean deleteRow(int row, boolean notify) {
		return false;
	}

	@Override
	public void showListWindow(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean showing(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	final Iterable<Column<ClusterParameters>> columnModel = Arrays.asList(
			new Column<ClusterParameters>("Diameter", "diameter", ClusterParameters.class),
			new Column<ClusterParameters>("Specificity", "specificity", ClusterParameters.class),
			new Column<ClusterParameters>("Density", "density", ClusterParameters.class));

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeClustering treeClustering = (TreeClustering) findNearestColleagueWithDuty(TreeClustering.class);
		final Collection<TreeVector> clusters = treeClustering.getClusters();
		DistanceBetween2Trees distance = treeClustering.getDistance();
		parameters = getParameters(clusters, distance);
		final ClusterParametersWindow window = new ClusterParametersWindow(this);
		
		final String assistantName = '#' + TreeClusteringParametersListAssistant.class.getSimpleName();
		for (final Column<ClusterParameters> column : columnModel) {
			final TreeClusteringParametersListAssistant assistant = (TreeClusteringParametersListAssistant) hireNamedEmployee(TreeClusteringParametersListAssistant.class, assistantName);
			assistant.setColumnModel(column);
			window.addListAssistant(assistant);
		}
		window.show();
		return true;
	}

	@Override
	public String getName() {
		return "Tree clustering parameters";
	}
	
	public static ClustersParameters getParameters(Collection<TreeVector> clusters, DistanceBetween2Trees distance) {
		final double minDistanceBetween = getMinDistanceBetweenClusters(clusters, distance);
		final List<ClusterParameters> parameters = new ArrayList<ClusterParameters>(clusters.size());
		for (final Trees cluster : clusters) {
			parameters.add(getClusterParameters(cluster, distance));
		}
		return new ClustersParameters(minDistanceBetween, parameters.toArray(new ClusterParameters[0]));
	}

	private static double getBoundingBallSize(Tree tree) {
		double boundingBallSize = 1;
		for (int node = tree.firstInPostorder(); tree.nodeExists(node); node = tree.nextInPostorder(node)) {
			final int numberOfNeighbors = tree.daughtersOfNode(node).length 
						+ (tree.getRoot() == node
							? 0
							: 1);
			double multiplier = 2 * numberOfNeighbors - 5;
			while (multiplier > 1) {
				boundingBallSize *= multiplier;
				multiplier -= 2;
			}
		}
		return boundingBallSize;
	}

	private static ClusterParameters getClusterParameters(Trees cluster,
			DistanceBetween2Trees distance) {
		final double diameter = getDiameter(cluster, distance);

		final Consenser consenser = new StrictConsensusTree();
		final Tree strictConsensusTree = consenser.consense(cluster);
		final double specificity = getSpecificity(cluster, strictConsensusTree);
		final double density = getDensity(cluster, strictConsensusTree);
		
		return new ClusterParameters(cluster.size(), diameter, specificity, density);
	}

	private static double getDensity(Trees cluster, Tree strictConsensusTree) {
		final List<Tree> uniqueTopologies = new ArrayList<Tree>();
		for (int i=0; i<cluster.size(); i++) {
			final Tree tree = cluster.getTree(i);
			boolean otherTreeHasEqualTopology = false;
			for (final Tree otherTree : uniqueTopologies) {
				if (tree.equalsTopology(otherTree, false)) {
					otherTreeHasEqualTopology = true;
					break;
				}
			}
			if (!otherTreeHasEqualTopology) {
				uniqueTopologies.add(tree);
			}
		}
		
		final double boundingBallSize = getBoundingBallSize(strictConsensusTree);
		return uniqueTopologies.size() / boundingBallSize;
	}

	private static double getDiameter(Trees cluster,
			DistanceBetween2Trees distance) {
		double diameter = Double.MIN_VALUE;
		final MesquiteNumber number = new MesquiteNumber();
		for (int i=0; i<cluster.size(); i++) {
			for (int j=0; j<cluster.size(); j++) {
				if (i != j) {
					distance.calculateNumber(cluster.getTree(i), cluster.getTree(j), number, null);
					diameter = Math.max(diameter, number.getDoubleValue());
				}
			}
		}
		return diameter;
	}

	private static double getMinDistanceBetweenClusters(
			Collection<TreeVector> clusters, DistanceBetween2Trees distance) {
		double minDistanceBetween = Double.MAX_VALUE;
		final MesquiteNumber number = new MesquiteNumber();
		for (final Trees cluster1 : clusters) {
			for (final Trees cluster2 : clusters) {
				if (cluster1 != cluster2) {
					for (int i=0; i<cluster1.size(); i++) {
						for (int j=0; j<cluster2.size(); j++) {
							distance.calculateNumber(cluster1.getTree(i), cluster2.getTree(j), number, null);
							minDistanceBetween = Math.min(minDistanceBetween, number.getDoubleValue());
						}
					}
				}
			}
		}
		return minDistanceBetween;
	}

	private static double getSpecificity(Trees cluster, Tree strictConsensusTree) {
		int rootNode = strictConsensusTree.getRoot();
		return (strictConsensusTree.numberOfInternalsInClade(rootNode) - 1.)
				/ (strictConsensusTree.numberOfTerminalsInClade(rootNode) - 3);
	}
}
