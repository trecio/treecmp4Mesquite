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
import treecmp.metric.topological.MatchingSplitMetricO3;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class FlowSplitMetricTest {
    
    public FlowSplitMetricTest() {
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
     * Test of getDistance method, of class FlowSplitMetric.
     */
    @Test
    public void testGetDistance() {
        Tree t1 = TreeCreator.getWeightedT1();
        Tree t2 = TreeCreator.getWeightedT2();
        Tree t3 = TreeCreator.getWeightedT3();
        Tree t4 = TreeCreator.getWeightedT4();
        FlowSplitMetric instance = new FlowSplitMetric();
        MatchingSplitMetricO3 ms = new MatchingSplitMetricO3();
        
        double expResult = 11.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.0);
     
        expResult = 11.0;
        result = instance.getDistance(t3, t4);        
        assertEquals(expResult, result, 0.0);
        
        Tree su1 = TreeCreator.getWeightedSimpleUnitT1();
        Tree su2 = TreeCreator.getWeightedSimpleUnitT2();
        assertEquals(ms.getDistance(su1, su2), instance.getDistance(su1, su2), 0.0);     
        
        Tree ut1 = TreeCreator.getWeightedUnitT1();
        Tree ut2 = TreeCreator.getWeightedUnitT2();
        Tree ut3 = TreeCreator.getWeightedUnitT3();
        
        assertEquals(ms.getDistance(ut1, ut2), instance.getDistance(ut1, ut2), 0.0);      
        assertEquals(ms.getDistance(ut1, ut3), instance.getDistance(ut1, ut3), 0.0);
        assertEquals(ms.getDistance(ut2, ut3), instance.getDistance(ut2, ut3), 0.0);
        
        Tree s10u1 = TreeCreator.getWeightedSimple10UnitT1();
        Tree s10u2 = TreeCreator.getWeightedSimple10UnitT2();
        assertEquals(ms.getDistance(s10u1, s10u2)*10, instance.getDistance(s10u1, s10u2),  0.0);
        
        Tree w10ut1 = TreeCreator.getWeighted10UnitT1();
        Tree w10ut2 = TreeCreator.getWeighted10UnitT2();
        Tree w10ut3 = TreeCreator.getWeighted10UnitT3();
        
        assertEquals(ms.getDistance(w10ut1, w10ut2)*10, instance.getDistance(w10ut1, w10ut2), 0.0);      
        assertEquals(ms.getDistance(w10ut1, w10ut3)*10, instance.getDistance(w10ut1, w10ut3), 0.0);
        assertEquals(ms.getDistance(w10ut2, w10ut3)*10, instance.getDistance(w10ut2, w10ut3), 0.0);
        
        //assertEquals(0, instance.getDistance(ut1, w10ut2), 0.0);      
        //assertEquals(0, instance.getDistance(ut1, w10ut3), 0.0);
        //assertEquals(0, instance.getDistance(ut2, w10ut3), 0.0);
    }
}