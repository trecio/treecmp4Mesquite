package mesquite.treecmp.metrics.KTreeScoreDistance;

import org.junit.Test;

import mesquite.treecmp.metrics.MetricTest;
import static org.junit.Assert.assertEquals;

public class KTreeScoreDistanceTests extends MetricTest<KTreeScoreDistance> {
	@Test public void itShouldReturnZeroForIsomorphicTrees() {
		String t1 = "(A, B:2, (D:4, E:5):3);";
		String t2 = "(D:4, E:5, (A:1, B:2):3);";
		assertEquals(0, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnZeroForScalledIsomorphicTrees() {
		String t1 = "(A:2, B:4, (D:8, E:10):6);";
		String t2 = "(D:4, E:5, (A, B:2):3);";
		assertEquals(0, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldCalculateDistanceForTwoNonIsomorphicTrees() {
		String t1 = "(A, B, (D, E):2);";
		String t2 = "(A:1, D:1, (B:1, E:1):2);";
		assertEquals(Math.sqrt(6), getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldCalculateDistanceForTwoTreesWhichBranchLengthDiffer() {
		String t1 = "(A:1, B:1, (D:1, E:1):1);";
		String t2 = "(A, B:2, (D, E));";
		assertEquals(Math.sqrt(.5), getMetricValue(t1, t2), 1e-6);
	}

	@Test public void itShouldCalculateDistanceForTwoTreesWhichBranchLengthDiffer2() {
		String t2 = "(A:1, B:1, (D:1, E:1):1);";
		String t1 = "(A, B:2, (D, E));";
		assertEquals(2/Math.sqrt(5), getMetricValue(t1, t2), 1e-6);
	}

	@Override
	protected KTreeScoreDistance createMetric() {
		return new KTreeScoreDistance();
	}

}
