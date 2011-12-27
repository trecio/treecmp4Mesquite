/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.config;

import java.util.Vector;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 *
 * DefinedMetricsSet is implemeted as singleton
 * 
 * 
 */


public class DefinedMetricsSet {

    private static DefinedMetricsSet DMset;
    private Vector<Metric> metricList;
    
    protected DefinedMetricsSet()
    {
        DMset=null;
        metricList=new Vector<Metric>();
        metricList.clear();

    }
    
    public static DefinedMetricsSet getDefinedMetricsSet()
    {
        if(DMset==null)
        {
            DMset=new DefinedMetricsSet(); 
        }
        return DMset;
    }
         
    public void addMetric(Metric m)
    {

        /**
         *
         * Here can be added a protection against adding the same metric more than onec
         */

        this.metricList.add(m);

    }
    public Vector<Metric> getDefinedMetrics()
    {

        return this.metricList;
    }


}
