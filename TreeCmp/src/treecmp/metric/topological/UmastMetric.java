/**
 * This file is part of TreeCmp, a tool for comparing phylogenetic trees using
 * the Matching Split distance and other metrics. Copyright (C) 2011, Damian
 * Bogdanowicz
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package treecmp.metric.topological;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.NodeUtils;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import treecmp.common.LapSolver;
import treecmp.common.TreeCmpUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 * UMAST metric.
 * Implementation of Procedure 1
 * Farach, Martin and Thorup, Mikkel; Fast comparison of evolutionary trees.
 */
public class UMASTMetric extends BaseMetric implements Metric {
	@Override
    public boolean isRooted() {
        return false;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
    	int result = umast(new SimpleTreePreservingNodeNumbers(t1), new SimpleTreePreservingNodeNumbers(t2));
    
    	return Math.max(t1.getExternalNodeCount(), t2.getExternalNodeCount()) - result;
    }

	private static int umast(final SimpleTree tree1, final SimpleTree tree2) {
		if (tree1.getExternalNodeCount() <= 3 || tree2.getExternalNodeCount() <= 3) {
			return getLeafLabelsIntersectionSize(tree1, tree2);
		}
		
		final int k = 3;
    	final int t1MaxPartSize = tree1.getExternalNodeCount() / k;
    	final int t2MaxPartSize = tree2.getExternalNodeCount() / k;
    	final CoreTree core1 = new CoreTree(tree1, t1MaxPartSize);
    	final CoreTree core2 = new CoreTree(tree2, t2MaxPartSize);
    	
    	//partition side trees into balanced side forests of sizes between n/2k and n/k
    	final List<List<String>> sideForests1 = getBalancedSideForests(core1, t1MaxPartSize/2, t1MaxPartSize);
    	final List<List<String>> sideForests2 = getBalancedSideForests(core2, t2MaxPartSize/2, t2MaxPartSize);
    	
    	int result = 0;
    	
    	final TreeRestricter r1 = new TreeRestricter(tree1);
    	final TreeRestricter r2 = new TreeRestricter(tree2);
    	    	
    	//for all pairs (f1, f2) of opposing side forests: b = a(f1) + a(f2), umast = max(umast, umast(t1|b, t2|b)
    	for (final List<String> sideForest1 : sideForests1) {
    		for (final List<String> sideForest2 : sideForests2) {
    			final Set<String> union = new LinkedHashSet<String>(sideForest1);
    			union.addAll(sideForest2);
    			
    			final SimpleTree t1Restricted = r1.getRestrictedTo(union);
    			final SimpleTree t2Restricted = r2.getRestrictedTo(union);
    			
    			result = Math.max(result, umast(t1Restricted, t2Restricted));
    		}
    	}

		//for all pairs (l1,l2) of opposing core leaves: compute CRMAST(t1^l1,t2^l2)
		final CRMASTSet crmastSet = new CRMASTSet(tree1, tree2);
		final int c1ExternalNodes = core1.getExternalNodeCount();
		final int c2ExternalNodes = core2.getExternalNodeCount();
    	for (int i=0; i<c1ExternalNodes; i++) {
    		tree1.reroot(tree1.getInternalNode(core1.getExternalNodeIdx(i)));
    		for (int j=0; j<c2ExternalNodes; j++) {
    			tree2.reroot(tree2.getInternalNode(core2.getExternalNodeIdx(j)));
    			crmastSet.include(RMASTMetric.crmast(tree1, tree2), tree1, tree2);
    		}
    	}
    	
    	//for all pairs (v1,v2) of opposing core vertices: umast = max(umast, umast(v1,v2))
    	final int c1Nodes = core1.getNodeCount();
    	final int c2Nodes = core2.getNodeCount();
    	for (int i=0; i<c1Nodes; i++) {
    		final Node v1 = tree1.getInternalNode(core1.getNode(i));
    		for (int j=0; j<c2Nodes; j++) {
    			final Node v2 = tree2.getInternalNode(core2.getNode(j));
				result = Math.max(result, match(v1, v2, crmastSet));
    		}
    	}
		return result;
	}

	private static int getLeafLabelsIntersectionSize(Tree tree1, Tree tree2) {
		final Set<String> t1Labels = getLeafLabels(tree1);
		final Set<String> t2Labels = getLeafLabels(tree2);
		t1Labels.retainAll(t2Labels);
		return t1Labels.size();
	}

	private static Set<String> getLeafLabels(Tree tree) {
		final Set<String> labels = new HashSet<String>();
		for (int i=0; i<tree.getExternalNodeCount(); i++) {
			labels.add(tree.getExternalNode(i).getIdentifier().getName());
		}
		return labels;
	}

	private static List<List<String>> getBalancedSideForests(CoreTree core, int minSize, int maxSize) {
		final List<List<String>> forests = new ArrayList<List<String>>();
		List<String> smallForest = new ArrayList<>();
		for (int i=0; i<core.getSideTreeCount(); i++) {
			final List<String> sideTree = core.getSideTreeLeafLabels(i);
			if (sideTree.size() >= minSize) {
				forests.add(sideTree);
			} else {
				if (smallForest.size() + sideTree.size() < minSize) {
					smallForest.addAll(sideTree);
				} else {
					forests.add(smallForest);
					smallForest = sideTree;
				}
			}
		}
		if (!smallForest.isEmpty()) {
			forests.add(smallForest);
		}
		return forests;
	}

	//TODO verify proper complexity of match. For two trees all matchings should take O((kn)^1.5 log n + n^2).
	private static int match(Node v1, Node v2, CRMASTSet crmastSet) {
		final Node[] v1Neighbors = TreeCmpUtils.getNeighboringNodes(v1);
		final Node[] v2Neighbors = TreeCmpUtils.getNeighboringNodes(v2);
		final int size = Math.max(v1Neighbors.length, v2Neighbors.length);
		
		final int w[][] = new int[size][];
		for (int i=0; i<size; i++) {
			w[i] = new int[size];
		}
		
		for (int i=0; i<v1Neighbors.length; i++) {
			for (int j=0; j<v2Neighbors.length; j++) {
				w[i][j] = -crmastSet.getForEdgePair(v1, v1Neighbors[i], v2, v2Neighbors[j]);
			}
		}
		
		final int[] rowSol = new int[size];
		final int[] colSol = new int[size];
		final int[] u = new int[size];
		final int[] v = new int[size]; 
		return -LapSolver.lap(size, w, rowSol, colSol, u, v);
	}

	private static final class CoreTree {
		private List<List<String>> sideTrees = new ArrayList<List<String>>();
		private List<Integer> externalNodes = new ArrayList<Integer>();
		private List<Integer> internalNodes = new ArrayList<Integer>();
	
		public CoreTree(Tree tree, int maxSideTreeSize) {
			final boolean[] isInternalNodeInCore = getCoreNodes(tree, maxSideTreeSize);
			
			for (int i=0; i<isInternalNodeInCore.length; i++) {
				if (isInternalNodeInCore[i]) {
					final Node v = tree.getInternalNode(i);
					final Node[] neighbors = TreeCmpUtils.getNeighboringNodes(v);
					
					int numberOfCoreNeighbors = 0;
					for (final Node neighbor : neighbors) {
						if (!neighbor.isLeaf() && isInternalNodeInCore[neighbor.getNumber()]) {
							numberOfCoreNeighbors += 1;
						} else {
							sideTrees.add(subtreeLeafs(v, neighbor, tree.getInternalNodeCount()));
						}
					}
					if (numberOfCoreNeighbors > 1) {
						internalNodes.add(i);
					} else {
						externalNodes.add(i);
					}
				}
			}
		}

		public List<String> getSideTreeLeafLabels(int i) {
			return sideTrees.get(i);
		}
	
		public int getSideTreeCount() {
			return sideTrees.size();
		}
	
		public int getNodeCount() {
			return externalNodes.size() + internalNodes.size();
		}
	
		public int getNode(int i) {
			final int externalNodeCount = getExternalNodeCount();
			return i < externalNodeCount
					? externalNodes.get(i)
					: internalNodes.get(i - externalNodeCount);
		}
	
		public int getExternalNodeIdx(int i) {
			return externalNodes.get(i);
		}
	
		public int getExternalNodeCount() {
			return externalNodes.size();
		}
		
		private static boolean[] getCoreNodes(Tree tree, int maxSideTreeSize) {
			final int internalNodes = tree.getInternalNodeCount();
			
			final int externalNodes = tree.getExternalNodeCount();
			final boolean[] isCoreNode = new boolean[internalNodes];
			final boolean[] isCoreBelow = new boolean[internalNodes];
			final int[] subTreeSizes = new int[internalNodes];
			for (final Node node : TreeCmpUtils.getNodesInPostOrder(tree)) {
				final int subTreeSize = node.isLeaf()
						? 1
						: subTreeSizes[node.getNumber()];
				//determine if the node is in core tree
				if (!node.isLeaf() 
						&& subTreeSize >= maxSideTreeSize) {
					//core edge going to parent node
					if (externalNodes - subTreeSize >= maxSideTreeSize) { 
						isCoreNode[node.getNumber()] = true;
						if (!node.isRoot()) {
							isCoreNode[node.getParent().getNumber()] = true;
						}
					//or the core tree consists of a single node 
					} else if (!isCoreBelow[node.getNumber()]) {
						isCoreNode[node.getNumber()] = true;
					}
				}
				
				//update information about the parent node
				if (!node.isRoot()) {
					subTreeSizes[node.getParent().getNumber()] += subTreeSize;
					if (!node.isLeaf()) {
						isCoreBelow[node.getParent().getNumber()] |= isCoreBelow[node.getNumber()] | isCoreNode[node.getNumber()];
					}
				}
			}
			
			return isCoreNode;
		}
		
		private List<String> subtreeLeafs(Node cameFrom, Node start, int numberOfInternalNodes) {
			if (cameFrom.isLeaf()) {
				throw new IllegalArgumentException("Tree traversal has to start from an internal node.");
			}
			
			if (start.isLeaf()) {
				return Arrays.asList(start.getIdentifier().getName());
			}
			
			final boolean[] visited = new boolean[numberOfInternalNodes];
			final List<String> leafs = new ArrayList<String>();
			final Stack<Node> stack = new Stack<Node>();
			stack.add(start);
			visited[start.getNumber()] = visited[cameFrom.getNumber()] = true;
			
			while (!stack.isEmpty()) {
				final Node v = stack.pop();
				for (final Node neighbor : TreeCmpUtils.getNeighboringNodes(v)) {
					if (neighbor.isLeaf()) {
						leafs.add(neighbor.getIdentifier().getName());
					} else if (!visited[neighbor.getNumber()]) {
						stack.push(neighbor);
						visited[neighbor.getNumber()] = true;
					}
				}
			}
			return leafs;
		}
	}
	
	private static final class CRMASTSet {
		private final int[][][][] values;
		private final int t1Leafs;
		private final int t2Leafs;
		private final int t1Nodes;
		private final int t2Nodes;
		
		public CRMASTSet(Tree t1, Tree t2) {
			t1Leafs = t1.getExternalNodeCount();
			t2Leafs = t2.getExternalNodeCount();
			t1Nodes = t1Leafs + t1.getInternalNodeCount();
			t2Nodes = t2Leafs + t2.getInternalNodeCount();
			values = new int[t1Nodes][][][];
		}
		
		public int getForEdgePair(Node v1, Node w1, Node v2, Node w2) {
			return values[getT1Index(v1)][getT1Index(w1)][getT2Index(v2)][getT2Index(w2)];
		}
	
		public void include(CRMAST crmast, Tree tree1, Tree tree2) {
			for (final Node v1 : TreeCmpUtils.getAllNodes(tree1)) {
				if (!v1.isRoot()) {
					final Node u1 = v1.getParent();
					final int u1Idx = getT1Index(u1);
					final int v1Idx = getT1Index(v1);
					if (values[u1Idx] == null) {
						values[u1Idx] = new int[t1Nodes][][];
					}
					if (values[u1Idx][v1Idx] == null) {
						values[u1Idx][v1Idx] = new int[t2Nodes][];
					}
					for (final Node v2 : TreeCmpUtils.getAllNodes(tree2)) {
						if (!v2.isRoot()) {
							final Node u2 = v2.getParent();
							final int u2Idx = getT2Index(u2);
							final int v2Idx = getT2Index(v2);
							if (values[u1Idx][v1Idx][u2Idx] == null) {
								values[u1Idx][v1Idx][u2Idx] = new int[t2Nodes];
							}
							values[u1Idx][v1Idx][u2Idx][v2Idx] = crmast.getRMAST(v1, v2);
						}
					}
				}
			}
		}

		private int getT1Index(Node v) {
			return v.isLeaf()
					? v.getNumber()
					: t1Leafs + v.getNumber();
		}

		private int getT2Index(Node v) {
			return v.isLeaf()
					? v.getNumber()
					: t2Leafs + v.getNumber();
		}
	}
	
	//TODO implement Harel, D. and Tarjan, R.E.; Fast algorithms for finding nearest common ancestor.  
	private static final class LCACalculator {
		public LCACalculator(Tree tree) {
			// TODO Auto-generated constructor stub
		}

		public Node forLeafs(Node u, Node v) {
			return NodeUtils.getFirstCommonAncestor(u, v);
		}		
	}
	
	private static final class SimpleTreePreservingNodeNumbers extends SimpleTree {
		private static final long serialVersionUID = -4057750013155408632L;
		private boolean initialized = false;

		public SimpleTreePreservingNodeNumbers(Tree tree) {
			super(tree);
			initialized = true;
		}

		@Override
		public void createNodeList() {
			if (!initialized) {
				super.createNodeList();
			}
		}
	}
	
	private static final class TreeRestricter {
		private final LCACalculator lca;
		private final int[] internalNodeDepths;
		private final Node[] leafOrder;
		
		public TreeRestricter(Tree tree) {
			lca = new LCACalculator(tree);
			
			internalNodeDepths = new int[tree.getInternalNodeCount()];
			leafOrder = new Node[tree.getExternalNodeCount()];
			
			int leafs = 0;
			for (final Node node : TreeCmpUtils.getNodesInPreOrder(tree)) {
				if (node.isLeaf()) {
					leafOrder[leafs++] = node;
				} else if (!node.isRoot()) {	//is internal
					internalNodeDepths[node.getNumber()]
							= internalNodeDepths[node.getParent().getNumber()] + 1;
				}
			}
		}
	
		public SimpleTree getRestrictedTo(Set<String> leafNames) {
			final Node[] leafs = getOrderedLeafs(leafNames);
			final int[] lcaDepths = new int[leafs.length - 1];
			for (int i=1; i<leafs.length; i++) {
				final Node u = lca.forLeafs(leafs[i-1], leafs[i]); 
				lcaDepths[i-1] = internalNodeDepths[u.getNumber()];
			}
			
			final Node root = construct(leafs, lcaDepths, 0, leafs.length, 0);
			return new SimpleTree(root);
		}

		private Node construct(Node[] leafs, int[] lcaDepths, int l, int r, int minDepth) {
			final Node u = NodeFactory.createNode(leafs[l].getIdentifier());
			Node parent = null;
			while (l<r-1 && lcaDepths[l] >= minDepth) {
				int i=l+1;
				while (i < r-1 && lcaDepths[i] > lcaDepths[l]) {
					i++;
				}
				if (parent == null) {
					parent = NodeFactory.createNode();
					parent.addChild(u);
				}
				final Node subTree = construct(leafs, lcaDepths, l+1, i, lcaDepths[l]);
				parent.addChild(subTree);
				l = i;
			}
			return parent != null
					? parent
					: u;
		}

		private Node[] getOrderedLeafs(Set<String> leafNames) {
			final Node[] leafs = new Node[leafNames.size()];
			int i=0;
			for (final Node node : leafOrder) {
				if (leafNames.contains(node.getIdentifier().getName())) {
					leafs[i++] = node;
				}
			}
			return leafs;
		}
	}
}