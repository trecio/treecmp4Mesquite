package mesquite.treecmp.MAST;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mesquite.treecmp.MetricTest;

public class MASTTests extends MetricTest<MAST> {
	@Test public void itShouldReturnZeroForEqualTrees() {
		final String t = "((A, B), C, D);";
		
		assertEquals(0., getMetricValue(t, t), 1e-6);
	}
	
	@Test public void itShouldReturnZeroForIsomorphicTrees() {
		final String t1 = "((A, B), C, D);";
		final String t2 = "(C, D, (A, B));";
		
		assertEquals(0., getMetricValue(t1, t2), 1e-6);
	}

	@Override
	protected MAST createMetric() {
		return new MAST();
	}
}
