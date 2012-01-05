package mesquite.treecomp.metrics.MatchingPairsMetric;

import junit.framework.Assert;
import mesquite.treecomp.metrics.MetricTestHelper;

import org.junit.Before;
import org.junit.Test;

public class MatchingPairsMetricTests {
	private MetricTestHelper metric;

	@Before
	public void setUp() {
		metric = new MetricTestHelper(new MatchingPairsMetric());
	}

	@Test
	public void AscaridoidDataSanityCheck() {
		String t1 = "((((Crud1:0.090001,(Tleo:0.1,(Cosb:0.092661,(((((((Ceud:0.160302,(Pdep:0.097051,Pang:0.1):0.215996):0.078215,(Alum:0.092407,Asuu:0.1):0.046955):0.186713,Hadu1:0.09289):0.011065,((Racu:0.125492,Cosc1:0.1):0.042691,(Tvit:0.17382,(Atyp:0.096208,((Abre:0.1,Apeg1:0.1):0.025663,(Tcfc:0.1,Pcra:0.092603):0.09382):0.015363):0.299439):0.088403):0.088507):0.053089,(Azip:0.1,((Asim:0.071022,Pdec1:0.1):0.148985,Hbid:0.117258):0.045068):0.054911):0.206318,Tcan:0.1):0.042707,Tcat1:0.065108):0.081351):0.064481):0.10569):0.023522,Orob:0.162861):0.08485,Crad:0.1):0.255176,((Pens:0.1,Pstr:0.1):0.123851,Aphy1:0.124981):0.086935);";
		String t2 = "((((Tleo:0.1,(Cosb:0.092661,(((((((((Ceud:0.160302,(Pdep:0.097051,Pang:0.1):0.138357):0.080836,(Alum:0.092407,Asuu:0.1):0.046955):0.156389,(((Asim:0.071022,Pdec1:0.156214):0.215231,Azip:0.1):0.03235,Hbid:0.173009):0.046619):0.052832,Hadu1:0.09289):0.011065,(((Tvit:0.093672,(Atyp:0.061137,(Abre:0.1,Apeg1:0.066219):0.023977):0.19142):0.016928,Tcfc:0.047101):0.123367,Pcra:0.139602):0.088403):0.042691,Cosc1:0.1):0.078989,Racu:0.125492):0.176749,Tcan:0.14376):0.023277,Tcat1:0.065108):0.081351):0.060842):0.15134,Crad:0.089628):0.029555,Crud1:0.047365):0.062476,((Pens:0.103037,Pstr:0.030057):0.144005,(Orob:0.244656,Aphy1:0.389677):0.092491):0.012876);";
		
		Assert.assertEquals(178., metric.getDistance(t1, t2));
	}
}
