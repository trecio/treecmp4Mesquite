package mesquite.treecmp.metrics.WeightedMatchingSplitMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class WeightedMatchingSplitMetric extends BaseForUnrootedTreeCmpMetric {
	public WeightedMatchingSplitMetric() {
		super(true);
	}

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.WeightedMatchingSplitMetric");
		
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null) {
			string.setName("Weighted Matching Split tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Weighted Matching Split Tree Distance (Unrooted) [TREECMP]";
	}

}
