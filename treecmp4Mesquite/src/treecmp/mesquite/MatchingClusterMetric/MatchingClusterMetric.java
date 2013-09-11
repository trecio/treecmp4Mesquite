package treecmp.mesquite.MatchingClusterMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import treecmp.mesquite.BaseForRootedTreeCmpMetric;
import treecmp.mesquite.PalFacade;
import treecmp.mesquite.PalFacade.Tree;

public class MatchingClusterMetric extends BaseForRootedTreeCmpMetric {

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.MatchingClusterMetric");
		
		double distance = metric.getDistance(t1, t2);

		result.setValue(distance);
		if (resultString!= null) {
			resultString.setValue("Matching Cluster tree distance: " + distance);
		}
	}

	@Override
	public String getName() {
		return "Matching Cluster Tree Difference (Rooted). [TREECMP]";
	}
}
