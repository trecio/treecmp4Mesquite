package mesquite.treecomp.metrics;

import junit.framework.Assert;

import mesquite.treefarm.Correlation2Trees.Correlation2Trees;

import org.junit.Before;
import org.junit.Test;

public class PatristicDistanceTests {

	private MetricTestHelper metric;

	@Before
	public void setUp() {
		metric = new MetricTestHelper(new Correlation2Trees());
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
