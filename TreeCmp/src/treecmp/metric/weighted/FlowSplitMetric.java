/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import algs.model.network.*;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.NodeUtilsExt;
import treecmp.common.SplitDist;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class FlowSplitMetric extends BaseMetric implements Metric {

    private static final int UNIT_MUL = 10000;

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

        int i, j, diff;

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

        int cost;
        int total_dem = 0;
        int total_sup = 0;

        Node node;
        //dummy demmander and supplier are included
        int N_sup = t1_TotNum;
        int N_dem = t2_TotNum;

        int costs[][] = new int[N_sup][N_dem];
        int[] sup = new int[N_sup];
        int[] dem = new int[N_dem];

        for (i = 0; i < t1Nodes.length; i++) {
            node = t1Nodes[i];
            sup[i] = (int) (node.getBranchLength() * UNIT_MUL);
            if (node.isLeaf()) {
                NodeUtilsExt.getSplitExternal(idGroup, node, t1Splits[i]);
            } else {
                SplitUtils.getSplit(idGroup, node, t1Splits[i]);
            }
            total_sup += sup[i];
        }

        for (i = 0; i < t2Nodes.length; i++) {
            node = t2Nodes[i];
            dem[i] = (int) (node.getBranchLength() * UNIT_MUL);
            if (node.isLeaf()) {
                NodeUtilsExt.getSplitExternal(idGroup, node, t2Splits[i]);
            } else {
                SplitUtils.getSplit(idGroup, node, t2Splits[i]);
            }
            total_dem += dem[i];
        }


        //calculate costs
        for (i = 0; i < t1Nodes.length; i++) {
            for (j = 0; j < t2Nodes.length; j++) {
                cost = SplitDist.getDist1Int(t1Splits[i], t2Splits[j]);
                costs[i][j] = cost;
            }
        }

        //adding artifitial suplier/demander
        // int costs[][] = new int[N_sup][N_dem];
        sup[N_sup - 1] = 0;
        dem[N_dem - 1] = 0;

        costs[N_sup - 1][N_dem - 1] = 0;
        for (i = 0; i < t1Nodes.length; i++) {
            costs[i][N_dem - 1] = SplitDist.getMinSize(t1Splits[i]);
        }
        for (i = 0; i < t2Nodes.length; i++) {
            costs[N_sup - 1][i] = SplitDist.getMinSize(t2Splits[i]);
        }

        if (total_sup > total_dem) {
            //add artifitial demander
            diff = total_sup - total_dem;
            dem[N_dem - 1] = diff;

        } else if (total_sup < total_dem) {
            //add artifitial suplier
            diff = total_dem - total_sup;
            sup[N_sup - 1] = diff;
        }
        Transportation tr = new Transportation(sup, dem, costs);
        FordFulkerson ff = new FordFulkerson(tr, new ShortestPathArray(tr));

        ff.compute();
        int result = tr.getCost();
        int flow = tr.getFlow();
        double final_dist = (double) result / (double) UNIT_MUL;
        return final_dist;
    }
}
