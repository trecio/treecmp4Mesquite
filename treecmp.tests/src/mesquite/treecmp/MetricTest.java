package mesquite.treecmp;

import org.junit.Before;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;

public abstract class MetricTest<TMetric extends DistanceBetween2Trees> {
	@Before public void createSut() {
		metric = createMetric();
		metric.startJob(null, null, false);
	}
	
	private TMetric metric;
	
	protected abstract TMetric createMetric();
	
	protected double getMetricValue(String tree1Description, String tree2Description) {
		Tree t1 = TreeConverter.getMesquiteFrom(tree1Description);
		Tree t2 = TreeConverter.getMesquiteFrom(tree2Description, t1.getTaxa());
		MesquiteNumber result = new MesquiteNumber();
		metric.calculateNumber(t1, t2, result , null);
		return result.getDoubleValue();
	}
}
