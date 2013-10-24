package treecmp.metric;

import java.util.BitSet;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.LapSolver;
import treecmp.common.Split;
import treecmp.common.SplitDist;

public class WeightedMatchingSplitMetric extends BaseMetric {

	@Override
	public double getDistance(Tree t1, Tree t2) {
		final IdGroup idGroup1 = TreeUtils.getLeafIdGroup(t1);
		final IdGroup idGroup2 = TreeUtils.getLeafIdGroup(t2);
		
		//do the calculations for all nontrivial splits
		final Split[] s1=SplitDist.getTreeSplits(t1, idGroup1);
        final Split[] s2=SplitDist.getTreeSplits(t2, idGroup2);
        final int taxaNumber = Math.max(idGroup1.getIdCount(), idGroup2.getIdCount());
		final int size = Math.max(s1.length, s2.length);		

		final double[][] assigncost = getCostMatrix(size, taxaNumber, s1, s2);
		final int[] rowsol = new int[size];
		final int[] colsol = new int[size];
		final double[] u = new double[size];
		final double[] v = new double[size];
		double distance = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
		
		//now adjust result by the differences between edges to leafs in both trees
		for (int i=0; i<t1.getExternalNodeCount(); i++) {
			final Node leaf1 = t1.getExternalNode(i);
			for (int j=0; j<t2.getExternalNodeCount(); j++) {
				final Node leaf2 = t2.getExternalNode(j);
				if (leaf1.getIdentifier().equals(leaf2.getIdentifier())) {
					distance += Math.abs(leaf1.getBranchLength() - leaf2.getBranchLength());
				}
			}
		}
		
		return distance;
	}

	private double[][] getCostMatrix(int size, int taxaNumber, Split[] s1, Split[] s2) {
		double[][] costs = new double[size][];
		for (int i=0; i<size; i++) {
			costs[i] = new double[size];
		}
		
		for (int i=0; i<s1.length; i++) {
			for (int j=0; j<s2.length; j++) {
				costs[i][j] = getDistance(s1[i], s2[j], taxaNumber);
			}
		}
		
		for (int i=s1.length; i<size; i++) {
			for (int j=0; j<s2.length; j++) {
				costs[i][j] = getDistanceToNeutral(s2[j], taxaNumber);
			}
		}
		
		for (int i=0; i<s1.length; i++) {
			for (int j=s2.length; j<size; j++) {
				costs[i][j] = getDistanceToNeutral(s1[i], taxaNumber);
			}
		}
		return costs;
	}

	private double getDistanceToNeutral(Split split, int taxaNumber) {
		final double branchLength = split.node.getBranchLength();
		return branchLength * smallerPartitionSize(split.bitSet, taxaNumber);
	}

	private double getDistance(Split s1, Split s2, int taxaNumber) {
		final BitSet symmetricDifference = (BitSet)s1.bitSet.clone();
		symmetricDifference.xor(s2.bitSet);
		final int symmetricDiffSize = symmetricDifference.cardinality();
		final int cost = Math.min(symmetricDiffSize, taxaNumber - symmetricDiffSize);
		
		final double branchLength1 = s1.node.getBranchLength();
		final double branchLength2 = s2.node.getBranchLength();
		
		return Math.min(branchLength1, branchLength2) * cost
				+ Math.max(0, branchLength1 - branchLength2) * smallerPartitionSize(s1.bitSet, taxaNumber)
				+ Math.max(0, branchLength2 - branchLength1) * smallerPartitionSize(s2.bitSet, taxaNumber);
	}

	private double smallerPartitionSize(BitSet bitSet, int taxaNumber) {
		final int firstPartitionSize = bitSet.cardinality();
		return Math.min(firstPartitionSize, taxaNumber - firstPartitionSize);
	}

}
