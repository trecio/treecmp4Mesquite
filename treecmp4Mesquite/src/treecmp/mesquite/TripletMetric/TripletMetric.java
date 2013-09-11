package treecmp.mesquite.TripletMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import treecmp.mesquite.BaseForRootedTreeCmpMetric;
import treecmp.mesquite.PalFacade;
import treecmp.mesquite.PalFacade.Tree;

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
		return "Triplet Tree Distance (rooted) [TREECMP]";
	}

}
