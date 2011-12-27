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
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class GMatchingSplitOptMetric1 extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2) {

        int i, j;
        int metric, w;

        SplitSystem s1 = SplitUtils.getSplits(t1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);

        BitSet[] bs1=SplitDist.SplitSystem2BitSetArray(s1);
        BitSet[] bs2=SplitDist.SplitSystem2BitSetArray(s2);
        
        int size1 = s1.getSplitCount();
        int size2 = s2.getSplitCount();

        int size =Math.max(size1, size2);
        
        int n=s1.getLabelCount();

        int [][] assigncost = new int[size][size];

        int [] rowsol = new int[size];
        int [] colsol = new int[size];
        int [] u = new int[size];
        int [] v = new int[size];

         if (size1>size2){
            for (i = 0; i < size1; i++) {
                for (j = 0; j < size1; j++) {

                    if (j<size2)
                       w=SplitDist.getDist1Bit(bs1[i], bs2[j],n);
                    else
                        w=SplitDist.getDistToOAsMinBit(bs1[i],n);

                    assigncost[i][j]=w;
                }
            }
        }else {

             for (i = 0; i < size2; i++) {
                for (j = 0; j < size2; j++) {

                    if (j<size1)
                        w = SplitDist.getDist1Bit(bs2[i], bs1[j],n);
                    else
                        w=SplitDist.getDistToOAsMinBit(bs2[i],n);

                    assigncost[i][j]=w;
                }
            }
        }



//    String split1=s1.toString();
//    String split2=s2.toString();
//
//    System.out.println("Split1:" +s1+" Split2:"+s2 +"" +"\n");


        metric=LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
        return metric;

    }
}
