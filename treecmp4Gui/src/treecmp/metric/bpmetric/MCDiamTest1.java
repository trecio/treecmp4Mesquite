/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;
import java.util.Vector;
import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.NodeUtils;
import pal.tree.SimpleNode;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeManipulator;
import pal.tree.TreeUtils;
import treecmp.common.TreeUtil2;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;
/**
 *
 * @author Damian
 */
public class MCDiamTest1 extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2) {

        Metric mc = new MatchingClusterOptMetric();
        int n = t1.getExternalNodeCount();
        int nn = n + 1;

        String newName=Integer.toString(nn);

        Tree[] trees1 =genAllTrees(t1);
        Tree[] trees2 =genAllTrees(t2);

        Tree t1_min=null, t1_max=null, t2_min=null, t2_max=null, t1r, t2r,t1_cand,t2_cand;
        Tree t1r_max=null, t2r_max=null;
        int N1 = trees1.length;
        int N2 = trees2.length;
        double dist;
        double mcMax = -1;
        
        int t1h_min=Integer.MAX_VALUE;
        int t2h_min=Integer.MAX_VALUE;
        int t1h_max=-1;
        int t2h_max=-1;
        int h;
        
        for (int i = 0; i < trees1.length; i++) {
            for (int j = 0; j < trees2.length; j++) {
                t1r = trees1[i];
                h=TreeUtil2.getNodeDepth(TreeUtils.getNodeByName(t1r, newName));
                if(h<t1h_min){
                    t1h_min=h;
                    t1_min=t1r;
                }
                if(h>t1h_max){
                    t1h_max=h;
                    t1_max=t1r;
                }
                
                t2r = trees2[j];
                h=TreeUtil2.getNodeDepth(TreeUtils.getNodeByName(t2r, newName));
                if(h<t2h_min){
                    t2h_min=h;
                    t2_min=t2r;
                }
                if(h>t2h_max){
                    t2h_max=h;
                    t2_max=t2r;
                }
                
                
                dist = mc.getDistance(t1r, t2r);
                if (dist > mcMax) {
                    mcMax = dist;
                    t1r_max=t1r;
                    t2r_max=t2r;
                }
            }
        }
           // if(t1h_max<t2h_max){
        if(t1h_max>=t2h_max){
            t1_cand=t1_max;
            t2_cand=t2_min;

        }else{
           t1_cand=t1_min;
           t2_cand=t2_max;
        }

        double cand_dist=mc.getDistance(t1_cand,t2_cand);
        double result=mcMax-cand_dist;
        
        return result;
    }


    static Tree[] genAllTrees(Tree baseTree)
  {
        Vector<Tree> newTrees=new Vector<Tree>();
        newTrees.clear();

        int extNodesNum=baseTree.getExternalNodeCount();
        int intNodesNum=baseTree.getInternalNodeCount();

        Node node;
        for(int i=0;i<extNodesNum;i++)
        {
            Tree workTree= baseTree.getCopy();
            node = workTree.getExternalNode(i);
            Tree t0=makeTree(workTree,node);
            newTrees.add(t0);
        }

        for(int i=0;i<intNodesNum;i++)
        {

            Tree workTree= baseTree.getCopy();
            node = workTree.getInternalNode(i);
            if(!node.isRoot()){
                Tree t0=makeTree(workTree,node);
                newTrees.add(t0);
            }
       }

             return newTrees.toArray(new Tree[0]);

  }

    static Tree makeTree(Tree workTree, Node node) {
        int nextLeaf = workTree.getExternalNodeCount() + 1;
        Tree t0=null;

        Node newExtNode = NodeFactory.createNode();
        newExtNode.setIdentifier(new Identifier(Integer.toString(nextLeaf)));

        if (!node.isRoot()) {
            Node parent = node.getParent();
            NodeUtils.removeChild(parent, node);

            Node newIntNode = NodeFactory.createNode();
            parent.addChild(newIntNode);

            newIntNode.addChild(newExtNode);
            newIntNode.addChild(node);

            t0 = new SimpleTree(workTree.getRoot());
        }else{
            //node is the root

            Node newRootNode = NodeFactory.createNode();

            newExtNode.setIdentifier(new Identifier(Integer.toString(nextLeaf)));
            newRootNode.addChild(newExtNode);
            newRootNode.addChild(node);
            t0 = new SimpleTree(newRootNode);

        }

        //workTree=t0;

        /* String tree="";

        StringWriter sWriter=new StringWriter();
        PrintWriter pWriter=new PrintWriter(sWriter);

        TreeUtils.printNH(t0,pWriter,false,false);
        tree=sWriter.toString();
        System.out.print("Crated tree:"+tree+"\n");
         */
        return t0;


    }
}
