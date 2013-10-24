/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class TreeUtilsExt extends TreeUtils {

    /**
     * get list of the identifiers of the external nodes without rebuilding node list
     *
     * @return leaf identifier group
     */
    public static final IdGroup getLeafIdGroupExt(Tree tree) {

        IdGroup labelList =
                new SimpleIdGroupExt(tree.getExternalNodeCount());

        for (int i = 0; i < tree.getExternalNodeCount(); i++) {
            labelList.setIdentifier(i, tree.getExternalNode(i).getIdentifier());
        }

        return labelList;
    }
    /**
     * Creates a map of leaves:
     * for leaf i in IdGroup call map[i] to obtain its ID in the tree
     *
     * value -1 means that there is no leaf of given ID in given tree
     *
     * @param idGroup
     * @param tree
     * @return
     */
    public static final int[] mapExternalIdsFromGroupToTree(IdGroup idGroup, Tree tree) {

        int[] alias = new int[idGroup.getIdCount()];

        for (int i = 0; i < idGroup.getIdCount(); i++) {
            Identifier groupId = idGroup.getIdentifier(i);
            Node node = TreeUtils.getNodeByName(tree, groupId.getName());
            if (node != null) {
                alias[i] = node.getNumber();
            } else {
                alias[i] = -1;
            }
        }

        return alias;
    }
    /**
     *  TODO: rethink and complete the implementation, now always 0 is returned!
     * @param t1
     * @param t2
     * @return 0 (!!!)
     */
    public int getMulValue(Tree t1, Tree t2) {
        int t1_ExtNodes = t1.getExternalNodeCount();
        int t2_ExtNodes = t2.getExternalNodeCount();
        int t1_IntNodes = t1.getInternalNodeCount();
        int t2_IntNodes = t2.getInternalNodeCount();

        double min = Double.MAX_VALUE;
        double max = -1;
        double val;
        Node node;

        for (int i = 0; i < t1_ExtNodes; i++) {
            val = t1.getExternalNode(i).getBranchLength();
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
        }

        for (int i = 0; i < t2_ExtNodes; i++) {
            val = t2.getExternalNode(i).getBranchLength();
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
        }

        for (int i = 0; i < t1_IntNodes; i++) {
            node = t1.getExternalNode(i);
            if (!node.isRoot()) {
                val = node.getBranchLength();
                if (val > max) {
                    max = val;
                }
                if (val < min) {
                    min = val;
                }
            }
        }

        for (int i = 0; i < t2_IntNodes; i++) {
            node = t2.getExternalNode(i);
            if (!node.isRoot()) {
                val = node.getBranchLength();
                if (val > max) {
                    max = val;
                }
                if (val < min) {
                    min = val;
                }
            }
        }
        return 0;
    }
}
