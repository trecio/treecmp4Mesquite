package mesquite.treecmp.clustering.KMeansAvgDistanceTreeClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.Tree;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.clustering.AbstractKCentroidMeans;

public class KMeansAvg extends AbstractKCentroidMeans<Collection<Integer>> {

	private final DistanceBetween2Trees distance;
	private final Trees trees;

	public KMeansAvg(Trees trees, DistanceBetween2Trees distance) {
		this.distance = distance;
		this.trees = trees;
	}

	@Override
	protected List<Collection<Integer>> computeCentres(
			List<Collection<Integer>> associations) {
		return new ArrayList<Collection<Integer>>(associations);
	}

	@Override
	protected double getDistanceFromCenterToTree(Collection<Integer> center, Collection<Integer> tree) {
		double sumDistances = 0;
		MesquiteNumber number = new MesquiteNumber();
		for (final Integer idxInTree : tree) {
			final Tree treeObject = trees.getTree(idxInTree);
			for (final Integer idxInCenter : center) {
				final Tree treeInCenter = trees.getTree(idxInCenter);
				distance.calculateNumber(treeObject, treeInCenter, number, null);
				sumDistances += number.getDoubleValue();
			}
		}
		return sumDistances / center.size() / tree.size();
	}

}
