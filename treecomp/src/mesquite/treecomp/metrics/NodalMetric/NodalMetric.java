package mesquite.treecomp.metrics.NodalMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.metrics.BaseForTreeCmpMetric;

public class NodalMetric extends BaseForTreeCmpMetric {	
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	protected void getDistanceForRootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		throw new IllegalArgumentException("Nodal metric is available only for unrooted trees.");
	}

	@Override
	protected void getDistanceForUnrootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.NodalUnrootedMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Nodal (L1 norm) tree distance: " + distance);
	}
	
	private final String MODULE_NAME = "Nodal (L1 norm) distance.";		
}
