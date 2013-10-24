/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.misc.IdGroup;
import pal.tree.*;
import treecmp.common.NodeUtilsExt;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class RFWeightMetric extends BaseMetric implements Metric {

    @Override
    public boolean isRooted() {
        return false;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        int t1_ExtNodes = t1.getExternalNodeCount();
        int t2_ExtNodes = t2.getExternalNodeCount();
        int t1_IntNodes = t1.getInternalNodeCount();
        int t2_IntNodes = t2.getInternalNodeCount();
        int t1_TotNum = t1_ExtNodes + t1_IntNodes;
        int t2_TotNum = t2_ExtNodes + t2_IntNodes;
        Node[] t1Nodes = new Node[t1_TotNum - 1];
        Node[] t2Nodes = new Node[t2_TotNum - 1];
        IdGroup idGroup = TreeUtils.getLeafIdGroup(t1);

        int i = 0, j = 0;

        for (i = 0; i < t1_ExtNodes; i++) {
            t1Nodes[i] = t1.getExternalNode(i);
        }

        j = t1_ExtNodes;
        for (i = 0; i < t1_IntNodes; i++) {
            Node tmp = t1.getInternalNode(i);
            if (!tmp.isRoot()) {
                t1Nodes[j] = tmp;
                j++;
            }
        }

        for (i = 0; i < t2_ExtNodes; i++) {
            t2Nodes[i] = t2.getExternalNode(i);
        }

        j = t2_ExtNodes;
        for (i = 0; i < t2_IntNodes; i++) {
            Node tmp = t2.getInternalNode(i);
            if (!tmp.isRoot()) {
                t2Nodes[j] = tmp;
                j++;
            }
        }

        boolean[][] t1Splits = new boolean[t1_TotNum - 1][t1_ExtNodes];
        boolean[][] t2Splits = new boolean[t2_TotNum - 1][t2_ExtNodes];

        Node node, node1, node2;

        for (i = 0; i < t1Nodes.length; i++) {
            node = t1Nodes[i];
            if (node.isLeaf()) {
                NodeUtilsExt.getSplitExternal(idGroup, node, t1Splits[i]);
            } else {
                SplitUtils.getSplit(idGroup, node, t1Splits[i]);
            }
        }

        for (i = 0; i < t2Nodes.length; i++) {
            node = t2Nodes[i];
            if (node.isLeaf()) {
                NodeUtilsExt.getSplitExternal(idGroup, node, t2Splits[i]);
            } else {
                SplitUtils.getSplit(idGroup, node, t2Splits[i]);
            }
        }

        boolean findSame = false;
        Node nodeSame = null;
        double cost = 0.0;
        double tot1 = 0.0;
        double tot2 = 0.0;
        double e1 = 0.0, e2 = 0.0;

        for (i = 0; i < t1Nodes.length; i++) {
            node1 = t1Nodes[i];
            findSame = false;
            nodeSame = null;
            for (j = 0; j < t2Nodes.length; j++) {
                node2 = t2Nodes[j];
                if (SplitUtils.isSame(t1Splits[i], t2Splits[j])) {
                    nodeSame = node2;
                    findSame = true;
                    break;
                }
            }
            e1 = node1.getBranchLength();
            if (findSame) {
                e2 = nodeSame.getBranchLength();
                cost = Math.abs(e1 - e2);
            } else {
                cost = e1;
            }
            tot1 = tot1 + cost;
        }

        for (i = 0; i < t2Nodes.length; i++) {
            node2 = t2Nodes[i];
            findSame = false;
            for (j = 0; j < t1Nodes.length; j++) {
                if (SplitUtils.isSame(t2Splits[i], t1Splits[j])) {
                    findSame = true;
                    break;
                }
            }

            if (!findSame) {
                e2 = node2.getBranchLength();
                tot2 = tot2 + e2;
            }
        }

        double dist = 0.5 * (tot1 + tot2);
        return dist;
    }
}
