/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

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
public class MatchingSplitSum extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2)
    {
        SplitSystem s1 = SplitUtils.getSplits(t1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);
        int size = s1.getSplitCount();
        int i,sum_t1,sum_t2;;
        
        sum_t1=0;
        for (i = 0; i < size; i++) {
            sum_t1+= SplitDist.getMinSize(s1.getSplit(i));
        }

        sum_t2=0;
        for (i = 0; i < size; i++) {
            sum_t2+= SplitDist.getMinSize(s2.getSplit(i));
        }

        return (double)(sum_t1+sum_t2);
    }
}
