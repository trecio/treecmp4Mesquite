package mesquite.treecmp.metrics.FlowSplitMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;
import mesquite.treecmp.metrics.PalFacade.TreeCmpMetric;

public class FlowSplitMetric extends BaseForUnrootedTreeCmpMetric {
	private final TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.weighted.FlowClusterMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null) {
			string.setName("Flow Split tree distance: " + distance);
		}		
	}

	@Override
	public String getName() {
		return "Flow Split Tree Difference (Unrooted). [TREECMP]";
	}

}
