package treecmp.r;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MatchingSplitMetricTests {
	private MatchingSplitUMetric metric;

	@Before
	public void setUp() {
		metric = new MatchingSplitUMetric();
	}
	
	@Test
	public void SameTreesShouldHaveDistance0() {
		String t = "((A:1,B:2):3,C:4,D:5);";
		
		double distance = metric.getDistance(t, t);
		Assert.assertEquals(0., distance);
	}
	
	@Test
	public void IsomorphicTreesShouldHaveDistance0() {
		String t1 = "((A:1,B:2):3,C:4,D:5);";
		String t2 = "((C:4,D:5):3,A:1,B:2);";
	
		double distance = metric.getDistance(t1, t2);
		Assert.assertEquals(0., distance);
	}
	
	@Test
	public void TwoTreesWithFourLeafsWithSameSplits() {
		String t1 = "((A:1,B:2):3,C:4,D:5);";
		String t2 = "((A:1,B:2):6,C:4,D:5);";
		
		double distance = metric.getDistance(t1, t2);
		Assert.assertEquals(0., distance);
	}
	
	@Test
	public void TwoDifferentTreesWithFourLeaves() {
		String t1 = "((A,B),C,D);";
		String t2 = "((A,C),B,D);";

		double distance = metric.getDistance(t1, t2);
		Assert.assertEquals(2., distance);
	}
}
