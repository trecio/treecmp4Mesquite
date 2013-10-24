/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.spr;

import treecmp.metric.topological.RFClusterMetric;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pal.io.OutputTarget;
import treecmp.common.TreeCmpException;
import treecmp.metric.*;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public abstract class SprHeuristicRfcBaseMetric extends BaseMetric implements Metric {

    private Metric m = getMetric();
    private Metric mRF = new RFClusterMetric();

    @Override
    public boolean isRooted() {
        return true;
    }
   
    public double getDistance(Tree tree1, Tree tree2) {
        double dist = 0;
        double startDist = 0;
      //  OutputTarget out = OutputTarget.openString();
     //   TreeUtils.printNH(tree1,out,false,false);
     //   out.close();
     //   System.out.println(super.getName());
     //   System.out.print(out.getString());
        ArrayList<Tree> bestTreeList = new  ArrayList<Tree>();

        try {
            startDist = mRF.getDistance(tree1, tree2);
            if (startDist == 0) {
                return 0;
            }
            int startLeafNum = tree1.getExternalNodeCount();
        //    System.out.println("Number of leaves: " + startLeafNum);
            Tree[] reducedTrees = SubtreeUtils.reduceCommonBinarySubtrees(tree1, tree2);

            Tree t1 = reducedTrees[0];
            Tree t2 = reducedTrees[1];

            int reducedLeafNum = t1.getExternalNodeCount();

         //   System.out.println("Number of leaves after reduction: " + reducedLeafNum);

            int sprDist = 0;
            Tree[] treeList;
            Tree bestTree = null;
            Tree tempTree = null;
            double bestDist, tempDist;
            Tree currentStepTree = t1;
            double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
            do {
                treeList = SprUtils.generateSprNeighbours(currentStepTree);
                bestDist = Double.POSITIVE_INFINITY;
                bestTreeList.clear();
                tempDist = 0;
                sprDist++;
                for (int i = 0; i < treeList.length; i++) {
                    tempTree = treeList[i];
                    tempDist = mRF.getDistance(tempTree, t2);
                    if (tempDist < bestDist) {
                        bestTreeList.clear();
                        bestDist = tempDist;
                        bestTree = tempTree;
                        bestTreeList.add(bestTree);
                    }else if (tempDist == bestDist){
                        bestTreeList.add(tempTree);
                    }
                }
                currentStepTree = findBestTree(bestTreeList,t2);
                bestDist1 = bestDist2;
                bestDist2 = bestDist;
                if (bestDist1 <= bestDist2){
                    return Double.POSITIVE_INFINITY;
                }

            } while (bestDist != 0);

            dist = (double) sprDist;
        } catch (TreeCmpException ex) {
            Logger.getLogger(SprHeuristicRfcBaseMetric.class.getName()).log(Level.SEVERE, null, ex);
        }


        return dist;
    }

    private Tree findBestTree(List<Tree> treeList, Tree t2) {
        Tree bestTree = null;
        double tempDist = 0;
        double bestDist = Double.POSITIVE_INFINITY;
        try {
            for (Tree tempTree : treeList) {
                tempDist = m.getDistance(tempTree, t2);
                if (tempDist < bestDist) {
                    bestDist = tempDist;
                    bestTree = tempTree;
                }
            }
        } catch (TreeCmpException ex) {
            Logger.getLogger(SprHeuristicRfcBaseMetric.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bestTree;
    }


    protected abstract Metric getMetric();
}
