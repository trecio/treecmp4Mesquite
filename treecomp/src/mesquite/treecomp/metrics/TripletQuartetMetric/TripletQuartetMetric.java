package mesquite.treecomp.metrics.TripletQuartetMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.metrics.BaseForTreeCmpMetric;

public class TripletQuartetMetric extends BaseForTreeCmpMetric {	

	public String getName() {
		return MODULE_NAME;
	}	

	@Override
	protected void getDistanceForRootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.TripletMetric");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Triples tree distance: " + distance);		
	}

	@Override
	protected void getDistanceForUnrootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.QuartetMetricLong");
		
		double distance = metric.getDistance(t1, t2);

		number.setValue(distance);
		if (string != null)
			string.setValue("Quartets tree distance: " + distance);		
	}
		
	private final String MODULE_NAME = "Triples/Quartets Distance";
}
