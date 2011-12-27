/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;
/**
 *
 * @author Damian
 */
public class MatchingClusterAbsDiff extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2)
    {
       int N1=t1.getInternalNodeCount();
       int N2=t2.getInternalNodeCount();
       Node node_t1, node_t2;
       int i,count,c1,c2;


       c1=0;
       for(i=0;i<N1;i++){
           node_t1=t1.getInternalNode(i);
           if (node_t1.isRoot())
               continue;
           count=NodeUtils.getLeafCount(node_t1);
           c1+=count;
       }
       c2=0;
       for(i=0;i<N2;i++){
           node_t2=t2.getInternalNode(i);
           if (node_t2.isRoot())
               continue;
           count=NodeUtils.getLeafCount(node_t2);
           c2+=count;
       }




       return (double)(Math.abs(c1-c2));
    }
}
