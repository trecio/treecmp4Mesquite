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
public class NLGMetric extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2) {
        int[] matching;
        int i, j;
        double metric, sum, w;

        SplitSystem s1 = SplitUtils.getSplits(t1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);

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
                w = SplitDist.getNLGDist(s1.getSplit(i), s2.getSplit(j));
                matcher.setWeight(i, j, w);
                weights[i][j] = w;
            }
        }




        matching = matcher.getMatching();
        sum = 0.0;
        for (i = 0; i < matching.length; i++) {
            sum += weights[i][matching[i]];
        }

        metric = 1.0-sum/size;
        return Math.abs(metric);

    }

}
