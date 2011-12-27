/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import pal.tree.Node;

/**
 *
 * @author Damian
 */
public class TreeUtil2 {

    public static int getNodeDepth(Node node){
        int depth=0;

        if (node.isRoot())
            return 0;

        while(!node.isRoot()){
            depth++;
            node=node.getParent();
        }

        return depth;
    }
}
