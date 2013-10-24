/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import distanceAlg1.Geodesic;
import distanceAlg1.PhyloTree;
import pal.io.OutputTarget;
import pal.tree.NodeUtils;
import pal.tree.Tree;
import pal.tree.TreeTool;
import polyAlg.PolyMain;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian Bogdanowicz
 */
public class OldGeoMetric extends BaseMetric implements Metric {

    private final static String GEO_OUTPUT = "gtp.log";

    @Override
    public boolean isRooted() {
        return true;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {

        Tree t1u = TreeTool.getUnrooted(t1);
        Tree t2u = TreeTool.getUnrooted(t2);

        OutputTarget tree1Geo = OutputTarget.openString();
        OutputTarget tree2Geo = OutputTarget.openString();

        NodeUtils.printNH(tree1Geo, t1u.getRoot(), true, false, 0, false);
        NodeUtils.printNH(tree2Geo, t2u.getRoot(), true, false, 0, false);

        String tree1Newick = tree1Geo.getString();
        String tree2Newick = tree2Geo.getString();

        tree1Geo.close();
        tree2Geo.close();

        PhyloTree pt1 = new PhyloTree(tree1Newick, false);
        PhyloTree pt2 = new PhyloTree(tree2Newick, false);

        Geodesic geo = PolyMain.getGeodesic(pt1, pt2, GEO_OUTPUT);

        geo.getDist();
        double dist = geo.getDist();
        return dist;
    }
}
