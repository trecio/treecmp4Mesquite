/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.tree.Tree;
import pal.tree.TreeTool;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class GeoUnrootedMetric extends BaseMetric implements Metric {

    private GeoMetricWrapper geoMetricWrapper = new GeoMetricWrapper();

    @Override
    public boolean isRooted() {
        return false;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        Tree t1u = TreeTool.getUnrooted(t1);
        Tree t2u = TreeTool.getUnrooted(t2);
        
        //TODO: Need to check if the method handles unrooted trees properly
        //If not it might be good to pass ther originally unrooted trees as
        //trees rooted in a common leaf.
        double dist = geoMetricWrapper.getDistance(t1u, t2u, false, null);
        return dist;
    }
}
