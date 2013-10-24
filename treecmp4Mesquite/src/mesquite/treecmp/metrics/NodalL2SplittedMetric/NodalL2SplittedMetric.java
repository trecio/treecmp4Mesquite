package mesquite.treecmp.metrics.NodalL2SplittedMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForUnrootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class NodalL2SplittedMetric extends BaseForUnrootedTreeCmpMetric {
	private final PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.topological.NodalL2SplittedMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null) {
			string.setValue("Nodal L2-norm Splitted tree distance: " + distance);
		}

	}

	@Override
	public String getName() {
		return "Nodal L2-norm Splitted Tree Distance (Rooted) [TREECMP]";
	}

}
