package treecmp.r;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WRFMatchingMetricTest {
	WRFSplitUMetric metric;
	
	@Before
	public void setUp() throws Exception {
		metric = new WRFSplitUMetric();
	}

	@Test
	public void TwoTreesOfFourLeaves() {
		String t1 = "((1,2):2,3,4);";
		String t2 = "((1,3):4,2,4);";
		
		double result = metric.getDistance(t1, t2);
		
		Assert.assertEquals(6., result);
	}
	
	@Test
	public void TwoTreesOfFourLeavesWithSameSplits() {
		String t1 = "((1,2):4,3,4);";
		String t2 = "((1,2):2,3,4);";
		
		double result = metric.getDistance(t1, t2);
		
		Assert.assertEquals(2., result);
	}
	
	@Test
	public void RandomTreeOfEightLeaves() {
		String t1 = "(weasel:19.25,(bear:10.3333,(dog:27.1667,((raccoon:24.4167,sea_lion:19.5833):-4.85,seal:16.85):4.45):-3.25):4.75,(monkey:100.917,cat:47.0833):20.75);";
		String t2 = "(weasel:19.25,(bear:10.3333,(dog:27.1667,((sea_lion:11.75,seal:12.25):9.25,raccoon:22.75):-0.25):-3.25):4.75,(monkey:100.917,cat:47.0833):20.75);";

		double result = metric.getDistance(t1, t2);
		
		Assert.assertEquals(16.2, result, 1e-6);
	}

	@Test
	public void IsomorphicTreesHaveDistance0() {
		String t1 = "((1,2):2,3,4);";
		String t2 = "((3,4):2,1,2);";
		
		double result = metric.getDistance(t1, t2);
		
		Assert.assertEquals(0., result);
	}
	
	@Test
	public void TreesOfSixLeavesWithoutCommonSplits() {
		String t1 = "((E,F):0.3,(A,B):0.1,(C,D):0.2);";
		String t2 = "((F,D):0.5,(B,E):0.6,(A,C):0.9);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(2.6, result, 1e-6);
	}
	
	@Test
	public void TreeWithExternalBranchLengths() {
		String t1 = "((E:1,F:2),A:3,B:4);";
		String t2 = "((F:2,E:1),A:5,B:1);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(5., result);
	}
	
	@Test
	public void TreeWithoutEdgeLengths() {
		String t1 = "((A,B),C,D);";
		String t2 = "((A,C),B,D);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(2., result);
	}
	
	@Test
	public void TreeWithNAs() {
		String t1 = "((E:NA,F:NA):NA,A:3,B:4);";
		String t2 = "((F:NA,E:NA):NA,A:5,B:1);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(5., result);
	}
	
	@Test
	public void TestHangUp1() {
		String t1 = "((1,((3,4):0.05,5):0.01):0.0127,6,2);";
		String t2 = "(1,(6,(3,(4,5):0.3):0.1):0.011,2);";
		double result = metric.getDistance(t1, t2);
		
		Assert.assertEquals(0.4637, result, 1e-6);
	}
}
