package treecmp.mesquite.QuartetMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import treecmp.mesquite.BaseForUnrootedTreeCmpMetric;
import treecmp.mesquite.PalFacade;
import treecmp.mesquite.PalFacade.Tree;

public class QuartedMetric extends BaseForUnrootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.QuartetMetricLong");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Quartet tree distance: " + distance);

	}

	@Override
	public String getName() {
		return "Quartet Tree Distance (unrooted) [TREECMP]";
	}

}
