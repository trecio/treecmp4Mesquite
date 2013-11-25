package mesquite.treecmp.metrics.FlowClusterMetric;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.treecmp.metrics.BaseForRootedTreeCmpMetric;
import mesquite.treecmp.metrics.PalFacade;
import mesquite.treecmp.metrics.PalFacade.Tree;
import mesquite.treecmp.metrics.PalFacade.TreeCmpMetric;

public class FlowClusterMetric extends BaseForRootedTreeCmpMetric {
	private final TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.weighted.FlowClusterMetric");
	
	public FlowClusterMetric() {
		super(true);
	}

	@Override
	protected void getDistance(Tree t1, Tree t2, MesquiteNumber number,
			MesquiteString string) {
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null) {
			string.setName("Flow Cluster tree distance: " + distance);
		}		
	}

	@Override
	public String getName() {
		return "Flow Cluster Tree Difference (Rooted). [TREECMP]";
	}

}
