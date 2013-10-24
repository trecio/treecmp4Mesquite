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
public class GeoMetricWrapperTest {
    
    public GeoMetricWrapperTest() {
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
     * Test of getDistance method, of class GeoMetricWrapper.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");
        Tree t1 = null;
        Tree t2 = null;
        boolean rooted = false;
        String logFileName = "";
        GeoMetricWrapper instance = new GeoMetricWrapper();
        double expResult = 0.0;
        double result = instance.getDistance(t1, t2, rooted, logFileName);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}