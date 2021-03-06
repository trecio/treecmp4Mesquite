package mesquite.treecmp.metrics.UMAST;

import static org.junit.Assert.assertEquals;
import mesquite.treecmp.metrics.MetricTest;

import org.junit.Test;

public class UMASTTests extends MetricTest<UMAST> {
	@Test public void itShouldReturn0ForTwoTreesWithThreeLeafs() {
		final String t1 = "(A,B,C);";
		final String t2 = "(B,A,C);";
		
		assertEquals(0., getMetricValue(t1, t2), 1e-6);
	}
	
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
	
	@Test public void itShouldReturn0ForTwoIsomorphicRootedTrees() {
		final String t1 = "((A,B),(C,D));";
		final String t2 = "((D,C),(B,A));";
		
		assertEquals(0, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnCorrectValueForSomeTwoTrees() {
		final String t1 = "(Crad,Crud1,((Tcat1,(Tvit,(Tcan,Tcfc))),(Asuu,(Alum,(Pcra,(Azip,(Aphy1,Apeg1)))))));";
		final String t2 = "(Crad,(Crud1,(Asuu,(Tcat1,(Tvit,(Tcan,Tcfc))))),(Aphy1,(Apeg1,(Azip,(Pcra,Alum)))));";
		
		assertEquals(5, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnCorrectValueForSomeTwoTrees2() {
		final String t1 = "((((((Tra,(Wah,Mer)),Leg),(Sym,(Cam,Ade))),(Tri,Asy)),(Cod,Cya)),Pla,Tob);";
		final String t2 = "((Tra,((Wah,Mer),(Leg,((Sym,(Cam,Ade)),(Tri,Asy))))),((Cod,Cya),Pla),Tob)";
		
		assertEquals(4, getMetricValue(t1, t2), 1e-6);
	}
	
	@Test public void itShouldReturnCorrectValueForSomeTwoBiggerTrees() {
		final String t1 = "(1,((((2,3),(4,5)),6),(((((7,8),(9,(10,11))),(12,13)),((14,(15,((16,17),18))),19)),20)));";
		final String t2 = "((((8,(16,18)),17),(12,20)),(((1,13),((4,7),((((((2,6),19),(3,15)),14),10),5))),(11,9)));";
		
		assertEquals(12, getMetricValue(t1,  t2), 1e-6);
	}
	
	@Test public void itShouldReturnNumberOfLeafsForTwoTreesWithNoCommonLeafs() {
		final String t1 = "(A,B,(C,D));";
		final String t2 = "(W,X,(Y,Z));";
		
		assertEquals(4., getMetricValue(t1, t2), 1e-6);
	}

	@Override
	protected UMAST createMetric() {
		return new UMAST();
	}

}