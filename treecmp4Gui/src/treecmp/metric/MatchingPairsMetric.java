package treecmp.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.LapSolver;
import treecmp.common.TreeCmpUtils;
import treecmp.qt.Leaf;

public class MatchingPairsMetric extends BaseMetric implements Metric {

	@Override
	public double getDistance(Tree t1, Tree t2) {
		final CostMatrixBuilder matrixBuilder = new CostMatrixBuilder(t1, t2);
		final int[][] matrix = matrixBuilder.build();		
		final int size = matrix.length;
		
		final int[] rowsol, colsol, u, v;
		rowsol = new int[size];
		colsol = new int[size];
		u = new int[size];
		v = new int[size];
		
		return LapSolver.lap(size, matrix, rowsol, colsol, u, v);
	}
	
	private class CostMatrixBuilder {
		private final Tree t1, t2;

		public CostMatrixBuilder(Tree t1, Tree t2) {
			this.t1 = t1;
			this.t2 = t2;			
		}

		public int[][] build() {
			final int size = Math.max(t1.getInternalNodeCount(), t2.getInternalNodeCount());
			final int[][] result = new int[size][];
			for (int i=0; i<size; i++)
				result[i] = new int[size];
			
			final IdGroup t1Ids = TreeUtils.getLeafIdGroup(t1);
			final IdGroup t2Ids = TreeUtils.getLeafIdGroup(t2);
			final IdGroup mergedIds = TreeCmpUtils.mergeIdGroups(t1Ids, t2Ids);

			final int[][] lcaInT1 = calculateLca(t1, mergedIds);
			final int[][] lcaInT2 = calculateLca(t2, mergedIds);
			
			final int[] howManyTimesIsLcaInT1 = new int[size];
			final int[] howManyTimesIsLcaInT2 = new int[size];

			for (int i=0; i<lcaInT1.length; i++)
				for (int j=0; j<lcaInT2.length; j++) {
					final int lcaInT1Id = lcaInT1[i][j];
					final int lcaInT2Id = lcaInT2[i][j];
					
					result[lcaInT1Id][lcaInT2Id]++;
					
					howManyTimesIsLcaInT1[lcaInT1Id]++;
					howManyTimesIsLcaInT2[lcaInT2Id]++;
				}
			
			for (int i=0; i<size; i++)
				for (int j=0; j<size; j++)
					result[i][j] = howManyTimesIsLcaInT1[i] + howManyTimesIsLcaInT2[j] - 2 * result[i][j];
			
			return result;
		}
		
		private int[][] calculateLca(Tree t, IdGroup ids) {
			final int size = ids.getIdCount();
			
			final Node[] nodes = TreeCmpUtils.getNodesInPostOrder(t);
			
			final int[][] result = new int[size][];
			for (int i=0; i<size; i++) {
				result[i] = new int[size];
				Arrays.fill(result[i], -1);
			}
			
			final Map<Node, ArrayList<Leaf>> clades = new HashMap<Node, ArrayList<Leaf>>();
			
			int lcaId = 0;
			for (final Node node : nodes) {
				final int childCount = node.getChildCount();
				if (!(node instanceof Leaf)) {
					List<ArrayList<Leaf>> childClades = new ArrayList<ArrayList<Leaf>>(childCount);
					int allChildCount = 0;
					for (int i=0; i<node.getChildCount(); i++) {
						ArrayList<Leaf> childClade = clades.get(node.getChild(i));
						clades.put(node.getChild(i), null);
						childClades.add(childClade);
						allChildCount += childClade.size();
					}					
					
					setupLcaForPairsOfLeafs(ids, result, lcaId, childClades);
					lcaId++;
					
					final ArrayList<Leaf> clade = flattenToFirstList(childClades, allChildCount);
					clades.put(node, clade);
				} else {
					ArrayList<Leaf> clade = new ArrayList<Leaf>();
					clade.add((Leaf)node);
					clades.put(node, clade);
				}
			}
			
			return result;
		}

		private void setupLcaForPairsOfLeafs(IdGroup ids, int[][] result,
				int lcaId, List<ArrayList<Leaf>> childClades) {
			for (int i=0; i<childClades.size(); i++) {
				final List<Leaf> firstChildClade = childClades.get(i);
				for (int j=0; j<childClades.size(); j++) {
					final List<Leaf> secondChildClade = childClades.get(j);
					for (final Leaf leafFromFirstChildClade : firstChildClade) {
						final int idOfLeafFromFirstChildClade = ids.whichIdNumber(leafFromFirstChildClade.toString());
						for (final Leaf leafFromSecondChildClade : secondChildClade) {
							final int idOfLeafFromSecondChildClade = ids.whichIdNumber(leafFromSecondChildClade.toString());
							result[idOfLeafFromFirstChildClade][idOfLeafFromSecondChildClade]
									= result[idOfLeafFromSecondChildClade][idOfLeafFromFirstChildClade]
											= lcaId;
						}
					}
				}
			}
		}
		
		private <T> ArrayList<T> flattenToFirstList(List<ArrayList<T>> lists, int expectedSize) {
			ArrayList<T> result = null;
			
			for (final ArrayList<T> list : lists)
				if (result != null)
					result.addAll(list);
				else {
					result = list;
					result.ensureCapacity(expectedSize);
				}
			
			return result;
		}
	}
}
