package mesquite.treecmp.metrics.RMAST;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class RMAST extends BaseForRootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.topological.RMASTMetric");

	@Override
	protected void getDistance(Tree palT1, Tree palT2, MesquiteNumber result,
			MesquiteString resultString) {
		final double value = metric.getDistance(palT1, palT2);
		
		result.setValue(value);
		if (resultString != null) {
			resultString.setValue("RMAST distance: " + value);
		}
	}

	@Override
	public String getName() {
		return "Maximum Agreement Subtree Distance (Rooted) [TREECMP]";
	}

}
