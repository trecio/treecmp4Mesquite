package treecmp.mesquite.MatchingPairsMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import treecmp.mesquite.BaseForRootedTreeCmpMetric;
import treecmp.mesquite.PalFacade;
import treecmp.mesquite.PalFacade.Tree;

public class MatchingPairsMetric extends BaseForRootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingPairsMetric");
		
		double distance = metric.getDistance(t1, t2);

		result.setValue(distance);
		if (resultString != null) {
			resultString.setValue("Matching Pairs tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Matching Pairs Tree Distance (Rooted). [TREECMP]";
	}

}
