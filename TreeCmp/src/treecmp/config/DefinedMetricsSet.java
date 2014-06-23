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

import java.util.Arrays;
import java.util.List;
import treecmp.metric.topological.MatchingClusterMetric;
import treecmp.metric.topological.MatchingSplitMetric;
import treecmp.metric.topological.NodalL2Metric;
import treecmp.metric.topological.NodalL2SplittedMetric;
import treecmp.metric.topological.QuartetMetricLong;
import treecmp.metric.topological.RFClusterMetric;
import treecmp.metric.topological.RFMetric;
import treecmp.metric.topological.RMASTMetric;
import treecmp.metric.topological.TripletMetric;
import treecmp.metric.topological.UMASTMetric;

public class DefinedMetricsSet {
    private static DefinedMetricsSet instance;
    private final List<DefinedMetric> metrics = Arrays.asList(
    		new DefinedMetric(MatchingSplitMetric.class, "MatchingSplit", "ms", "-Matching Split metric"),
    		new DefinedMetric(RFMetric.class, "R-F", "rf", "-Robinson-Foulds metric"),
    		new DefinedMetric(QuartetMetricLong.class, "Quartet", "qt", "-Quartet metric"),
    		new DefinedMetric(NodalL2Metric.class, "PathDiffernce", "pd", "-Path Difference metric"),
    		new DefinedMetric(NodalL2SplittedMetric.class, "NodalSplitted", "ns", "-Nodal Splitted metric"),
    		new DefinedMetric(MatchingClusterMetric.class, "MatchingCluster", "mc", "-Matching Cluster metric"),
    		new DefinedMetric(RFClusterMetric.class, "R-F_Cluster", "rc", "-Robinson-Foulds Cluster metric"),
    		new DefinedMetric(TripletMetric.class, "Triples", "tt", "-Triplet metric"),
    		new DefinedMetric(UMASTMetric.class, "UMAST", "umast", "-Unrooted MAST metric"),
    		new DefinedMetric(RMASTMetric.class, "RMAST", "rmast", "-Rooted MAST Metric")
	);
        
    public static synchronized DefinedMetricsSet getInstance()
    {
        if(instance==null)
        {
            instance=new DefinedMetricsSet(); 
        }
        return instance;
    }
    
	public DefinedMetric getDefinedMetric(String metricName) {
		for (final DefinedMetric metric : metrics) {
			if (metric.commandName.equals(metricName)) {
				return metric;
			}
		}
		return null;
	}

	public int size() {
		return metrics.size();
	}
}
