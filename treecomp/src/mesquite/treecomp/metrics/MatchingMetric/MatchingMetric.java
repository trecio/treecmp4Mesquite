package mesquite.treecomp.metrics.MatchingMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.metrics.BaseForTreeCmpMetric;

public class MatchingMetric extends BaseForTreeCmpMetric {	

	public String getName() {
		return MODULE_NAME;
	}
	
	@Override
	protected void getDistanceForRootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.bpmetric.MatchingClusterOptMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Matching cluster tree distance: " + distance);		
	}

	@Override
	protected void getDistanceForUnrootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.bpmetric.MatchingSplitOptMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Matching split tree distance: " + distance);
	}
	
	private final String MODULE_NAME = "Matching Split/Cluster Distance";
}
