package mesquite.treecmp.metrics.UMAST;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mesquite.treecmp.metrics.MetricTest;

public class UMASTTests extends MetricTest<UMAST> {
	@Test public void itShouldReturn0ForEqualTrees() {
		final String t = "((A,B),C,D);";
		
		assertEquals(0, getMetricValue(t, t), 1e-6);
	}
	
	@Test public void itShouldReturn0ForIsomorphicTrees() {
		final String t1 = "((A,B),C,D);";
		final String t2 = "(A,B,(C,D));";
		
		assertEquals(0, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturn1ForTreesWithOneLeafOutsideOfAgreementSubTree() {
		final String t1 = "(A,B,((C,D),E));";
		final String t2 = "(A,(B,E),(C,D));";
		
		assertEquals(1, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnNMinus3ForBinaryTreeAndAStar() {
		final String t1 = "(((AAA,AAB),(ABA,ABB)),((BAA,BAB),(BBA,BBB)),((CAA,CAB),(CBA,CBB)));";
		final String t2 = "(AAA,AAB,ABA,ABB,BAA,BAB,BBA,BBB,CAA,CAB,CBA,CBB);";
		
		assertEquals(9, getMetricValue(t1, t2), 1e-6);
	}

	@Override
	protected UMAST createMetric() {
		return new UMAST();
	}

}