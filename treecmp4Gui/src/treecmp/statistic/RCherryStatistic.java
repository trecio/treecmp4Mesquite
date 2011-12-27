/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.statistic;

import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class RCherryStatistic extends BaseStatistic implements Statistic{

    public double getStatistic(Tree t) {

        int N=t.getInternalNodeCount();
        Node node_t;
        int count;
        int num_cherry=0;

        for(int i=0;i<N;i++){
           node_t=t.getInternalNode(i);
           if (node_t.isRoot())
               continue;
           count=NodeUtils.getLeafCount(node_t);
           if(count==2)
               num_cherry++;
        }

        return (double)num_cherry;
    }

}
