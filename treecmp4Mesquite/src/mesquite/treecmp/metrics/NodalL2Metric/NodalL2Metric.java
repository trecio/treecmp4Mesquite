package mesquite.treecmp.metrics.NodalL2Metric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.PalFacade;
import mesquite.treecmp.PalFacade.Tree;

public class NodalL2Metric extends BaseForUnrootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.NodalUnrootedL2Metric");
		
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
