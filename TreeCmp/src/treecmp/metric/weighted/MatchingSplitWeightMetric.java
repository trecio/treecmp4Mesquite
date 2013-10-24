/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.LapSolver;
import treecmp.common.NodeUtilsExt;
import treecmp.common.SplitDist;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian Bogdanowicz
 */
public class MatchingSplitWeightMetric extends BaseMetric implements Metric {

    public static final int UNIT_MUL = 10000;

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


        int size = Math.max(t1Nodes.length, t2Nodes.length);

        int[][] assigncost = new int[size][size];
        int[] rowsol = new int[size];
        int[] colsol = new int[size];
        int[] u = new int[size];
        int[] v = new int[size];

        int hab = 0;
        int haO = 0;
        int hbO = 0;
        int s1 = 0, s2 = 0, s3 = 0;
        int fa = 0, gb = 0;
        //calculate costs
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {

                if (i < t1Nodes.length && j < t2Nodes.length) {
                    hab = SplitDist.getDist1Int(t1Splits[i], t2Splits[j]);
                    haO = SplitDist.getMinSize(t1Splits[i]);
                    hbO = SplitDist.getMinSize(t2Splits[j]);
                    node1 = t1Nodes[i];
                    node2 = t2Nodes[j];
                    fa = (int) (node1.getBranchLength() * UNIT_MUL);
                    gb = (int) (node2.getBranchLength() * UNIT_MUL);

                    s1 = Math.min(fa, gb) * hab;
                    s2 = inc(fa, gb) * haO;
                    s3 = inc(gb, fa) * hbO;

                    assigncost[i][j] = s1 + s2 + s3;

                } else if (i < t1Nodes.length && j >= t2Nodes.length) {
                    node1 = t1Nodes[i];
                    fa = (int) (node1.getBranchLength() * UNIT_MUL);
                    haO = SplitDist.getMinSize(t1Splits[i]);

                    s1 = fa * haO;
                    assigncost[i][j] = s1;

                } else if (i >= t1Nodes.length && j < t2Nodes.length) {
                    node2 = t2Nodes[j];
                    gb = (int) (node2.getBranchLength() * UNIT_MUL);
                    hbO = SplitDist.getMinSize(t2Splits[j]);

                    s1 = gb * hbO;
                    assigncost[i][j] = s1;
                } else {
                    assigncost[i][j] = 0;
                }
            }
        }
        int metric = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
        double dist = (double) metric / (double) UNIT_MUL;
        return dist;
    }

    public int inc(int x, int y) {
        return Math.max(0, x - y);
    }
}
