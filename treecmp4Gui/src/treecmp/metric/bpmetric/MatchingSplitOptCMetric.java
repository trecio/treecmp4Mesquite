/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import java.util.BitSet;
import pal.misc.IdGroup;
import pal.tree.SplitSystem;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import treecmp.common.LapSolverDllWrapper;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;
/**
 *
 * @author Damian
 */
public class MatchingSplitOptCMetric extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2) {

        int i, j;
        int metric, w;

        long start = System.currentTimeMillis();

        SplitSystem s1 = SplitUtils.getSplits(t1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);

        BitSet[] bs1=SplitDist.SplitSystem2BitSetArray(s1);
        BitSet[] bs2=SplitDist.SplitSystem2BitSetArray(s2);
        int size = s1.getSplitCount();
        int n=s1.getLabelCount();

        int [][] assigncost = new int[size][size];

        int [] rowsol = new int[size];
        int [] colsol = new int[size];
        int [] u = new int[size];
        int [] v = new int[size];

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                //w = SplitDist.getDist1Int(s1.getSplit(i), s2.getSplit(j));
                w=SplitDist.getDist1Bit(bs1[i], bs2[j],n);
                assigncost[i][j]=w;
            }
        }

       long end_matrix = System.currentTimeMillis();

       System.out.println("Execution of matrix calculation time was "+(end_matrix-start)+" ms.");


//    String split1=s1.toString();
//    String split2=s2.toString();
//
//    System.out.println("Split1:" +s1+" Split2:"+s2 +"" +"\n");


        //metric=MatchingSplitOptCMetric.lap(size, assigncost, rowsol, colsol, u, v);
        metric=LapSolverDllWrapper.lap(size, assigncost, rowsol, colsol, u, v);
        long end = System.currentTimeMillis();
        System.out.println("Execution of LapSolver time was "+(end-end_matrix)+" ms.");
        return metric;

    }
}
