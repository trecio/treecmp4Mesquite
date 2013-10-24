/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.spr;

import treecmp.metric.Metric;
import treecmp.metric.topological.RFClusterMetric;

/**
 *
 * @author Damian
 */
public class SprHeuristicRFCMetric extends SprHeuristicBaseMetric{

 @Override
protected Metric getMetric(){
    return new RFClusterMetric();
 }
}
