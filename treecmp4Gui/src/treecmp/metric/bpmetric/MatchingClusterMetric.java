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
import treecmp.common.BipartiteMatcher;
import pal.misc.IdGroup;
import pal.tree.CladeSystem;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class MatchingClusterMetric extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2) {

       int N1=t1.getInternalNodeCount();
       int N2=t2.getInternalNodeCount();
       Node node_t1, node_t2;
       int i,j;
       double clusterXorDist,sum,metric;
       int[] matching;

       boolean[] clade_t1=new boolean[t1.getExternalNodeCount()];
       boolean[] clade_t2=new boolean[t2.getExternalNodeCount()];

       IdGroup idGroup=TreeUtils.getLeafIdGroup(t1);

        int size=Math.max(N1, N2)-1;

      double weights[][] = new double[size][size];

        BipartiteMatcher matcher = new BipartiteMatcher(size);
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                matcher.setWeight(i, j, 0.0);
                weights[i][j] = 0.0;
            }
        }


       for(i=0;i<N1;i++){
           node_t1=t1.getInternalNode(i);
           if (node_t1.isRoot())
               continue;
           CladeSystem.getClade(idGroup, node_t1, clade_t1);

            for(j=0;j<N2;j++){
                node_t2=t2.getInternalNode(j);
                if (node_t2.isRoot())
                    continue;
                CladeSystem.getClade(idGroup, node_t2, clade_t2);

                clusterXorDist=-clusterXor(clade_t1,clade_t2);

                matcher.setWeight(i, j, clusterXorDist);
                weights[i][j] = clusterXorDist;
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

private double clusterXor(boolean[] clade_t1, boolean[] clade_t2)
{
    int n=clade_t1.length;
    int neq=0;

    for(int i=0;i<n;i++) {
        if(clade_t1[i]!=clade_t2[i]) neq++;
    }
    
    return (double) neq;
}


}
