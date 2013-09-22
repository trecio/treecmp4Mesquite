package mesquite.treecmp.metrics.MatchingClusterMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class MatchingClusterMetric extends BaseForRootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingClusterMetric");
		
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
