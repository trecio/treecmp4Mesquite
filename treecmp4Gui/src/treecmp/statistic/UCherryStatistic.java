/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.statistic;

import pal.tree.SplitSystem;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import treecmp.metric.bpmetric.SplitDist;

/**
 *
 * @author Damian
 */
public class UCherryStatistic extends BaseStatistic implements Statistic {

    public double getStatistic(Tree t) {

        SplitSystem s = SplitUtils.getSplits(t);
        int size = s.getSplitCount();
        int min,max;
        int num_cherry=0;

        for (int i = 0; i < size; i++) {
           
            min=SplitDist.getMinSize(s.getSplit(i));   
            max=SplitDist.getMaxSize(s.getSplit(i));
            
            if(min == 2)
                num_cherry++;
            //could happen only if there are exactly 4 leaves in tree t and min=2
            if(max == 2)
                num_cherry++;
        }
        
        return (double)num_cherry;
    }
}
