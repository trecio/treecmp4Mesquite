/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import java.util.Collections;
import java.util.Vector;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;
import pal.tree.TreeManipulator;
import pal.tree.TreeUtils;
import treecmp.ResultWriter;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class MsMcCompareMetric extends BaseMetric implements Metric {
 public double getDistance(Tree t1, Tree t2) {
        //  long start = System.currentTimeMillis();
        int i, j;
        int N1 = t1.getExternalNodeCount();
        int N2 = t2.getExternalNodeCount();
        //NodeUtils
        Node n1,n2;
        Tree t1_rooted=null;
        Tree t2_rooted=null;
        Tree t1_temp=null;
        Tree t2_temp=null;
        Metric m=new MatchingClusterOptMetric();
        double dist_min=Double.POSITIVE_INFINITY;
        double dist;
        String minDistNode="";
        for(i=0;i<N1;i++)
        {
            n1=t1.getExternalNode(i);
            n2=TreeUtils.getNodeByName(t2, n1.getIdentifier().getName());
            t1_rooted=createTree(t1,n1);
            t2_rooted=createTree(t2,n2);
            dist=m.getDistance(t1_rooted, t2_rooted);
            if(dist<dist_min){
                dist_min=dist;
                t1_temp=t1_rooted;
                t2_temp=t2_rooted;
                //minDistNode=n1.getIdentifier().getName();
            }
        }
       // System.out.println("Min dist node: "+minDistNode);
        MatchingSplitOptMetric ms=new MatchingSplitOptMetric();
        ms.setCalcEdgeCosts(true);
        ms.getDistance(t1, t2);
        Vector<Integer> msEdgeCost=ms.getEdgeCosts();

        MatchingClusterOptMetric mc=new MatchingClusterOptMetric();
        mc.setCalcEdgeCosts(true);
        mc.getDistance(t1_temp, t2_temp);
        Vector<Integer> mcEdgeCost=mc.getEdgeCosts();

        Collections.sort(msEdgeCost);
        Collections.sort(mcEdgeCost);

        int N=msEdgeCost.size();
        
       /* ResultWriter out= new ResultWriter();
        out.setText("MS edges\tMC edges");
        out.write();
        for(i=0;i<N;i++){
           int msEdge=msEdgeCost.get(i);
           int mcEdge=mcEdgeCost.get(i);
           out.setText(msEdge+"\t"+mcEdge);
           out.write();
       }
        */
        return dist_min;

    }

    private Tree createTree(Tree t, Node n) {
        Tree tr = t.getCopy();

        TreeManipulator tm = new TreeManipulator(tr);
        Node nr = TreeUtils.getNodeByName(tr, n.getIdentifier().getName());
        //System.out.println(tr.toString());
        Tree tr1 = tm.getTreeRootedAbove(nr);
        Node r1 = tr1.getRoot();
        int ch = r1.getChildCount();
        //Node root = null;

        for (int i = 0; i < ch; i++) {
            if (r1.getChild(i).isLeaf()) {
                r1.removeChild(i);
                break;
            }
        }
        Node child = r1.getChild(0);
        NodeUtils.removeBranch(child);
        tr1.createNodeList();

        // System.out.println(tr1.toString());
        return tr1;

    }
}
