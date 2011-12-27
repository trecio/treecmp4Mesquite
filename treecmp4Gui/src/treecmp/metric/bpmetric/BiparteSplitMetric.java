/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import pal.tree.*;
import pal.misc.IdGroup;
import treecmp.common.BipartiteMatcher;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class BiparteSplitMetric extends BaseMetric implements Metric {

    //private String name = "BiparteSplitMetric";
    
        public static double getBiparteSplitDistance(Tree tree1, Tree tree2) {


        int[] matching;
        int i, j;
        double metric, sum, w;

        SplitSystem s1 = SplitUtils.getSplits(tree1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, tree2);

        int size = s1.getSplitCount();


        double weights[][] = new double[size][size];

        BipartiteMatcher matcher = new BipartiteMatcher(size);
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                matcher.setWeight(i, j, 0.0);
                weights[i][j] = 0.0;
            }
        }


        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                w = -SplitDist.getDist1(s1.getSplit(i), s2.getSplit(j));
                matcher.setWeight(i, j, w);
                weights[i][j] = w;
            }
        }

//    String split1=s1.toString();
//    String split2=s2.toString();
//   
//    System.out.println("Split1:" +s1+" Split2:"+s2 +"" +"\n");


        matching = matcher.getMatching();
        sum = 0.0;
        for (i = 0; i < matching.length; i++) {
            sum += weights[i][matching[i]];
        }

        metric = -sum;
        return Math.abs(metric);

    }
    

    public double getDistance(Tree t1, Tree t2) {

        return getBiparteSplitDistance(t1, t2);
    }
        
        
}
