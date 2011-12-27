/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.common;

import java.util.ArrayList;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class MinDistTreeHolder {

    private ArrayList<Tree> minDistTrees;
    private int maxTreesListSize;
    private double min;

    public MinDistTreeHolder() {
        this.min = Double.MAX_VALUE;
        minDistTrees = new ArrayList<Tree>();
        this.maxTreesListSize = Integer.MAX_VALUE;

    }

    public void clear() {
        this.min = Double.MAX_VALUE;
        this.maxTreesListSize = Integer.MAX_VALUE;
        this.minDistTrees.clear();
    }

    public ArrayList<Tree> getMinDistTrees() {
        return minDistTrees;
    }

    public double getMin() {
        return this.min;
    }

       public void updateMinTrees(double dist, Tree t)
    {



        if(dist==min && this.minDistTrees.size()<this.maxTreesListSize)
        {
            //add tree to minDist list
            this.minDistTrees.add(t);

        }else if(dist<min)
        {
            this.minDistTrees.clear();
            this.minDistTrees.add(t);
            min=dist;
        }

    }

      public int getNumOfTrees() {
        return this.minDistTrees.size();
    }
}
