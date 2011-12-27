package treecmp.r;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PatristicDistanceTests {

	private PatristicDistancesCorrelationRMetric metric;

	@Before
	public void setUp() {
		metric = new PatristicDistancesCorrelationRMetric();
	}
	
	@Test
	public void NonIsomorphicTreesWithoutBranchLengths() {
		String t1 = "((A, B), C, D);";
		String t2 = "((A, C), B, D);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(1.5, result);
	}
	
	@Test
	public void TwoIsomorphicTreesWithoutBranchLengths() {
		String t1 = "((A, B), C, D);";
		String t2 = "((C, D), A, B);";
		
		double result = metric.getDistance(t1, t2);
		Assert.assertEquals(0., result);
		
	}
}
