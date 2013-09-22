package mesquite.treecmp.metrics.TripletMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;

public class TripletMetric extends BaseForRootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.TripletMetric");
		
		double distance = metric.getDistance(t1, t2);

		result.setValue(distance);
		if (resultString != null)
			resultString.setValue("Triples tree distance: " + distance);
	}

	@Override
	public String getName() {
		return "Triplet Tree Distance (Rooted) [TREECMP]";
	}

}
