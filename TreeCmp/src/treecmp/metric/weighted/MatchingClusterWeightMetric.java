/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClustIntersectInfoMatrix;
import treecmp.common.LapSolver;
import treecmp.common.TreeCmpUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian Bogdanowicz
 */
public class MatchingClusterWeightMetric extends BaseMetric implements Metric {

    public static final int UNIT_MUL = 10000;

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
        Node[] t1Nodes = new Node[t1_TotNum - 1];
        Node[] t2Nodes = new Node[t2_TotNum - 1];
        int i, j;
       
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
        
        IdGroup idGroup = TreeUtils.getLeafIdGroup(t1);
        //first calculate the intersection matrix in order to 
        //quickly compute distance between clusters     
        ClustIntersectInfoMatrix cIntM = TreeCmpUtils.calcClustIntersectMatrix(t1, t2, idGroup);
        
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
        Node t1Node, t2Node;
        //calculate costs
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {  
                if (i < t1Nodes.length && j < t2Nodes.length) {                    
                    t1Node = t1Nodes[i];
                    t2Node = t2Nodes[j];
                    haO = cIntM.getSizeT1(t1Node);
                    hbO = cIntM.getSizeT2(t2Node);
                    hab = haO + hbO - (cIntM.getInterSize(t1Node, t2Node) << 1);

                    fa = (int) (t1Node.getBranchLength() * UNIT_MUL);
                    gb = (int) (t2Node.getBranchLength() * UNIT_MUL);

                    s1 = Math.min(fa, gb) * hab;
                    s2 = inc(fa, gb) * haO;
                    s3 = inc(gb, fa) * hbO;

                    assigncost[i][j] = s1 + s2 + s3;

                } else if (i < t1Nodes.length && j >= t2Nodes.length) {
                    t1Node = t1Nodes[i];
                    fa = (int) (t1Node.getBranchLength() * UNIT_MUL);
                    haO = cIntM.getSizeT1(t1Node);

                    s1 = fa * haO;
                    assigncost[i][j] = s1;

                } else if (i >= t1Nodes.length && j < t2Nodes.length) {
                    t2Node = t2Nodes[j];
                    gb = (int) (t2Node.getBranchLength() * UNIT_MUL);
                    hbO = cIntM.getSizeT2(t2Node);

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
