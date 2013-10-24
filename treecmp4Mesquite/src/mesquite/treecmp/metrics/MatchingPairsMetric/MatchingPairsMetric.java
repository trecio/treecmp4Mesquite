package mesquite.treecmp.metrics.MatchingPairsMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class MatchingPairsMetric extends BaseForRootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingPairsMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		double distance = metric.getDistance(t1, t2);

		result.setValue(distance);
		if (resultString != null) {
			resultString.setValue("Matching Pairs tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Matching Pairs Tree Distance (Rooted) [TREECMP]";
	}

}
