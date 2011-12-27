/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class TreeCountPair {

    public pal.tree.Tree tree;
    public int count;

    public TreeCountPair() {
    this.count=0;
    this.tree=null;
    }

    public TreeCountPair(Tree tree) {
        this.tree = tree;
        this.count=1;
    }
    
    
}
