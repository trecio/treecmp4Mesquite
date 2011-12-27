/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import pal.io.OutputTarget;
import pal.tree.Tree;
import pal.tree.TreeManipulator;
import pal.tree.TreeUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MsMc2CompareMetric extends BaseMetric implements Metric{
public double getDistance(Tree t1, Tree t2) {
        //  long start = System.currentTimeMillis();
        int i, j;
        
        Metric mc=new MatchingClusterOptMetric();
        Metric ms=new MatchingSplitOptMetric();
        Tree [] trees1=TreeManipulator.getEveryRoot(t1);
        Tree [] trees2=TreeManipulator.getEveryRoot(t2);
        int N1 = trees1.length;
        int N2 = trees2.length;
        Tree t1r,t2r,t1_temp,t2_temp;
        double mcMin=Double.POSITIVE_INFINITY;
        double dist;
        
        /*
        OutputTarget out = OutputTarget.openString();
        TreeUtils.printNH(trees1[i], out, false, false);
        String treeNewick = out.getString();
        out.close();
        System.out.print(treeNewick);
        */

        double msDist=ms.getDistance(t1, t2);
        
        for(i=0;i<trees1.length;i++){
            for(j=0;j<trees2.length;j++){
                t1r=trees1[i];
                t2r=trees2[j];
                dist=mc.getDistance(t1r, t2r);
                if(dist<mcMin){
                    mcMin=dist;
                    t1_temp=t1r;
                    t2_temp=t2r;
                }
            }
        }

        //NodeUtils
        return (msDist-mcMin);
}
}
