package mesquite.treecmp.metrics.NodalL2Metric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class NodalL2Metric extends BaseForUnrootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.topological.NodalL2Metric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null) {
			string.setValue("Nodal L2-norm tree distance: " + distance);
		}

	}

	@Override
	public String getName() {
		return "Nodal L2-norm Tree Distance (Unrooted) [TREECMP]";
	}

}
