package treecmp.mesquite.NodalL2Metric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import treecmp.mesquite.BaseForUnrootedTreeCmpMetric;
import treecmp.mesquite.PalFacade;
import treecmp.mesquite.PalFacade.Tree;

public class NodalL2TMetric extends BaseForUnrootedTreeCmpMetric {

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
		return "Nodal L1-norm Tree Distance (unrooted) [TREECMP]";
	}

}
