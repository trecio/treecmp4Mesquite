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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

public class ActiveMetricsSet {

    private static ActiveMetricsSet instance;
    private final ArrayList<Metric> metricList;
    private final Logger log = Logger.getLogger(ActiveMetricsSet.class.getName()); 

    private ActiveMetricsSet()
    {
        metricList=new ArrayList<Metric>();
    }

    public static synchronized ActiveMetricsSet getInstance()
    {
        if(instance==null)
        {
        	instance=new ActiveMetricsSet();
        }
        return instance;
    }

    public void addMetric(DefinedMetric definedMetric)
    {
    	final BaseMetric metric;
		try {
			metric = definedMetric.implementation.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.log(Level.SEVERE, "Could not create metric object.", e);
			return;
		}
		metric.setName(definedMetric.name);
		metric.setCommandLineName(definedMetric.commandName);
		metric.setDescription(definedMetric.description);
		metric.setUnifomFileName(definedMetric.uniformFileName);
		metric.setYuleFileName(definedMetric.yuleFileName);
		metric.setAlnFileSuffix(definedMetric.alnFileSuffix);
		metric.setDiffLeafSets(definedMetric.diffLeaves);
        this.metricList.add(metric);

    }
    public ArrayList<Metric> getActiveMetrics()
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
