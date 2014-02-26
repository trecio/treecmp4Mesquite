package mesquite.treecmp.metrics.RMAST;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mesquite.treecmp.metrics.MetricTest;

public class RMASTTests extends MetricTest<RMAST> {
	@Test public void itShouldReturnZeroForEqualTrees() {
		final String t = "((A, B), C);";
		
		assertEquals(0., getMetricValue(t, t), 1e-6);
	}
	
	@Test public void itShouldReturnZeroForIsomorphicTrees() {
		final String t1 = "((A, B), (C, D));";
		final String t2 = "((C, D), (A, B));";
		
		assertEquals(0., getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturn1ForTreesForWhichOneLeafIsOutsideAgreementTree() {
		final String t1 = "((A,(B,E)),(C,D));";
		final String t2 = "(((A,B),(C,D)),E);";
		
		assertEquals(1., getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturn1ForMultifurcatingTreesWhichOneLeafIsOutsideAgreementTree() {
		final String t1 = "((A,B,C),D);";
		final String t2 = "(A,B,C,D);";
		
		assertEquals(1., getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnNMinus2ForOppositeCaterpillars() {
		final String t1 = "(((((((((A,B),C),D),E),F),G),H),I),J);";
		final String t2 = "(((((((((J,I),H),G),F),E),D),C),B),A);";
		
		assertEquals(8., getMetricValue(t1, t2), 1e-6);
	}

	@Override
	protected RMAST createMetric() {
		return new RMAST();
	}
}
