package treecmp.metric.weighted;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import pal.misc.IdGroup;
import pal.tree.*;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class NodalL2EdgeWeightMetric extends BaseMetric implements Metric {

    @Override
    public boolean isRooted() {
        return false;
    }

    @Override
    public double getDistance(Tree t1, Tree t2) {
        double dist, diff;
        String n1, n2;
        int row1, col1, row2, col2;

        TreeDistanceMatrix tr1 = new TreeDistanceMatrix(t1);
        TreeDistanceMatrix tr2 = new TreeDistanceMatrix(t2);

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
                diff = tr1.getDistance(row1, col1) - tr2.getDistance(row2, col2);
                dist += diff * diff;
            }
        }
        return Math.sqrt(dist);
    }
}
