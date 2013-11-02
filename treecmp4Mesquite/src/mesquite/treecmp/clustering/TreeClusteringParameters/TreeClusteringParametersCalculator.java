package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import mesquite.consensus.StrictConsensusTree.StrictConsensusTree;
import mesquite.consensus.lib.Bipartition;
import mesquite.consensus.lib.BipartitionVector;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.DistanceBetween2Trees;

public final class TreeClusteringParametersCalculator {
	private TreeClusteringParametersCalculator() {}

	public static ClustersParameters getParameters(Trees allTrees, Collection<TreeVector> clusters, DistanceBetween2Trees distance, Taxa taxa) {
		final double minDistanceBetween = getMinDistanceBetweenClusters(clusters, distance);
	 	final List<ClusterParameters> parameters = new ArrayList<ClusterParameters>(clusters.size());
	 	final TreeVector consensusTrees = new TreeVector(taxa);
		final Consenser consenser = new StrictConsensusTree();
		for (final TreeVector cluster : clusters) {
			final Tree strictConsensusTree = consenser.consense(cluster);
			consensusTrees.addElement(strictConsensusTree, false);

			parameters.add(getClusterParameters(cluster, distance, strictConsensusTree));
		}
		
		final Tree allTreesConsensus = consenser.consense(consensusTrees);
		final ClusterParameters allTreeParameters = getClusterParameters(allTrees, distance, allTreesConsensus);		
		final InformationLoss informationLoss = getInformationLoss(allTrees, clusters, consensusTrees);
		return new ClustersParameters(minDistanceBetween, parameters.toArray(new ClusterParameters[0]), allTreeParameters, informationLoss );
	}
	
	private static double getAverageDistance(Trees cluster,
			DistanceBetween2Trees distance) {
		final int numberOfTrees = cluster.size();
		if (numberOfTrees <= 1) {
			return 0;
		}
		final MesquiteNumber number = new MesquiteNumber();
		double sumOfDistances = 0;
		for (int i=0; i<numberOfTrees; i++) {
			final Tree t1 = cluster.getTree(i);
			for (int j=i+1; j<numberOfTrees; j++) {
				final Tree t2 = cluster.getTree(j);
				distance.calculateNumber(t1, t2, number, null);
				sumOfDistances += number.getDoubleValue();
			}
		}
		return sumOfDistances * 2 / (numberOfTrees) / (numberOfTrees-1);
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

	private static double getBoundingBallsUnionSize(TreeVector consensusTrees) {
		final int numberOfTrees = consensusTrees.size();
		double boundingBallsUnionSize = 0;		
		Map<BitSet, Tree> intersections = new IdentityHashMap<BitSet, Tree>();
		for (int i=0; i<numberOfTrees; i++) {
			final BitSet singleton = new BitSet(numberOfTrees);
			final Tree tree = consensusTrees.getTree(i); 
			singleton.set(i);
			boundingBallsUnionSize += getBoundingBallSize(tree);
			intersections.put(singleton, tree);
		}
		for (int numberOfSetsInIntersections = 2; numberOfSetsInIntersections<=numberOfTrees; numberOfSetsInIntersections++) {
			final double sign = numberOfSetsInIntersections % 2 == 0 ? -1 : 1;
			final Map<BitSet, Tree> newIntersections = new IdentityHashMap<BitSet, Tree>();
			for (final Map.Entry<BitSet, Tree> entry : intersections.entrySet()) {	//out of all intersections of size i-1
				final BitSet treesInIntersection = entry.getKey();					//tries to make nonempty intersections of size i
				final Tree intersectionTree = entry.getValue();
				final int firstIncludedTree = treesInIntersection.nextSetBit(0);
				for (int i=0; i<firstIncludedTree; i++) {	//by adding trees at lower index than all trees which are already there 
					final Tree newIntersectionTree = getConsensusTreeIntersection(intersectionTree, consensusTrees.getTree(i), consensusTrees.getTaxa());
					if (newIntersectionTree != null) {
						final BitSet treesInNewIntersection = (BitSet) treesInIntersection.clone();
						treesInIntersection.set(i);
						newIntersections.put(treesInNewIntersection, newIntersectionTree);
						
						boundingBallsUnionSize += sign * getBoundingBallSize(newIntersectionTree);
					}
				}
			}
			
			intersections = newIntersections;
			
		}
		return boundingBallsUnionSize;
	}

	private static ClusterParameters getClusterParameters(Trees cluster,
			DistanceBetween2Trees distance, Tree strictConsensusTree) {
		final double avgDistance = getAverageDistance(cluster, distance);
		final double diameter = getDiameter(cluster, distance);

		final double specificity = getSpecificity(cluster, strictConsensusTree);
		final double density = getDensity(cluster, strictConsensusTree);
		
		return new ClusterParameters(cluster.size(), avgDistance, diameter, specificity, density);
	}

	private static Tree getConsensusTreeIntersection(Tree tree1,
			Tree tree2, Taxa taxa) {
		final BipartitionVector bipartitions = new BipartitionVector();
		bipartitions.setRooted(tree1.getRooted() && tree2.getRooted());
		bipartitions.setMode(BipartitionVector.SEMISTRICTMODE);
		bipartitions.setTaxa(taxa);
		bipartitions.addTree(tree1);
		bipartitions.addTree(tree2);		
		for (int i=0; i<bipartitions.size(); i++) {
			final Bipartition bipartition = bipartitions.getBipart(i);
			for (int j=i+1; j<bipartitions.size(); j++) {
				if (!bipartitions.compatible(bipartition.getBits(), bipartitions.getBipart(j).getBits())) {
					return null;
				}
			}
		}
		return bipartitions.makeTree();
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

	private static InformationLoss getInformationLoss(
			Trees allTrees, Collection<TreeVector> clusters, TreeVector consensusTrees) {
		final int numberOfTreesOutsideClusters = getNumberOfTreesOutsideClusters(allTrees, clusters);
		final int numberOfTrees = allTrees.size();
		final int numberOfTreesWithinClusters = numberOfTrees - numberOfTreesOutsideClusters;
		final double boundingBallUnionSize = getBoundingBallsUnionSize(consensusTrees);
		final double numberOfAdditionalTreesInBoundingBall = boundingBallUnionSize - numberOfTreesWithinClusters;
		
		final double f = 1./numberOfTrees;
		final double g = 1./boundingBallUnionSize;
		
		final double Linf = Linf(f, 0, numberOfTreesOutsideClusters, 
				f, g, numberOfTreesWithinClusters, 
				0, g, numberOfAdditionalTreesInBoundingBall);
		final double L1 = L1(f, 0, numberOfTreesOutsideClusters, 
				f, g, numberOfTreesWithinClusters, 
				0, g, numberOfAdditionalTreesInBoundingBall);
		final double L2 = L2(f, 0, numberOfTreesOutsideClusters, 
				f, g, numberOfTreesWithinClusters, 
				0, g, numberOfAdditionalTreesInBoundingBall);
		final double KL = KL(f, 0, numberOfTreesOutsideClusters, 
				f, g, numberOfTreesWithinClusters, 
				0, g, numberOfAdditionalTreesInBoundingBall);
		return new InformationLoss(Linf, L1, L2, KL);
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

	private static int getNumberOfTreesOutsideClusters(Trees allTrees,
			Collection<TreeVector> clusters) {
		final HashSet<Tree> allClusteredTrees = new HashSet<Tree>();
		for (final TreeVector cluster : clusters) {
			for (int i=0; i<cluster.size(); i++) {
				allClusteredTrees.add(cluster.getTree(i));
			}
		}
		
		int numberOfTreesOutsideClusters = 0;
		for (int i=0; i<allTrees.size(); i++) {
			if (!allClusteredTrees.contains(allTrees.getTree(i))) {
				numberOfTreesOutsideClusters++;
			}
		}
		return numberOfTreesOutsideClusters;
	}

	private static double getSpecificity(Trees cluster, Tree strictConsensusTree) {
		int rootNode = strictConsensusTree.getRoot();
		return (strictConsensusTree.numberOfInternalsInClade(rootNode) - 1.)
				/ (strictConsensusTree.numberOfTerminalsInClade(rootNode) - 3);
	}
	
	private static double KL(double... valuesAndCounts) {
		double result = 0;
		for (int i=0; i<valuesAndCounts.length; i+=3) {
			final double val1 = valuesAndCounts[i];
			final double val2 = valuesAndCounts[i+1];
			final double count = valuesAndCounts[i+2];
			
			if (val1 > 0 && val2 > 0) {
				result += val1 * Math.log(val1 / val2) * count;
			}
		}
		return result;
	}
	
	private static double Linf(double... valuesAndCounts) {
		double result = Double.MIN_VALUE;
		for (int i=0; i<valuesAndCounts.length; i+=3) {
			final double val1 = valuesAndCounts[i];
			final double val2 = valuesAndCounts[i+1];
			final double count = valuesAndCounts[i+2];
			if (count >= 0.5) {
				result = Math.max(result, Math.abs(val1-val2));
			}
		}
		return result;
	}
	
	private static double L1(double... valuesAndCounts) {
		double result = 0;
		for (int i=0; i<valuesAndCounts.length; i+=3) {
			final double val1 = valuesAndCounts[i];
			final double val2 = valuesAndCounts[i+1];
			final double count = valuesAndCounts[i+2];
			
			result += Math.abs(val1-val2) * count;
		}
		return result;
	}
	
	private static double L2(double... valuesAndCounts) {
		double result = 0;
		for (int i=0; i<valuesAndCounts.length; i+=3) {
			final double val1 = valuesAndCounts[i];
			final double val2 = valuesAndCounts[i+1];
			final double count = valuesAndCounts[i+2];
			final double difference = Math.abs(val1-val2);
			
			result += difference * difference * count;
		}
		return Math.sqrt(result);
	}
}
