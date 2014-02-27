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

import pal.tree.SimpleTree;
import pal.tree.Tree;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 * UMAST metric.
 * Implementation of Procedure 1
 * Farach, Martin and Thorup, Mikkel; Fast comparison of evolutionary trees.
 */
public class UMASTMetric extends BaseMetric implements Metric {

    @Override
    public boolean isRooted() {
        return false;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
    	RMASTMetric rmast = new RMASTMetric();
    	final SimpleTree tree1 = new SimpleTree(t1);
    	final SimpleTree tree2 = new SimpleTree(t2);
    	
    	double umast = Double.MAX_VALUE;
    	for (int i=0; i<tree1.getInternalNodeCount(); i++) {
    		tree1.reroot(tree1.getInternalNode(i));
    		for (int j=0; j<tree2.getInternalNodeCount(); j++) {
    			tree2.reroot(tree2.getInternalNode(j));
    			umast = Math.min(umast, rmast.getDistance(tree1, tree2));
    		}
    	}
    	
    	return umast;
    }
}
