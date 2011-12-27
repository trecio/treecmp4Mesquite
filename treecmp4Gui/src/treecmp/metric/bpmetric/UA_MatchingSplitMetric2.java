/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import treecmp.common.BipartiteMatcher;
import pal.misc.IdGroup;
import pal.tree.SplitSystem;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class UA_MatchingSplitMetric2 extends BaseMetric implements Metric  {

     public double getDistance(Tree tree1, Tree tree2) {

        int[] matching;
        int i, j;
        double metric, sum, w;

        SplitSystem s1 = SplitUtils.getSplits(tree1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, tree2);

        int size1 = s1.getSplitCount();
        int size2 = s2.getSplitCount();

        int size =Math.max(size1, size2);

        double weights[][] = new double[size][size];

        BipartiteMatcher matcher = new BipartiteMatcher(size);
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                matcher.setWeight(i, j, 0.0);
                weights[i][j] = 0.0;
            }
        }

        if (size1>size2){
            for (i = 0; i < size1; i++) {
                for (j = 0; j < size1; j++) {

                    if (j<size2)
                        w = -SplitDist.getDist1(s1.getSplit(i), s2.getSplit(j));
                    else
                        w = -SplitDist.getDistToO_2(s1.getSplit(i));

                    matcher.setWeight(i, j, w);
                    weights[i][j] = w;
                }
            }
        }else  {

             for (i = 0; i < size2; i++) {
                for (j = 0; j < size2; j++) {

                    if (j<size1)
                        w = -SplitDist.getDist1(s2.getSplit(i), s1.getSplit(j));
                    else
                        w = -SplitDist.getDistToO_2(s2.getSplit(i));

                    matcher.setWeight(i, j, w);
                    weights[i][j] = w;
                }
            }



        }



        matching = matcher.getMatching();
        sum = 0.0;
        for (i = 0; i < matching.length; i++) {
            sum += weights[i][matching[i]];
        }

        metric = -sum;
        return Math.abs(metric);

    }
}
