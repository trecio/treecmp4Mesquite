/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.topological;

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
public class NodalL2MetricTest {
    
    public NodalL2MetricTest() {
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
     * Test of getDistance method, of class NodalL2Metric.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = null;
        Tree t2 = null;
        NodalL2Metric instance = new NodalL2Metric();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDistanceOld method, of class NodalL2Metric.
     */
    @Test
    public void testGetDistanceOld() {
        System.out.println("getDistanceOld");
        Tree t1 = null;
        Tree t2 = null;
        NodalL2Metric instance = new NodalL2Metric();
        double expResult = 0.0;
        double result = instance.getDistanceOld(t1, t2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}