package treecmp.r;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class RFMetricTestCase {
	RFUMetric metric;

	@Before
	public void setUp() throws Exception {
		metric = new RFUMetric();
	}
	
	@Test
	public void TestTreeWith5Taxa() {
		String t1 = "((1,2),3,(4,5));";
		String t2 = "((1,5),4,(2,3));";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(2., result);
	}
	
	@Test
	public void TestTreeWith6Taxa() {
		String t1 = "((1,6),2,(5,(3,4)));";
		String t2 = "((1,2),(3,4),(5,6));";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(2., result);
	}
}
