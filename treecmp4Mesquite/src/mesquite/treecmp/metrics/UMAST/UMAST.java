package mesquite.treecmp.metrics.UMAST;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class UMAST extends BaseForUnrootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.topological.UMASTMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		final double value = metric.getDistance(t1, t2);
		
		result.setValue(value);
		if (resultString != null) {
			resultString.setValue("UMAST distance: " + value);
		}
	}

	@Override
	public String getName() {
		return "Maximum Agreement Subtree Distance (Unrooted) [TREECMP]";
	}

}
