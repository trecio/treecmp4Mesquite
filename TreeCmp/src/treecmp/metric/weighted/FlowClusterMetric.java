/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import algs.model.network.FordFulkerson;
import algs.model.network.ShortestPathArray;
import algs.model.network.Transportation;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClustIntersectInfoMatrix;
import treecmp.common.TreeCmpUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian Bogdanowicz
 */
public class FlowClusterMetric extends BaseMetric implements Metric {

    private static final int UNIT_MUL = 10000;

    @Override
    public boolean isRooted() {
        return true;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        int t1_ExtNodes = t1.getExternalNodeCount();
        int t2_ExtNodes = t2.getExternalNodeCount();
        int t1_IntNodes = t1.getInternalNodeCount();
        int t2_IntNodes = t2.getInternalNodeCount();
        int t1_TotNum = t1_ExtNodes + t1_IntNodes;
        int t2_TotNum = t2_ExtNodes + t2_IntNodes;
        Node t1Node, t2Node, node;
        int i, j, diff;
        int total_dem = 0;
        int total_sup = 0;

        //dummy demmander and supplier are included
        int N_sup = t1_TotNum;
        int N_dem = t2_TotNum;
        int[] sup = new int[N_sup];
        int[] dem = new int[N_dem];
        Node[] t1Nodes = new Node[t1_TotNum - 1];
        Node[] t2Nodes = new Node[t2_TotNum - 1];

        int costs[][] = new int[N_sup][N_dem];

        IdGroup idGroup1 = TreeUtils.getLeafIdGroup(t1);

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

        for (i = 0; i < t1Nodes.length; i++) {
            node = t1Nodes[i];
            sup[i] = (int) (node.getBranchLength() * UNIT_MUL);

            total_sup += sup[i];
        }

        for (i = 0; i < t2Nodes.length; i++) {
            node = t2Nodes[i];
            dem[i] = (int) (node.getBranchLength() * UNIT_MUL);
            total_dem += dem[i];
        }

        //first calculate the intersection matrix in order to 
        //quickly compute distance between clusters     
       ClustIntersectInfoMatrix cIntM = TreeCmpUtils.calcClustIntersectMatrix(t1, t2, idGroup1);

        //calculate costs
        for (i = 0; i < t1Nodes.length; i++) {
            t1Node = t1Nodes[i];
            for (j = 0; j < t2Nodes.length; j++) {
                t2Node = t2Nodes[j];
                costs[i][j] = cIntM.getSizeT1(t1Node) + cIntM.getSizeT2(t2Node) - (cIntM.getInterSize(t1Node, t2Node) << 1);
            }
        }
        //adding artifitial suplier/demander
        // int costs[][] = new int[N_sup][N_dem];
        sup[N_sup - 1] = 0;
        dem[N_dem - 1] = 0;

        costs[N_sup - 1][N_dem - 1] = 0;
        for (i = 0; i < t1Nodes.length; i++) {
            t1Node = t1Nodes[i];
            costs[i][N_dem - 1] = cIntM.getSizeT1(t1Node);
        }
        for (i = 0; i < t2Nodes.length; i++) {
            t2Node = t2Nodes[i];
            costs[N_sup - 1][i] = cIntM.getSizeT2(t2Node);
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
