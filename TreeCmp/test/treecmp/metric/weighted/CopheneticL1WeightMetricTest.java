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

/**
 *
 * @author Damian
 */
public class CopheneticL1WeightMetricTest {
    
    public CopheneticL1WeightMetricTest() {
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
     * Test of isRooted method, of class CopheneticL1WeightMetric.
     */
    @Test
    public void testIsRooted() {
        System.out.println("isRooted");
        CopheneticL1WeightMetric instance = new CopheneticL1WeightMetric();
        boolean expResult = false;
        boolean result = instance.isRooted();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDistance method, of class CopheneticL1WeightMetric.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = null;
        Tree t2 = null;
        CopheneticL1WeightMetric instance = new CopheneticL1WeightMetric();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}