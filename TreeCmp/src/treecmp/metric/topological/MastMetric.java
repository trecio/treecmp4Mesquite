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

package treecmp.metric.topological;

import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

//TODO to be implemented!!!
public class MastMetric extends BaseMetric implements Metric {

    @Override
    public boolean isRooted() {
        return true;
    }
    
    @Override
    public double getDistance(Tree t1, Tree t2) {
        throw new UnsupportedOperationException("This metric has not been implemented yet!");
    }
}
