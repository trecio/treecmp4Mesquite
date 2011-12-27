/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MatchingClusterMetricMax extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2) 
    {
       int N1=t1.getInternalNodeCount();
       int N2=t2.getInternalNodeCount();
       Node node_t1, node_t2;
       int i,count,c1,c2,sum,cmax;

       int n=t1.getExternalNodeCount();
        
       Vector<Integer> clusterSize_t1=new Vector<Integer>();
       Vector<Integer> clusterSize_t2=new Vector<Integer>();
       
       
       
       for(i=0;i<N1;i++){
           node_t1=t1.getInternalNode(i);
           if (node_t1.isRoot())
               continue;
           count=NodeUtils.getLeafCount(node_t1);   
           clusterSize_t1.add(new Integer(count));
       }
       
       for(i=0;i<N2;i++){
           node_t2=t2.getInternalNode(i);
           if (node_t2.isRoot())
               continue;
           count=NodeUtils.getLeafCount(node_t2);   
           clusterSize_t2.add(new Integer(count));
       }
       
       Collections.sort(clusterSize_t1,new CmpSort());
       Collections.sort(clusterSize_t2,new CmpSort());

       sum=0;
       for (i=0;i<clusterSize_t1.size();i++)
       {
           c1=clusterSize_t1.get(i);
           c2=clusterSize_t2.get(i);
           cmax=Math.min(c1+c2, 2*n-c1-c2);
           sum+=cmax;
       }
       
       
       return (double)sum;
    }


}

class CmpSort implements Comparator{

    public int compare(Object o1, Object o2) {
        
        int a=((Integer)o1).intValue();
        int b=((Integer)o2).intValue();
        return a-b;
    }
    
}
