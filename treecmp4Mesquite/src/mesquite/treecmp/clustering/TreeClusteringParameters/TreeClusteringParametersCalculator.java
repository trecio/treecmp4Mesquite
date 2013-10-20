package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.consensus.StrictConsensusTree.StrictConsensusTree;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.DistanceBetween2Trees;

public final class TreeClusteringParametersCalculator {
	private TreeClusteringParametersCalculator() {}

	public static ClustersParameters getParameters(Collection<TreeVector> clusters, DistanceBetween2Trees distance, Taxa taxa) {
		final double minDistanceBetween = getMinDistanceBetweenClusters(clusters, distance);
	 	final List<ClusterParameters> parameters = new ArrayList<ClusterParameters>(clusters.size());
	 	final TreeVector allTrees = new TreeVector(taxa);
		for (final TreeVector cluster : clusters) {
			parameters.add(getClusterParameters(cluster, distance));
			allTrees.addElements(cluster, false);
			
		}
		final ClusterParameters allTreeParameters = getClusterParameters(allTrees, distance);
		return new ClustersParameters(minDistanceBetween, parameters.toArray(new ClusterParameters[0]), allTreeParameters);
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
		double diameter = 0;
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
