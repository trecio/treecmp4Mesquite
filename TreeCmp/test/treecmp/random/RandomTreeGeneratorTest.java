package treecmp.random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import treecmp.metric.topological.RFClusterMetric;
import treecmp.common.DifferentLeafSetUtils;
import pal.tree.ReadTree;
import pal.io.InputSource;
import pal.tree.TreeParseException;
import treecmp.common.NodeUtilsExt;
import pal.tree.Tree;
import pal.misc.SimpleIdGroup;
import pal.misc.IdGroup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pal.misc.Identifier;
import static org.junit.Assert.*;

/**
 *
 * @author Damian
 */
public class RandomTreeGeneratorTest {

    public RandomTreeGeneratorTest() {
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

    @Test
    public void generateYuleTreeTest() {

        int n = 80;

        RandomTreeGenerator randTreeGen1 = new RandomTreeGenerator(genIdGroup(n));

        Tree tree = randTreeGen1.generateYuleTree(true);
        String treeString = NodeUtilsExt.treeToSimpleString(tree, false) + ";";
        System.out.println(treeString);


    }

    @Test
    public void generateYuleTreeBasedOnGivenTest() throws TreeParseException {

        int n = 20;

        RandomTreeGenerator randTreeGen1 = new RandomTreeGenerator(genIdGroup(n));
        String baseTree = "((1,2),(3,4));";
        InputSource in1 = InputSource.openString(baseTree);
        Tree inTree = new ReadTree(in1);

        Tree tree = randTreeGen1.generateYuleTree(inTree, true);
        String treeString = NodeUtilsExt.treeToSimpleString(tree, false) + ";";
        System.out.println(treeString);

        Tree [] trees = DifferentLeafSetUtils.pruneTrees(inTree, tree);
        double d1 = RFClusterMetric.getRFClusterMetric(inTree, trees[0]);
        assertTrue(0 == d1);
        double d2 = RFClusterMetric.getRFClusterMetric(inTree, trees[1]);
        assertTrue(0 == d2);

    }

    private IdGroup genIdGroup(int n) {

        IdGroup idGroup = new SimpleIdGroup(n);
        for (int i = 1; i <= n; i++) {
            idGroup.setIdentifier(i - 1, new Identifier(String.valueOf(i)));
        }

        return idGroup;
    }
}
