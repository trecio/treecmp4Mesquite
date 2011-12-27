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
 */
public class ActiveMetricsSet {

    private static ActiveMetricsSet AMset;
    private Vector<Metric> metricList;

    protected ActiveMetricsSet()
    {
        AMset=null;
        metricList=new Vector<Metric>();
        metricList.clear();

    }

    public static ActiveMetricsSet getActiveMetricsSet()
    {
        if(AMset==null)
        {
            AMset=new ActiveMetricsSet();
        }
        return AMset;
    }

    public void addMetric(Metric m)
    {

        /**
         *
         * Here can be added a protection against adding the same metric more than onec
         */

        this.metricList.add(m);

    }
    public Vector<Metric> getActiveMetrics()
    {

        return this.metricList;
    }

     public Metric[] getActiveMetricsTable()
    {
        int size=this.metricList.size();
        Metric[] mTable=new Metric[size];

        for(int i=0;i<size;i++)
        {
            mTable[i]=this.metricList.get(i);

        }


         return mTable;
    }


}
