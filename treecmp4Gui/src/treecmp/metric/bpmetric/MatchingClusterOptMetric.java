/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import java.util.Vector;
import pal.misc.IdGroup;
import pal.tree.CladeSystem;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MatchingClusterOptMetric extends BaseMetric implements Metric{
    private Vector<Integer> edgeCosts;
    private boolean calcEdgeCosts;

    public boolean isCalcEdgeCosts() {
        return calcEdgeCosts;
    }

    public Vector<Integer> getEdgeCosts() {
        return edgeCosts;
    }

    public void setCalcEdgeCosts(boolean calcEdgeCosts) {
        this.calcEdgeCosts = calcEdgeCosts;
    }

    public void clearCalcEdgeCosts(){
        edgeCosts.clear();
    }

    public MatchingClusterOptMetric() {
        super();
        calcEdgeCosts=false;
        edgeCosts=new Vector<Integer>();
    }


    public double getDistance(Tree t1, Tree t2) {
        //  long start = System.currentTimeMillis();
        int i, j, metric;
        int N1 = t1.getInternalNodeCount();
        int N2 = t2.getInternalNodeCount();
        Node node_t1, node_t2;

        boolean[] clade_t1 = new boolean[t1.getExternalNodeCount()];
        boolean[] clade_t2 = new boolean[t2.getExternalNodeCount()];

        IdGroup idGroup = TreeUtils.getLeafIdGroup(t1);

        int size = Math.max(N1, N2) - 1;

        int[][] assigncost = new int[size][size];

        int[] rowsol = new int[size];
        int[] colsol = new int[size];
        int[] u = new int[size];
        int[] v = new int[size];

        for (i = 0; i < N1; i++) {
            node_t1 = t1.getInternalNode(i);
            if (node_t1.isRoot()) {
                continue;
            }
            CladeSystem.getClade(idGroup, node_t1, clade_t1);

            for (j = 0; j < N2; j++) {
                node_t2 = t2.getInternalNode(j);
                if (node_t2.isRoot()) {
                    continue;
                }
                CladeSystem.getClade(idGroup, node_t2, clade_t2);
                assigncost[i][j] = ClusterDist.clusterXor(clade_t1, clade_t2);
            }
        }

        metric = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
         //calc edge Costs
        if(calcEdgeCosts){
            for (i = 0; i < size; i++){
                j = rowsol[i];
                edgeCosts.add(assigncost[i][j]);
            }
       }
        return metric;

    }
}
