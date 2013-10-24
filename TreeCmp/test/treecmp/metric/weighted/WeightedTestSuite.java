/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Damian
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    treecmp.metric.weighted.GeoUnrootedMetricTest.class,
    treecmp.metric.weighted.MatchingSplitWeightMetricTest.class, 
    treecmp.metric.weighted.FlowClusterMetricTest.class,
    treecmp.metric.weighted.GeoRootedMetricTest.class,
    treecmp.metric.weighted.FlowSplitMetricTest.class,
    treecmp.metric.weighted.RFWeightMetricTest.class,
    treecmp.metric.weighted.GeoMetricWrapperTest.class,
    treecmp.metric.weighted.NodalL1EdgeWeightMetricTest.class,
    treecmp.metric.weighted.NodalL2EdgeWeightMetricTest.class,
    treecmp.metric.weighted.MatchingClusterWeightMetricTest.class,
    treecmp.metric.weighted.CopheneticL1WeightMetricTest.class, 
    treecmp.metric.weighted.CopheneticL2WeightMetricTest.class
})
public class WeightedTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}