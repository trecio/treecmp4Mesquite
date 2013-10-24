/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.spr;

import treecmp.metric.Metric;
import treecmp.metric.topological.NodalL2SplittedMetric;

/**
 *
 * @author Damian
 */
public class SprHeuristicNsRfcMetric extends SprHeuristicRfcBaseMetric{

 @Override
protected Metric getMetric(){
    return new NodalL2SplittedMetric();
 }
}
