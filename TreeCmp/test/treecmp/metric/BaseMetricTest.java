/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.Tree;
import treecmp.statdata.IMetircDistrbHolder;
import treecmp.test.util.TreeCreator;

/**
 *
 * @author Damian
 */
public class BaseMetricTest {
    
    public BaseMetricTest() {
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
     * Test of getDistAfterPrunningIfNeeded method, of class BaseMetric.
     */
    @Test
    public void testGetDistAfterPrunningIfNeeded() throws Exception {
        System.out.println("getDistAfterPrunningIfNeeded");
        Tree t1 = TreeCreator.getTreeFromString("(a,b,c,d,e);");
        Tree t2 = TreeCreator.getTreeFromString("((c,(d,e,f),g),h);");
        BaseMetric instance = new BaseMetricImpl();

        DistInfo result = instance.getDistAfterPrunningIfNeeded(t1, t2);
        assertEquals(0.0, result.getDist(), 0);
        assertEquals(t1.getExternalNodeCount(), result.getT1LeafNum());
        assertEquals(t2.getExternalNodeCount(), result.getT2LeafNum());
        assertEquals(3, result.getCommonLeafNum());
    }

    public class BaseMetricImpl extends BaseMetric {

        public boolean isRooted() {
            return false;
        }

        public double getDistance(Tree t1, Tree t2) {
            return 0.0;
        }
    }
}