package mesquite.treecmp.metrics.MatchingSplitMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.PalFacade;
import mesquite.treecmp.PalFacade.Tree;

public class MatchingSplitMetric extends BaseForUnrootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingSplitMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null) {
			string.setValue("Matching Split tree distance: " + distance);
		}

	}

	@Override
	public String getName() {
		return "Matching Split Tree Distance (Unrooted) [TREECMP]";
	}

}
