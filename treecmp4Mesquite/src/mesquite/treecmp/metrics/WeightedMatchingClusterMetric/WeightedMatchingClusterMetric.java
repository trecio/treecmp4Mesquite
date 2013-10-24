package mesquite.treecmp.metrics.WeightedMatchingClusterMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class WeightedMatchingClusterMetric extends BaseForRootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.weighted.MatchingClusterWeightMetric");

	public WeightedMatchingClusterMetric() {
		super(true);
	}

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null) {
			string.setName("Weighted Matching Cluster tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Weighted Matching Cluster Tree Distance (Rooted) [TREECMP]";
	}

}
