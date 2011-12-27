/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric;

import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class UA_RFMetric extends BaseMetric implements Metric {

    public static double getARFDistance(Tree t1, Tree t2) {

        double dist = 2.0*TreeUtils.getRobinsonFouldsDistance(t1, t2);

        return dist;

    }



    public double getDistance(Tree t1, Tree t2) {

        return UA_RFMetric.getARFDistance(t1, t2);

    }
}
