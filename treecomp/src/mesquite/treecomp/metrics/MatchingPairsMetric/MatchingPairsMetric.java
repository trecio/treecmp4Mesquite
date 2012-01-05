package mesquite.treecomp.metrics.MatchingPairsMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.metrics.BaseForTreeCmpMetric;

public class MatchingPairsMetric extends BaseForTreeCmpMetric {	

	public String getName() {
		return MODULE_NAME;
	}
	
	@Override
	protected void getDistanceForRootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingPairsMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Matching pairs tree distance: " + distance);		
	}

	@Override
	protected void getDistanceForUnrootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		throw new IllegalArgumentException("Matching pairs metric can be calculated only for rooted trees.");
	}
	
	private final String MODULE_NAME = "Matching Pairs Distance";
}
