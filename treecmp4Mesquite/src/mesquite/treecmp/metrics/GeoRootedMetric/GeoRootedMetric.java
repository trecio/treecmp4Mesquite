package mesquite.treecmp.metrics.GeoRootedMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;
import mesquite.treecmp.metrics.PalFacade.TreeCmpMetric;

public class GeoRootedMetric extends BaseForRootedTreeCmpMetric {
	private final TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.weighted.GeoRootedMetric");

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null) {
			string.setName("Geodesic tree distance: " + distance);
		}		
	}

	@Override
	public String getName() {
		return "Geodesic Tree Difference (Rooted). [TREECMP]";
	}

}
