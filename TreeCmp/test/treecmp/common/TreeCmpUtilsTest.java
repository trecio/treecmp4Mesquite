/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.common;

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
import treecmp.common.TreeCmpUtils.RandomProcessType;

/**
 *
 * @author Damian
 */
public class TreeCmpUtilsTest {

    public TreeCmpUtilsTest() {
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
    public void makeBinaryTest() throws TreeParseException {
        String baseTree1 = "((1,2),(3,4));";
        Tree inTree1 = new ReadTree(InputSource.openString(baseTree1));
        Tree binTree1 = TreeCmpUtils.makeBinary(inTree1, true, RandomProcessType.YULE);
        System.out.println("Tree1: " + NodeUtilsExt.treeToSimpleString(binTree1, false));

        String baseTree2 = "(1,2,(3,4));";
        Tree inTree2 = new ReadTree(InputSource.openString(baseTree2));
        Tree binTree2 = TreeCmpUtils.makeBinary(inTree2, true, RandomProcessType.YULE);
        System.out.println("Tree2: " + NodeUtilsExt.treeToSimpleString(binTree2, false));

        String baseTree3 = "(1,2,3,4);";
        Tree inTree3 = new ReadTree(InputSource.openString(baseTree3));
        Tree binTree3 = TreeCmpUtils.makeBinary(inTree3, true, RandomProcessType.YULE);
        System.out.println("Tree3: " + NodeUtilsExt.treeToSimpleString(binTree3, false));

        String baseTree4 = "(1,2,3,4,5,6,7,8,9,10,11,11a,12,13,13b,14);";
        Tree inTree4 = new ReadTree(InputSource.openString(baseTree4));
        Tree binTree4 = TreeCmpUtils.makeBinary(inTree4, true, RandomProcessType.YULE);
        System.out.println("Tree4: " + NodeUtilsExt.treeToSimpleString(binTree4, false));

    }
}
