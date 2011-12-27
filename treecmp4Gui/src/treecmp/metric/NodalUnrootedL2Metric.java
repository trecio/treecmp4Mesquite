/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric;

import pal.misc.IdGroup;
import pal.tree.Tree;
import pal.tree.TreeDistanceMatrix;
import pal.tree.TreeTool;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class NodalUnrootedL2Metric extends BaseMetric implements Metric{

    public double getDistance(Tree t1, Tree t2) {
        double dist,diff;
        String n1, n2;
        int row1, col1, row2, col2;

        t1=TreeTool.getUnrooted(t1);
        t2=TreeTool.getUnrooted(t2);


        TreeDistanceMatrix tr1 = new TreeDistanceMatrix(t1, true, 0);
        TreeDistanceMatrix tr2 = new TreeDistanceMatrix(t2, true, 0);

        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);

        dist = 0.0;
        for (int i = 0; i < id1.getIdCount(); i++) {
            for (int j = i + 1; j < id1.getIdCount(); j++) {

                n1 = id1.getIdentifier(i).getName();
                n2 = id1.getIdentifier(j).getName();
                row1 = tr1.whichIdNumber(n1);
                col1 = tr1.whichIdNumber(n2);
                row2 = tr2.whichIdNumber(n1);
                col2 = tr2.whichIdNumber(n2);
                diff=tr1.getDistance(row1, col1) - tr2.getDistance(row2, col2);
                dist += diff*diff;

            }
        }
        return Math.sqrt(dist);
    }

}
