/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.spr;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.SimpleNode;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.common.ClusterDist;
import treecmp.metric.topological.RFClusterMetric;


/**
 *
 * @author Damian
 */
public class SprUtils {

public static int num = 0;

    public static Tree[] generateSprNeighbours(Tree tree){

        int extNum = tree.getExternalNodeCount();
        int intNum = tree.getInternalNodeCount();
        IdGroup idGroup = TreeUtils.getLeafIdGroup(tree);
        int neighSize = calcSprNeighbours(tree);
        Set<TreeHolder> sprTreeSet = new HashSet<TreeHolder>((4*neighSize)/3);
       // System.out.println("Neigh siez="+neighSize);
        Node s,t;
        Tree resultTree;
        //leaf to leaf
        for (int i=0; i<extNum; i++){
            s = tree.getExternalNode(i);
            for (int j=0; j<extNum; j++){
                t = tree.getExternalNode(j);
                if (isValidMove(s,t)){
                    resultTree = createSprTree(tree,s,t);
                    sprTreeSet.add(new TreeHolder(resultTree,idGroup));
                   // System.out.println("neigbours/neighsize = "+sprTreeSet.size() +"/" +neighSize);
                }
            }            
        }
        //non-leaf and non-root to leaf
         for (int i=0; i<intNum; i++){
            s = tree.getInternalNode(i);
            if(s.isRoot())
                continue;
            for (int j=0; j<extNum; j++){
                t = tree.getExternalNode(j);
                if (isValidMove(s,t)){
                    resultTree = createSprTree(tree,s,t);
                    sprTreeSet.add(new TreeHolder(resultTree,idGroup));
                    //System.out.println("neigbours/neighsize = "+sprTreeSet.size() +"/" +neighSize);
                }
            }            
        }
        //leaf - non-leaf
         for (int i=0; i<extNum; i++){
            s = tree.getExternalNode(i);
            for (int j=0; j<intNum; j++){
                t = tree.getInternalNode(j);
                if (isValidMove(s,t)){
                    resultTree = createSprTree(tree,s,t);
                     sprTreeSet.add(new TreeHolder(resultTree,idGroup));
                     //System.out.println("neigbours/neighsize = "+sprTreeSet.size() +"/" +neighSize);
                }
            }
        }

        //non-leaf, non-root to non-leaf

         for (int i=0; i<intNum; i++){
            s = tree.getInternalNode(i);
            if(s.isRoot())
                continue;
            for (int j=0; j<intNum; j++){
                t = tree.getInternalNode(j);
                if (isValidMove(s,t)){
                    resultTree = createSprTree(tree,s,t);
                    if (resultTree != null){
                        sprTreeSet.add(new TreeHolder(resultTree,idGroup));
                       // System.out.println("neigbours/neighsize = "+sprTreeSet.size() +"/" +neighSize);
                    }
                }
            }
        }

        int n = sprTreeSet.size();
        Tree [] sprTreeArray = new Tree[n];
        int i=0;
        for (TreeHolder th: sprTreeSet ){
            sprTreeArray[i] = th.tree;
            i++;
        }
        return sprTreeArray;
    }

    public static boolean sameParent(Node n1, Node n2){
        boolean n1Root = n1.isRoot();
        boolean n2Root = n2.isRoot();

        if (n1Root && n1Root)
            return true;

        if (!n1Root && !n2Root){
            Node n1Parent = n1.getParent();
            Node n2Parent = n2.getParent();
            return (n1Parent == n2Parent);
        }
 
        return false;
    }
    
    public static boolean isChildParent(Node n1, Node n2){
      
        Node n1Parent = n1.getParent();
        Node n2Parent = n2.getParent();
        
        if (n2 == n1Parent || n1 == n2Parent)
            return true;
        
        return false;
    }
    
     public static boolean isInnerMove(Node s, Node t){
      
        Node lca = NodeUtils.getFirstCommonAncestor(s, t);       
        if (lca == s)
            return true;
        return false;
    }

    public static boolean isValidMove(Node s, Node t) {
        if (sameParent(s, t)) {
            return false;
        }
        if (isChildParent(s, t)) {
            return false;
        }
        if (isInnerMove(s, t)) {
            return false;
        }

        return true;

    }
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

    public static int calcSprNeighbours(Tree baseTree){
        int n= baseTree.getExternalNodeCount();
        int intNum = baseTree.getInternalNodeCount();
        Node node;
        int gammaTemp, gammaSum = 0;
        for (int i = 0; i<intNum; i++){
            node = baseTree.getInternalNode(i);
            if (node.isRoot())
                continue;
            gammaTemp = getNodeDepth(node)-1;
            gammaSum += gammaTemp;            
        }
        //based on "On the Combinatorics of Rooted Binary Phylogenetic Trees", Yun S. Song
        int neighNum = 2*(n-2)*(2*n-5)-2*gammaSum;
        
        return neighNum;
    }

    public static Tree createSprTree(Tree baseTree, Node s, Node t){
       // if (num ==45){
            
         //   int ggg=0;
        //}

        Tree resultTree = baseTree.getCopy();
        Node resultRoot = resultTree.getRoot();
        int sourceNum = s.getNumber();
        int targetNum = t.getNumber();

        Node source, target;
        if (s.isLeaf()){
            source = resultTree.getExternalNode(sourceNum);
        }else{
            source = resultTree.getInternalNode(sourceNum);
        }

        if (t.isLeaf()){
            target = resultTree.getExternalNode(targetNum);
        }else{
            target = resultTree.getInternalNode(targetNum);
        }

        Node sourceParent = source.getParent();
        Node targetParent = target.getParent();
        boolean isTargetRoot = target.isRoot();
        boolean isSourceParentRoot = sourceParent.isRoot();
         
        //it should be the same tree
        if (isTargetRoot && isSourceParentRoot)
            return null;

        Node otherSourceChild = findOtherChild(source,sourceParent);
        Node sourceParent2 = null;
        int sourceParentPos = -1;
        if (!isSourceParentRoot){    
            //remove degree 2 soureceParent vertex
            sourceParent2 = sourceParent.getParent();
            sourceParentPos = findChildPos(sourceParent,sourceParent2);
        }
          
        Node newNode = new SimpleNode();
        if (!isTargetRoot){                    
            //split target edge
            int targetPos = findChildPos(target,targetParent);
            targetParent.setChild(targetPos, newNode);
        }
 
        
        if (!isSourceParentRoot){
            //remove degree 2 soureceParent vertex
            sourceParent2.setChild(sourceParentPos, otherSourceChild);
        }
        newNode.addChild(target);
        newNode.addChild(source);


        SimpleTree newTree;



        if (isTargetRoot){            
            newNode.setParent(null);
            resultTree.setRoot(newNode);
            //newTree = new SimpleTree(newNode);

        } else if (isSourceParentRoot){            
            otherSourceChild.setParent(null);
            resultTree.setRoot(otherSourceChild);
            //newTree = new SimpleTree(otherSourceChild);
        } else{
            resultRoot.setParent(null);
            resultTree.setRoot(resultRoot);
            //newTree = new SimpleTree(resultRoot);

        }
       /* int N = newTree.getInternalNodeCount();
        if (N<4){
            int gg= 0 ;
        }
        newTree.createNodeList();
        //return resultTree;

*/
        /* OutputTarget out = OutputTarget.openString();
         TreeUtils.printNH(newTree,out,false,false);
         out.close();
        String treeString = out.getString();
        System.out.println(treeString + ": " +num);
        num++;*/

        //return newTree;
        return resultTree;
    }

    public static int findChildPos(Node child, Node parent){
        int childNum = parent.getChildCount();

        for (int i=0;i<childNum; i++){
            Node ch = parent.getChild(i);
            if (ch == child)
                return i;
        }

        return -1;
    }

      public static Node findOtherChild(Node child1, Node parent){
        int childNum = parent.getChildCount();

        for (int i=0;i<childNum; i++){
            Node ch = parent.getChild(i);
            if (ch != child1)
                return ch;
        }

        return null;
    }
            
}

class TreeHolder {
    public Tree tree;
    public IdGroup idGroup;
    public int hash;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        
        TreeHolder ref = (TreeHolder)obj;
        double dist = RFClusterMetric.getRFClusterMetric(tree, ref.tree);
        if (dist == 0){
       /*     OutputTarget out1 = OutputTarget.openString();
            TreeUtils.printNH(tree,out1,false,false);
            out1.close();
            String treeString1 = out1.getString();

            OutputTarget out2 = OutputTarget.openString();
            TreeUtils.printNH(ref.tree,out2,false,false);
            out2.close();
            String treeString2 = out2.getString();

            System.out.println("drzewa rowne 1: "+treeString1);
            System.out.println("drzewa rowne 2: "+treeString2);
            */
            return true;
        }
        else
           return false;

    }

    public TreeHolder(Tree t, IdGroup idGroup ){
        this.idGroup = idGroup;
        this.tree = t;
       //  OutputTarget out = OutputTarget.openString();
       //         TreeUtils.printNH(t,out,false,false);
        //        out.close();
         //       System.out.print(out.getString());

        BitSet[] bsArray = ClusterDist.RootedTree2BitSetArray(t, idGroup);;
        BitSet bs;
        int totlalHash = 0;
        int partialHash;
        for(int i=0; i<bsArray.length; i++){
            bs = bsArray[i];
            partialHash = bs.hashCode();
            totlalHash ^= partialHash;
        }
        this.hash = totlalHash;
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
   
}