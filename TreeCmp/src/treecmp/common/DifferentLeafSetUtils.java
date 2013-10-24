/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pal.misc.IdGroup;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeRestricter;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class DifferentLeafSetUtils {

    public static List<String> getCommonLeaves(Tree t1, Tree t2) {
        //IdGroup idGroup1 = TreeUtils.getLeafIdGroup(t1);
        //IdGroup idGroup2 = TreeUtils.getLeafIdGroup(t2);
        //IdGroup idGroup1 = t1;
        //IdGroup idGroup2 = t2;
        IdGroup idGroup1 = TreeUtilsExt.getLeafIdGroupExt(t1);
        IdGroup idGroup2 = TreeUtilsExt.getLeafIdGroupExt(t2);
        return getCommonLeaves(idGroup1, idGroup2);
    }

    public static List<String> getCommonLeaves(IdGroup idGroup1, IdGroup idGroup2) {

        Set<String> id1Set = new HashSet<String>((idGroup1.getIdCount() * 4) / 3);
        for (int i = 0; i < idGroup1.getIdCount(); i++) {
            id1Set.add(idGroup1.getIdentifier(i).getName());
        }

        List<String> commonIds = new ArrayList(idGroup1.getIdCount());

        for (int i = 0; i < idGroup2.getIdCount(); i++) {
            String name = idGroup2.getIdentifier(i).getName();
            if (id1Set.contains(name)) {
                commonIds.add(name);
            }
        }
        return commonIds;
    }

    public static boolean isLeafSetsEqual(Tree t1, Tree t2) {
        IdGroup idGroup1 = TreeUtils.getLeafIdGroup(t1);
        IdGroup idGroup2 = TreeUtils.getLeafIdGroup(t2);
        return isLeafSetsEqual(idGroup1, idGroup2);
    }

    public static boolean isLeafSetsEqual(IdGroup idGroup1, IdGroup idGroup2) {

        List<String> commonIdsList = getCommonLeaves(idGroup1, idGroup2);
        int commonTaxaNum = commonIdsList.size();
        int t1TaxaNum = idGroup1.getIdCount();
        int t2TaxaNum = idGroup2.getIdCount();

        if (t1TaxaNum == commonTaxaNum && t2TaxaNum == commonTaxaNum) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * If trees have the same leaf set then the original trees (i.e. those that
     * have been passed as arguments) are returned; otherwise new objects are
     * created and return.
     *
     * @param t1
     * @param t2
     * @return
     */
    public static Tree[] pruneTrees(Tree t1, Tree t2) {

        List<String> commonIdsList = getCommonLeaves(t1, t2);
        int commonTaxaNum = commonIdsList.size();
        int t1TaxaNum = t1.getExternalNodeCount();
        int t2TaxaNum = t2.getExternalNodeCount();
        String[] commonIdsArray = commonIdsList.toArray(new String[commonTaxaNum]);

        Tree[] trees = new Tree[2];

        if (commonTaxaNum > 0) {
            if (t1TaxaNum == commonTaxaNum && t2TaxaNum == commonTaxaNum) {
                trees[0] = t1;
                trees[1] = t2;
            } else {
                TreeRestricter tr1 = new TreeRestricter(t1, commonIdsArray, true);
                TreeRestricter tr2 = new TreeRestricter(t2, commonIdsArray, true);
                trees[0] = tr1.generateTree();
                trees[1] = tr2.generateTree();
            }
        } else {
            //empty trees
            trees[0] = new SimpleTree();
            trees[1] = new SimpleTree();
        }
        return trees;
    }
}
