/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.topological;

import pal.tree.Tree;
import pal.tree.ReadTree;
import pal.io.InputSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pal.tree.TreeParseException;
import treecmp.random.RandomTreeGenerator;
import treecmp.test.util.TestUtils;

/**
 *
 * @author Damian
 */
public class MatchingPairMetricTest {

    private MatchingPairMetric mpMetric = null;
    public MatchingPairMetricTest() {
        this.mpMetric = new MatchingPairMetric();

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void getDistanceTest1() throws TreeParseException{
        String treeString1 = "((1,2),(3,4));";
        InputSource in = InputSource.openString(treeString1);
        Tree t1 = new ReadTree(in);

        String treeString2 = "((1,2),(3,4));";
        in = InputSource.openString(treeString2);
        Tree t2 = new ReadTree(in);

        double dist = mpMetric.getDistance(t1, t2);
        assertEquals(0, dist, 0);
    }
    @Test
    public void getDistanceTest2() throws TreeParseException{
        String treeString1 = "((1,3),(4,2));";
        InputSource in = InputSource.openString(treeString1);
        Tree t1 = new ReadTree(in);

        String treeString2 = "((1,2),(3,4));";
        in = InputSource.openString(treeString2);
        Tree t2 = new ReadTree(in);

        double mpDist = mpMetric.getDistance(t1, t2);
    }

     @Test
    public void getDistanceTest3() throws TreeParseException{
        String treeString1 = "(1,(3,(4,2)));";
        InputSource in = InputSource.openString(treeString1);
        Tree t1 = new ReadTree(in);

        String treeString2 = "((1,2),(3,4));";
        in = InputSource.openString(treeString2);
        Tree t2 = new ReadTree(in);

        double mpDist = mpMetric.getDistance(t1, t2);
    }
     @Test
     public void getDistanceTest4Rand() throws TreeParseException{
        runGetDistanceTestRand(10);
     }

     @Test
     public void getDistanceTestNonBin1() throws TreeParseException{
        String treeString1 = "(1,2,3,4);";
        InputSource in = InputSource.openString(treeString1);
        Tree t1 = new ReadTree(in);

        String treeString2 = "((1,2),(3,4));";
        in = InputSource.openString(treeString2);
        Tree t2 = new ReadTree(in);

        double mpDist = mpMetric.getDistance(t1, t2);
        System.out.println("Dist: "+mpDist);
        assertEquals(2, mpDist, 0);
        assertEquals(mpMetric.getDistance(t1, t2), mpMetric.getDistance(t2, t1), 0);

     }

    @Test
    public void getDistanceTest5Rand() throws TreeParseException {
        for (int i = 5; i < 100; i++) {
            System.out.println("Leaf num = " + i);
            runGetDistanceTestRand(i);
        }
    }

    private void runGetDistanceTestRand(int n) throws TreeParseException {
        RandomTreeGenerator randTreeGen1 = new RandomTreeGenerator(TestUtils.genIdGroup(n));
        String baseTree = "((1,2),(3,4));";
        InputSource in1 = InputSource.openString(baseTree);
        Tree inTree = new ReadTree(in1);

        Tree tree1 = randTreeGen1.generateYuleTree(inTree, true);
        Tree tree2 = randTreeGen1.generateYuleTree(inTree, true);
        double mpDist = mpMetric.getDistance(tree1, tree2);
        System.out.println("MP= " + mpDist);
    }

    @Test
    public void coutChildrenPairsTest(){

    }
}