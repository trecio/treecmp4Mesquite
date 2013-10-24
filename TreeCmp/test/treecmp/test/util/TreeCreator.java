/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.test.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pal.io.InputSource;
import pal.tree.ReadTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;

/**
 *
 * @author Damian
 */
public class TreeCreator {

    private final static Logger LOG = Logger.getLogger(TreeCreator.class.getName());

    public static Tree getTreeFromString(String treeStr) {
        Tree tree = null;
        InputSource is = InputSource.openString(treeStr);
        try {
            tree = new ReadTree(is);
            is.close();
        } catch (TreeParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return tree;
    }

    /* The four trees for "On Matching Distances for Rooted and Weighted Phylogenetic Trees"
     * unpublished work by Damian Bogdanowicz and Krzysztof Giaro
     */
    public static Tree getWeightedT1() {
        String treeStr = "((a:1,b:1,c:1,d:1):10,e:1,(f:1,g:1,h:1,i:1,j:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedT2() {
        String treeStr = "((a:1,b:1,c:1,d:1,e:1):10,f:1,(g:1,h:1,i:1,j:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedT3() {
        String treeStr = "((a:1,b:1,c:1,d:1):10,e:1,f:1,g:1,h:1,i:1,j:1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedT4() {
        String treeStr = "((a:1,b:1,c:1,d:1,e:1):9,f:1,(g:1,h:1,i:1,j:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedSimpleUnitT1() {
        String treeStr = "((a:1,b:1):1,(c:1,d:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedSimpleUnitT2() {
        String treeStr = "(((a:1,b:1):1,c:1):1,d:1);";
        return getTreeFromString(treeStr);
    }

     public static Tree getWeightedSimple10UnitT1() {
        String treeStr = "((a:10,b:10):10,(c:10,d:10):10);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedSimple10UnitT2() {
        String treeStr = "(((a:10,b:10):10,c:10):10,d:10);";
        return getTreeFromString(treeStr);
    }
    
    public static Tree getWeightedUnitT1() {
        String treeStr = "((a:1,b:1,c:1,d:1):1,e:1,(f:1,g:1,h:1,i:1,j:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedUnitT2() {
        String treeStr = "((a:1,b:1,c:1,d:1,e:1):1,f:1,(g:1,h:1,i:1,j:1):1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedUnitT3() {
        String treeStr = "((a:1,b:1,c:1,d:1):1,e:1,f:1,g:1,h:1,i:1,j:1);";
        return getTreeFromString(treeStr);
    }

      public static Tree getWeighted10UnitT1() {
        String treeStr = "((a:10,b:10,c:10,d:10):10,e:10,(f:10,g:10,h:10,i:10,j:10):10);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeighted10UnitT2() {
        String treeStr = "((a:10,b:10,c:10,d:10,e:10):10,f:10,(g:10,h:10,i:10,j:10):10);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeighted10UnitT3() {
        String treeStr = "((a:10,b:10,c:10,d:10):10,e:10,f:10,g:10,h:10,i:10,j:10);";
        return getTreeFromString(treeStr);
    }
    
    /* The two trees given as an example with gtp application: 
     * http://www.unc.edu/depts/stat-or/miscellaneous/provan/treespace/
     * They should be at the distance of 2.844225
     */
    public static Tree getWeightedGtpT1() {
        String treeStr = "(((((4:1,5:1):0.88,(3a:1,3b:1):1):0.47,2:1):0.73,1:1):0.83,6:1);";
        return getTreeFromString(treeStr);
    }

    public static Tree getWeightedGtpT2() {
        String treeStr = "(((((3a:0.2,3b:1):0.5,4:1):0.15,2:1):0.87,(5:1,6:1):0.42):0.7,1:1);";
        return getTreeFromString(treeStr);
    }
}
