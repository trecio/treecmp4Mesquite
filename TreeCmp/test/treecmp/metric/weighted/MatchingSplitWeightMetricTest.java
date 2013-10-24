/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.Tree;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class MatchingSplitWeightMetricTest {
    
    public MatchingSplitWeightMetricTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDistance method, of class MatchingSplitWeightMetric.
     */
    @Test
    public void testGetDistance() {
        Tree t1 = TreeCreator.getWeightedT1();
        Tree t2 = TreeCreator.getWeightedT2();
        Tree t3 = TreeCreator.getWeightedT3();
        Tree t4 = TreeCreator.getWeightedT4();
        MatchingSplitWeightMetric instance = new MatchingSplitWeightMetric();
        double expResult = 11.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
     
        expResult = 17.0;
        result = instance.getDistance(t3, t4);        
        assertEquals(expResult, result, 0.0);
    }
}