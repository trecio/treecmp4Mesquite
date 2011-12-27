/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;
import treecmp.MetricUtils;

/**
 *
 * @author Damian
 */
public class MatchingClusterSumAbsDiff extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2)
    {
       int N=t1.getExternalNodeCount();
       Node node_t1, node_t2;
       String nodeName="";
       int i,d1,d2,sum;

       sum=0;

       for(i=0;i<N;i++){
           node_t1=t1.getExternalNode(i);
           d1=MetricUtils.getNumInernalNodesToRoot(node_t1);

           nodeName=node_t1.getIdentifier().getName();
           node_t2=TreeUtils.getNodeByName(t2, nodeName);

           d2=MetricUtils.getNumInernalNodesToRoot(node_t2);

           sum+=Math.abs(d1-d2);
       }

       return (double)(sum);
    }
}
