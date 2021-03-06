package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public static ClustersParameters getParameters(Trees allTrees, Collection<TreeVector> clusters, DistanceBetween2Trees distance) {
		final double avgDistanceBetween = getAverageDistanceBetweenClusters(clusters, distance);
		final double minDistanceBetween = getMinDistanceBetweenClusters(clusters, distance);
	 	final List<ClusterParameters> parameters = new ArrayList<ClusterParameters>(clusters.size());
	 	final TreeVector consensusTrees = new TreeVector(allTrees.getTaxa());
		final Consenser consenser = new StrictConsensusTree();
		for (final TreeVector cluster : clusters) {
			final Tree strictConsensusTree = consenser.consense(cluster);
			consensusTrees.addElement(strictConsensusTree, false);

			parameters.add(getClusterParameters(cluster, distance, strictConsensusTree));
		}
		
		final Tree allTreesConsensus = consenser.consense(consensusTrees);
		final ClusterParameters allTreeParameters = getClusterParameters(allTrees, distance, allTreesConsensus);		
		final InformationLoss informationLoss = getInformationLoss(allTrees, clusters, consensusTrees);
		return new ClustersParameters(avgDistanceBetween, minDistanceBetween, parameters.toArray(new ClusterParameters[0]), allTreeParameters, informationLoss);
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

	private static double getAverageDistanceBetweenClusters(
			Collection<TreeVector> clusters, DistanceBetween2Trees distance) {
		double sumDistancesBetween = 0;
		int numberOfPairs = 0;
		
		final MesquiteNumber number = new MesquiteNumber();
		for (final Trees cluster1 : clusters) {
			for (final Trees cluster2 : clusters) {
				if (cluster1 != cluster2) {
					final int cluster1Size = cluster1.size();
					final int cluster2Size = cluster2.size();
					numberOfPairs += cluster1Size * cluster2Size;
					for (int i=0; i<cluster1Size; i++) {
						for (int j=0; j<cluster2Size; j++) {
							distance.calculateNumber(cluster1.getTree(i), cluster2.getTree(j), number, null);
							sumDistancesBetween += number.getDoubleValue();
						}
					}
				}
			}
		}
		return numberOfPairs > 0 
				? sumDistancesBetween / numberOfPairs
				: 0;
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
						treesInNewIntersection.set(i);
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

		final double specificity = getSpecificity(strictConsensusTree);
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
		final Collection<Integer> uniqueTopologies = getTreeDistribution(cluster);
		
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
		Trees treesOutsideClusters = allTrees;
		for (final TreeVector cluster : clusters) {
			treesOutsideClusters = except(treesOutsideClusters, cluster);
		}
		final TreeVector treesOutsideBoundingBalls = getTreesNotMatchingTo(treesOutsideClusters, consensusTrees);
		final Collection<Integer> treesOutsideBoundingBallsDistribution = getTreeDistribution(treesOutsideBoundingBalls);
		final int numberOfTreeTopologiesOutsideBoundingBalls = treesOutsideBoundingBallsDistribution.size();
		
		final TreeVector treesInsideBoundingBalls = except(allTrees, treesOutsideBoundingBalls);
		final Collection<Integer> treeDistribution = getTreeDistribution(treesInsideBoundingBalls);
		final int numberOfTreeTopologiesInsideBoundingBalls = treeDistribution.size();
		
		final double boundingBallUnionSize = getBoundingBallsUnionSize(consensusTrees);
		final double numberOfAdditionalTreesInBoundingBall = boundingBallUnionSize - numberOfTreeTopologiesInsideBoundingBalls;
		
		final double g = 1./boundingBallUnionSize;
		
		final int numberOfDescriptionItems = numberOfTreeTopologiesOutsideBoundingBalls + numberOfTreeTopologiesInsideBoundingBalls +1;		
		final double[] fValues = new double[numberOfDescriptionItems];
		final double[] gValues  = new double[numberOfDescriptionItems];
		final double[] counts = new double[numberOfDescriptionItems];
		final double numberOfAllTrees = allTrees.size();
		//trees in set but outside bounding balls
		int i=0;
		for (final Integer numberOfDuplicates : treesOutsideBoundingBallsDistribution) {
			fValues[i] = numberOfDuplicates / numberOfAllTrees;
			gValues[i] = 0;
			counts[i] = 1;
			i+=1;
		}
		//trees in both set and bounding balls
		for (final Integer numberOfDuplicates : treeDistribution) {
			fValues[i] = numberOfDuplicates / numberOfAllTrees;
			gValues[i] = g;
			counts[i] = 1;
			i+=1;
		}
		//trees not in set but in bounding balls
		fValues[i] = 0;
		gValues[i] = g;
		counts[i] = numberOfAdditionalTreesInBoundingBall;
		
		final double Linf = Linf(fValues, gValues, counts);
		final double L1 = L1(fValues, gValues, counts);
		final double L2 = L2(fValues, gValues, counts);
		final double KL = KL(fValues, gValues, counts);
		return new InformationLoss(Linf, L1, L2, KL);
	}

	private static TreeVector getTreesNotMatchingTo(Trees trees,
			TreeVector consensusTrees) {
		final int numberOfTrees = trees.size();
		final int numberOfConsensusTrees = consensusTrees.getNumberOfTrees();

		final Taxa taxa = trees.getTaxa();
		final TreeVector notMatching = new TreeVector(taxa);
		
		for (int i=0; i<numberOfTrees; i++) {
			final Tree tree = trees.getTree(i);
			boolean foundCompatibleConsensusTree = false;
			for (int j=0; j<numberOfConsensusTrees && !foundCompatibleConsensusTree; j++) {
				final Tree consensusTree = consensusTrees.getTree(j);
				final Tree intersection = getConsensusTreeIntersection(tree, consensusTree, taxa);
				if (intersection != null) {
					foundCompatibleConsensusTree = true;
				}
			}
			if (!foundCompatibleConsensusTree) {
				notMatching.addElement(tree, false);
			}
		}
		
		return notMatching;
	}

	private static TreeVector except(Trees set,
			TreeVector excluded) {
		final Set<Tree> excludedSet = new HashSet<Tree>();
		for (int i=0; i<excluded.size(); i++) {
			final Tree excludedTree = excluded.getTree(i);
			excludedSet.add(excludedTree);
		}
		final TreeVector result = new TreeVector(set.getTaxa());
		for (int i=0; i<set.size(); i++) {
			final Tree tree = set.getTree(i);
			if (!excludedSet.contains(tree)) {
				result.addElement(tree, false);
			}
		}
		return result;
	}

	private static Collection<Integer> getTreeDistribution(Trees allTrees) {
		final Map<Tree, Integer> uniqueTopologyTreeCounts = new IdentityHashMap<Tree, Integer>();
		final int numberOfTrees = allTrees.size();
		for (int i=0; i<numberOfTrees; i++) {
			final Tree tree = allTrees.getTree(i);
			boolean foundMatch = false;
			for (final Map.Entry<Tree, Integer> treeTopology : uniqueTopologyTreeCounts.entrySet()) {
				if (tree.equalsTopology(treeTopology.getKey(), false)) {
					uniqueTopologyTreeCounts.put(treeTopology.getKey(), treeTopology.getValue()+1);
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch) {
				uniqueTopologyTreeCounts.put(tree, 1);
			}
		}
		return uniqueTopologyTreeCounts.values();
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

	private static double getSpecificity(Tree strictConsensusTree) {
		int rootNode = strictConsensusTree.getRoot();
		return (strictConsensusTree.numberOfInternalsInClade(rootNode) - 1.)
				/ (strictConsensusTree.numberOfTerminalsInClade(rootNode) - 3);
	}
	
	private static double KL(double[] left, double[] right, double[] counts) {
		double result = 0;
		for (int i=0; i<left.length; i++) {
			final double val1 = left[i];
			final double val2 = right[i];
			
			if (val1 > 0 && val2 > 0) {
				result += val1 * Math.log(val1 / val2) * counts[i];
			}
		}
		return result;
	}
	
	private static double Linf(double[] left, double[] right, double[] counts) {
		double result = 0;
		for (int i=0; i<left.length; i++) {
			if (counts[i] >= 0.5) {
				result = Math.max(result, Math.abs(left[i]-right[i]));
			}
		}
		return result;
	}
	
	private static double L1(double[] left, double[] right, double[] counts) {
		double result = 0;
		for (int i=0; i<left.length; i++) {
			
			result += Math.abs(left[i]-right[i]) * counts[i];
		}
		return result;
	}
	
	private static double L2(double[] left, double[] right, double[] counts) {
		double result = 0;
		for (int i=0; i<left.length; i++) {
			final double difference = Math.abs(left[i]-right[i]);
			
			result += difference * difference * counts[i];
		}
		return Math.sqrt(result);
	}
}
