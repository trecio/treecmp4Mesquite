package mesquite.treecmp.metrics.MatchingSplitMetric;

import static org.junit.Assert.assertEquals;
import mesquite.treecmp.MetricTest;
import mesquite.treecmp.metrics.MatchingSplitMetric.MatchingSplitMetric;

import org.junit.Test;

public class MatchingSplitMetricTests extends MetricTest<MatchingSplitMetric> {
	@Override
	protected MatchingSplitMetric createMetric() {
		// TODO Auto-generated method stub
		return new MatchingSplitMetric();
	}
	
	@Test
	public void SameTreesShouldHaveDistance0() {
		String t = "((A:1,B:2):3,C:4,D:5);";

		assertEquals(0., getMetricValue(t, t), 1e-6);
	}
	
	@Test
	public void IsomorphicTreesShouldHaveDistance0() {
		String t1 = "((A:1,B:2):3,C:4,D:5);";
		String t2 = "((C:4,D:5):3,A:1,B:2);";
	
		assertEquals(0., getMetricValue(t1, t2), 1e-6);
	}
	
	@Test
	public void TwoTreesWithFourLeafsWithBranchLengthsAndSameSplits() {
		String t1 = "((A:1,B:2):3,C:4,D:5);";
		String t2 = "((A:1,B:2):6,C:4,D:5);";
		
		assertEquals(0., getMetricValue(t1, t2), 1e-6);
	}
	
	@Test
	public void TwoDifferentTreesWithFourLeaves() {
		String t1 = "((A,B),C,D);";
		String t2 = "((A,C),B,D);";

		assertEquals(2., getMetricValue(t1, t2), 1e-6);
	}

}
