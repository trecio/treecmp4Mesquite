package mesquite.treecmp.metrics.MatchingClusterMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;
import mesquite.treecmp.metrics.PalFacade.TreeCmpMetric;

public class MatchingClusterMetric extends BaseForRootedTreeCmpMetric {
	private final TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.topological.MatchingClusterMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		double distance = metric.getDistance(t1, t2);

		result.setValue(distance);
		if (resultString!= null) {
			resultString.setValue("Matching Cluster tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Matching Cluster Tree Difference (Rooted). [TREECMP]";
	}
}
