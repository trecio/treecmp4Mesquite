/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

/**
 *
 * @author Damian
 */
public class ClusterDist {

    public ClusterDist() {
    }

    public static int clusterXor(boolean[] clade_t1, boolean[] clade_t2)
    {
        int n=clade_t1.length;
        int neq=0;

        for(int i=0;i<n;i++) {
            if(clade_t1[i]!=clade_t2[i]) neq++;
        }

        return  neq;
    }
}
