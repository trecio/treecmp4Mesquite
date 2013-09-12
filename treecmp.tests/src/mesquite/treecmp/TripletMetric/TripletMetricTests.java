package mesquite.treecmp.TripletMetric;

import junit.framework.Assert;
import mesquite.treecmp.MetricTest;

import org.junit.Test;

public class TripletMetricTests extends MetricTest<TripletMetric> {
	@Override
	protected TripletMetric createMetric() {
		return new TripletMetric();
	}
	
	@Test
	public void SameTreesHaveDistance0() {
		String t1 = "((A,B),C);";
		
		Assert.assertEquals(0., getMetricValue(t1, t1));
	}
	
	@Test
	public void TwoTreesWithThreeLeafs() {
		String t1 = "((A,B),C);";
		String t2 = "(A,(B,C));";

		Assert.assertEquals(1., getMetricValue(t1, t2));
	}
	
	@Test
	public void IsoMorphicTrees() {
		String t1 = "((A,B),C);";
		String t2 = "(C,(A,B));";

		Assert.assertEquals(0., getMetricValue(t1, t2));
	}
	
	@Test
	public void TwoTreesWithFiveLeafs() {
		String t1 = "((A,B),(C,(D,E)));";
		String t2 = "((A,E),(C,(B,D)));";

		Assert.assertEquals(9., getMetricValue(t1, t2));		
	}
	
	@Test
	public void TwoTreesWithFiveLeafs2() {
		String t1 = "(((1,(2,3)),4),5);";
		String t2 = "(((1,2),3),(4,5));";
		
		Assert.assertEquals(4., getMetricValue(t1, t2));
	}

	@Test
	public void TwoTreesWithFiveLeafs3() {
		String t1 = "((1,(2,3)),(4,5));";
		String t2 = "(5,(4,(3,(2,1))));";
		
		Assert.assertEquals(4., getMetricValue(t1, t2));
	}

	@Test
	public void BranchLengthsAreIgnored() {
		String blt1 = "(((1:1,(2:2,3:3)),4:4),5:5);";
		String blt2 = "(((1:6,2:7),3:8),(4:9,5:10));";
		double blResult = getMetricValue(blt1, blt2);
		
		String t1 = "(((1,(2,3)),4),5);";
		String t2 = "(((1,2),3),(4,5));";
		double result = getMetricValue(t1, t2);
		
		Assert.assertEquals(result, blResult);
	}

	@Test
	public void NonBinaryTree() {
		String t1 = "((A,B),(C,D));";
		String t2 = "((A,B,C),D);";

		Assert.assertEquals(3., getMetricValue(t1, t2));
	}

}
