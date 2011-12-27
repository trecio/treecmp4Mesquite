/** This file is part of MSdist, a program for computing the Matching Split
    distance between phylogenetic trees.
    Copyright (C) 2010,  Damian Bogdanowicz

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

package treecmp.metric.bpmetric;

import java.util.BitSet;
import pal.misc.IdGroup;
import pal.tree.SplitSystem;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

public class MatchingSplitMetric extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2) {

        int i, j;
        int metric;

        SplitSystem s1 = SplitUtils.getSplits(t1);
        IdGroup idGroup = s1.getIdGroup();
        SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);

        int size1 = s1.getSplitCount();
        int size2 = s2.getSplitCount();
        int size = Math.max(size1, size2);

        if(size<=0)
            return 0;

        BitSet[] bs1 = SplitDist.SplitSystem2BitSetArray(s1);
        BitSet[] bs2 = SplitDist.SplitSystem2BitSetArray(s2);
        int n = s1.getLabelCount();

        //int[][] assigncost = new int[size][size];
        short[][] assigncost = new short[size][size];
        int[] rowsol = new int[size];
        int[] colsol = new int[size];
        int[] u = new int[size];
        int[] v = new int[size];


        if (size1 > size2) {
            for (i = 0; i < size1; i++) {
                for (j = 0; j < size2; j++) {
                    assigncost[i][j] = (short)SplitDist.getDist1Bit(bs1[i], bs2[j], n);
                }
                for (j = size2; j < size1; j++) {
                    assigncost[i][j] = (short)SplitDist.getDistToOAsMinBit(bs1[i], n);
                }
            }
        } else {
            for (i = 0; i < size2; i++) {
                for (j = 0; j < size1; j++) {
                    assigncost[i][j] = (short)SplitDist.getDist1Bit(bs2[i], bs1[j], n);
                }
                for (j = size1; j < size2; j++) {
                    assigncost[i][j] = (short)SplitDist.getDistToOAsMinBit(bs2[i], n);
                }
            }
        }

        //metric = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
        metric = LapSolver.lapShort(size, assigncost, rowsol, colsol, u, v);
        return metric;

    }
}
