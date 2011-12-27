/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp;

import pal.tree.Node;

/**
 *
 * @author Damian
 */
public class MetricUtils {

    public static int getNumInernalNodesToRoot(Node n)
    {

        if (n.isRoot())
            return 0;

        int counter=0;
        Node parent=n.getParent();
        while(!parent.isRoot()) {
            counter++;
            parent=parent.getParent();
        }
        
        return counter;
    }


}
