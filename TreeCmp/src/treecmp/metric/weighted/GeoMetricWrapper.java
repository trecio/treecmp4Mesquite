/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.weighted;

/**
 *
 * @author Damian
 */

import distanceAlg1.Geodesic;
import distanceAlg1.PhyloTree;
import pal.tree.*;
import polyAlg.PolyMain;
import treecmp.common.NodeUtilsExt;
/**
 *
 * @author Damian
 */
public class GeoMetricWrapper {

    /**
     *
     * @param t1
     * @param t2
     * @param rooted
     * @param logFileName - can be null
     * @return
     */
    public double getDistance(Tree t1, Tree t2, boolean rooted, String logFileName) {
        String tree1Newick = NodeUtilsExt.treeToSimpleString(t1, true);
        String tree2Newick = NodeUtilsExt.treeToSimpleString(t2, true);
        
        PhyloTree pt1 = new PhyloTree(tree1Newick, rooted);
        PhyloTree pt2 = new PhyloTree(tree2Newick, rooted);
        
        Geodesic geo = PolyMain.getGeodesic(pt1, pt2, logFileName);
        double dist = geo.getDist();     
        return dist;
    }
}
