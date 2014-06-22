/** This file is part of TreeCmp, a tool for comparing phylogenetic trees
    using the Matching Split distance and other metrics.
    Copyright (C) 2011,  Damian Bogdanowicz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package treecmp.config;

import java.util.List;
import java.util.ArrayList;
import treecmp.metric.Metric;

public class DefinedMetricsSet {
    private static DefinedMetricsSet instance;
    private ArrayList<Metric> metricList;
    
    private DefinedMetricsSet()
    {
        metricList=new ArrayList<Metric>();
    }
    
    public static synchronized DefinedMetricsSet getInstance()
    {
        if(instance==null)
        {
            instance=new DefinedMetricsSet(); 
        }
        return instance;
    }
         
    public void addMetric(Metric m)
    {
    	metricList.add(m);
    }
    
    public List<Metric> getDefinedMetrics()
    {

        return this.metricList;
    }


}
