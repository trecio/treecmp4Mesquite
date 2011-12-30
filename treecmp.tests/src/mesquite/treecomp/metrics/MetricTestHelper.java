package mesquite.treecomp.metrics;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.treecomp.common.TreeConverter;

public class MetricTestHelper {
	private NumberFor2Trees metric;

	public MetricTestHelper(NumberFor2Trees metric) {
		this.metric = metric;
	}
	
	public double getDistance(String t1Desc, String t2Desc) {
		Tree t1 = TreeConverter.getMesquiteFrom(t1Desc);
		Tree t2 = TreeConverter.getMesquiteFrom(t2Desc, t1.getTaxa());
		
		MesquiteNumber number = new MesquiteNumber();
		MesquiteString string = new MesquiteString();
		
		metric.startJob("", null, false);
		metric.initialize(t1, t2);
		metric.calculateNumber(t1, t2, number, string);
		return number.getDoubleValue();
	}
}
