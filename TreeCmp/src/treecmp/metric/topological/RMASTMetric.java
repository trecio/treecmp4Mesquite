package treecmp.metric.topological;

import pal.tree.Node;
import pal.tree.Tree;
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 * RMAST metric.
 * Implementation of Procedure 3
 * Farach, Martin and Thorup, Mikkel; Fast comparison of evolutionary trees. 
 */
public class RMASTMetric extends BaseMetric implements Metric {

	@Override
	public boolean isRooted() {
		return true;
	}

	@Override
	public double getDistance(Tree t1, Tree t2) {
		return Math.max(t1.getExternalNodeCount(), t2.getExternalNodeCount())
				- crmast(t1, t2).getRMAST();
	}
	
	public static CRMAST crmast(Tree t1, Tree t2) {
		final int t1Leafs = t1.getExternalNodeCount();
		final int t2Leafs = t2.getExternalNodeCount();
		
		//dynamic programming array: first dimension are nodes from t1, second from t2
		//nodes are ordered according to their numbers: leafs first then internal nodes
		final CRMAST mast = new CRMAST(t1, t2);

		//fill values for pairs where at least one of nodes is a leaf 
		for (int i=0; i<t1Leafs; i++) {
			final Node leaf1 = t1.getExternalNode(i);
			for (int j=0; j<t2Leafs; j++) {
				final Node leaf2 = t2.getExternalNode(j);
				
				if (leaf1.getIdentifier().equals(leaf2.getIdentifier())) {
					mast.set(i, j, 1);
					
					Node v1 = leaf1;
					do {
						v1 = v1.getParent();
						mast.set(v1, leaf2, 1);
					} while (v1 != t1.getRoot());
					
					Node v2 = leaf2;
					do {
						v2 = v2.getParent();
						mast.set(leaf1, v2, 1);
					} while (v2 != t2.getRoot());
				}
			}
		}
		
		final int[] internalNodeOrder1 = getInternalNodeOrder(t1);
		final int[] internalNodeOrder2 = getInternalNodeOrder(t2);
		//fill values for pairs of internal nodes
		for (int i=0; i<internalNodeOrder1.length; i++) {
			final Node v1 = t1.getInternalNode(internalNodeOrder1[i]);
			for (int j=0; j<internalNodeOrder2.length; j++) {
				final Node v2 = t2.getInternalNode(internalNodeOrder2[j]);
				mast.set(v1, v2, Math.max(diag(v1, t1, v2, t2, mast), match(v1, t1, v2, t2, mast)));
			}
		}
		
		return mast;
	}

	private static int diag(Node v1, Tree t1, Node v2, Tree t2, CRMAST mast) {
		int result = 0;
		for (int i=0; i<v2.getChildCount(); i++) {
			final Node child2 = v2.getChild(i);
			result = Math.max(result, mast.getRMAST(v1, child2));
		}
		for (int i=0; i<v1.getChildCount(); i++) {
			final Node child1 = v1.getChild(i);
			result = Math.max(result, mast.getRMAST(child1, v2));		
		}
		return result;
	}

	//TODO verify proper complexity of match. For two trees all matchings should take O(n^2). 
	private static int match(Node v1, Tree t1, Node v2, Tree t2, CRMAST mast) {
		final int v1Children = v1.getChildCount();
		final int v2Children = v2.getChildCount();
		final int size = Math.max(v1Children, v2Children);
		final int[][] w = new int[size][];
		for (int i=0; i<size; i++) {
			w[i] = new int[size];
		}
		
		for (int i=0; i<v1Children; i++) {
			final Node child1 = v1.getChild(i);
			for (int j=0; j<v2Children; j++) {
				final Node child2 = v2.getChild(j);
				w[i][j] = -mast.getRMAST(child1, child2);
			}
		}

		final int[] rowSol = new int[size];
		final int[] colSol = new int[size];
		final int[] u = new int[size];
		final int[] v = new int[size]; 
		return -LapSolver.lap(size, w, rowSol, colSol, u, v);
	}
	
	private static int[] getInternalNodeOrder(Tree t) {
		final int[] order = new int[t.getInternalNodeCount()];
		int qFront = order.length;
		int qEnd = order.length;
		
		//visit internal nodes in bfs order starting from root
		//and grow the order queue from the back
		order[--qEnd] = t.getRoot().getNumber();
		while (qFront > 0) {	
			final Node v = t.getInternalNode(order[--qFront]);
			for (int i=0; i<v.getChildCount(); i++) {
				final Node child = v.getChild(i);
				if (!child.isLeaf()) {
					order[--qEnd] = child.getNumber();
				}
			}
		}
		return order;
	}

}

final class CRMAST {
	private final int[][] mast;
	private final int t1RootIdx;
	private final int t2RootIdx;
	private final int t1ExternalNodeCount;
	private final int t2ExternalNodeCount;

	public CRMAST(Tree t1, Tree t2) {
		t1ExternalNodeCount = t1.getExternalNodeCount();
		t2ExternalNodeCount = t2.getExternalNodeCount();
		final int t1Nodes = t1ExternalNodeCount + t1.getInternalNodeCount();
		final int t2Nodes = t2ExternalNodeCount + t2.getInternalNodeCount();
		mast = new int[t1Nodes][];
		for (int i=0; i<t1Nodes; i++) {
			mast[i] = new int[t2Nodes];
		}
		t1RootIdx = getInternalNodeIdx(t1.getRoot(), t1ExternalNodeCount);
		t2RootIdx = getInternalNodeIdx(t2.getRoot(), t2ExternalNodeCount);
	}

	public int getRMAST() {
		return mast[t1RootIdx][t2RootIdx];
	}

	public int getRMAST(Node v1, Node v2) {
		return mast[getNodeIdx(v1, t1ExternalNodeCount)][getNodeIdx(v2, t2ExternalNodeCount)];
	}

	public void set(int i, int j, int value) {
		mast[i][j] = value;
	}

	public void set(Node v1, Node v2, int value) {
		mast[getNodeIdx(v1, t1ExternalNodeCount)][getNodeIdx(v2, t2ExternalNodeCount)] = value;
	}

	private static int getInternalNodeIdx(Node v, int externalNodeCount) {
		return externalNodeCount + v.getNumber();
	}

	private static int getNodeIdx(Node v, int externalNodeCount) {
		return v.isLeaf()
				? v.getNumber()
				: getInternalNodeIdx(v, externalNodeCount);
	}
}