/**
 * This file is part of TreeCmp, a tool for comparing phylogenetic trees using
 * the Matching Split distance and other metrics. Copyright (C) 2011, Damian
 * Bogdanowicz
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package treecmp.metric.topological;

import pal.misc.IdGroup;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.TreeCmpUtils;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

public class NodalL1Metric extends BaseMetric implements Metric {

    @Override
    public boolean isRooted() {
        return false;
    }

    public double getDistance(Tree t1, Tree t2) {
        double dist, diff;

        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);
        int[][] nsMatrix1 = TreeCmpUtils.calcNodalSplittedMatrix(t1, null);
        int[][] nsMatrix2 = TreeCmpUtils.calcNodalSplittedMatrix(t2, id1);

        dist = 0.0;
        for (int i = 0; i < id1.getIdCount(); i++) {
            for (int j = i + 1; j < id1.getIdCount(); j++) {
                diff = nsMatrix1[i][j] + nsMatrix1[j][i] - nsMatrix2[i][j] - nsMatrix2[j][i];
                dist += Math.abs(diff);
            }
        }
        return dist;
    }
}
