/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.topological;

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
    treecmp.metric.topological.NodalL2MetricTest.class,
    treecmp.metric.topological.MatchingPairMetricTest.class,
    treecmp.metric.topological.MastMetricTest.class,
    treecmp.metric.topological.GeneralizedRFClusterMetricTest.class,
    treecmp.metric.topological.NodalL2SplittedMetricTest.class,
    treecmp.metric.topological.CopheneticL1MetricTest.class,
    treecmp.metric.topological.CopheneticL2MetricTest.class,
    treecmp.metric.topological.UmastMetricTest.class
})
public class TopologicalTestSuite {

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