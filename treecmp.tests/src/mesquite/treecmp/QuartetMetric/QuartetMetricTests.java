package mesquite.treecmp.QuartetMetric;


import junit.framework.Assert;
import mesquite.treecmp.MetricTest;

import org.junit.Test;

public class QuartetMetricTests extends MetricTest<QuartetMetric> {
	@Override
	protected QuartetMetric createMetric() {
		return new QuartetMetric();
	}

	@Test
	public void TwoTrees() {
		String t1 = "((A,B),C,D);";
		String t2 = "(A,(B,C),D);";

		Assert.assertEquals(1., getMetricValue(t1, t2));
	}
	
	@Test
	public void SameTrees() {
		String t1 = "((A,B),C,D);";

		Assert.assertEquals(0., getMetricValue(t1, t1));
	}

	@Test
	public void TwoIsomorphicTrees() {
		String t1 = "((A,B),C,D);";
		String t2 = "(C,(A,B),D);";

		Assert.assertEquals(0., getMetricValue(t1, t2));
	}
	
	@Test
	public void TwoTreesWithExtraLeafs () {
		String t1 = "((A,B),C,(D,E));";
		String t2 = "(A,(B,(C,F)),D);";

		Assert.assertEquals(9., getMetricValue(t1, t2));
	}
	
	@Test
	public void TwoFourLeafTreesWithExtraLeaf() {
		String t1 = "((A,B),C,D);";
		String t2 = "((A,B),C,E);";

		Assert.assertEquals(1., getMetricValue(t1, t2));
	}
		
	@Test
	public void NonBinaryTreeOfFourLeaves() {
		String t2 = "((A,B),C,D);";
		String t1 = "(A,B,C,D);";

		Assert.assertEquals(1., getMetricValue(t1, t2));
	}
	
	@Test
	public void NonBinaryTreeOfFiveLeafs() {
		String t2 = "((A,B),C,(D,E));";
		String t1 = "((A,B,C,D),E);";

		Assert.assertEquals(5., getMetricValue(t1, t2));
	}
	
	@Test
	public void AscaridoidDataSample() {
		String t1 = "(Asuu,((Azip,Hbid),Racu),Hadu1);";
		String t2 = "((Asuu,(Azip,Hbid)),Racu,Hadu1);";
		
		Assert.assertEquals(2., getMetricValue(t1, t2));
	}
}
