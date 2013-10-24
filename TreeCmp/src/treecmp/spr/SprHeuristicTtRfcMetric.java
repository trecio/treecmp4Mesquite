/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.spr;

import treecmp.metric.Metric;
import treecmp.metric.topological.TripletMetric;

/**
 *
 * @author Damian
 */
public class SprHeuristicTtRfcMetric extends SprHeuristicRfcBaseMetric{

 @Override
 protected Metric getMetric(){
    return new TripletMetric();
 }
}
