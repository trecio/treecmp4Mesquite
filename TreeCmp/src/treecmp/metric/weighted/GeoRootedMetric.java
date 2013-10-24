/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric.weighted;

import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class GeoRootedMetric extends BaseMetric implements Metric {

    private GeoMetricWrapper geoMetricWrapper = new GeoMetricWrapper();

    @Override
    public boolean isRooted() {
        return true;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        double dist = geoMetricWrapper.getDistance(t1, t2, true, null);
        return dist;
    }
}
