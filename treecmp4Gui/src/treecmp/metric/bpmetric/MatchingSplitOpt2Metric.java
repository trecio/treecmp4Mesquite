/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;
/**
 *
 * @author Damian
 */
public class MatchingSplitOpt2Metric extends BaseMetric implements Metric{

    boolean [][]leafT1toIntT2;
    boolean [][]leafT2toIntT1;
    short [][] assigncost;
    NodeInfo[] infoT1;
    NodeInfo[] infoT2;

    public double getDistance(Tree t1, Tree t2) {

        int i, j,level;
        int metric, w,id;
        int extId1;
        int extId2;
        Integer iI;

        long start = System.currentTimeMillis();

        int n=t1.getExternalNodeCount();
        int ni=t1.getInternalNodeCount();
        int size=n-3;

        leafT1toIntT2=new boolean[n][size];
        leafT2toIntT1=new boolean[n][size];
        assigncost = new short[size][size];

        int [] rowsol = new int[size];
        int [] colsol = new int[size];
        int [] u = new int[size];
        int [] v = new int[size];



        int nExt1=t1.getExternalNodeCount();
        int nInt1=t1.getInternalNodeCount();

        int nExt2=t2.getExternalNodeCount();
        int nInt2=t2.getInternalNodeCount();
/*
        for(i=0;i<nExt1;i++){
            System.out.println("Ext1:"+t1.getExternalNode(i).toString()+" num:" +t1.getExternalNode(i).getNumber());
        }
        for(i=0;i<nInt1;i++){
            System.out.println("Int1:" +t1.getInternalNode(i).toString()+" num:" +t1.getInternalNode(i).getNumber());
            if(t1.getInternalNode(i).isRoot()) System.out.println("Root!");
        }

        for(i=0;i<nExt2;i++){
            System.out.println("Ext2:" +t2.getExternalNode(i).toString()+" num:" +t2.getExternalNode(i).getNumber());
        }
        for(i=0;i<nInt2;i++){
            System.out.println("Int2:" +t2.getInternalNode(i).toString()+" num:" +t2.getInternalNode(i).getNumber());
            if(t1.getInternalNode(i).isRoot()) System.out.println("Root!");
        }
*/
        infoT1=calcLevelsAndSizes(t1);
        infoT2=calcLevelsAndSizes(t2);
        //int extNodeT1Level[]=calcLevels(t1);
        //int extNodeT2Level[]=calcLevels(t2);

        //TreeMap<Integer,Vector<Node>> levelT1toExtNode=levelsToMap(extNodeT1Level,t1);
        //TreeMap<Integer,Vector<Node>> levelT2toExtNode=levelsToMap(extNodeT2Level,t2);
        TreeMap<Integer,Vector<Node>> levelT1toExtNode=levelsInfoToMap(infoT1,t1);
        TreeMap<Integer,Vector<Node>> levelT2toExtNode=levelsInfoToMap(infoT2,t2);

        Integer currentLevel=0;

        Vector<Integer> levels=mergeLevels(levelT1toExtNode,levelT2toExtNode);
        for (i=0;i<levels.size();i++){
            currentLevel=levels.get(i);
            calcDistForNodesT1(currentLevel,levelT1toExtNode,levelT2toExtNode,t2);
            calcDistForNodesT2(currentLevel,levelT2toExtNode,levelT1toExtNode,t1);

        }
/*
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                System.out.println(i+ " "+j+":"+assigncost[i][j]);
            }
        }
  */
        teransformCosts(t1,t2,size);
        long lapStart = System.currentTimeMillis();
        metric=LapSolver.lapShort(size, assigncost, rowsol, colsol, u, v);
        long end = System.currentTimeMillis();
        System.out.println("Execution of MS opt2 time was "+(end-start)+" ms.");
        System.out.println("Execution of Lap for MS opt2 time was "+(end-lapStart)+" ms.");
        return metric;

    }

    private NodeInfo [] calcLevelsAndSizes(Tree t){
        
        int n=t.getExternalNodeCount()-3;
        NodeInfo[] nodeInfo=new NodeInfo[n];
        Node curNode=t.getExternalNode(0);
        int ind=0,level,size,childSize0,childSize1,childLevel0,childLevel1;
        Node child0,child1;

        while(!curNode.isRoot()){
            if (!curNode.isLeaf()){
                ind=curNode.getNumber();
                child0=curNode.getChild(0);
                child1=curNode.getChild(1);
                if(child0.isLeaf()){
                    childSize0=1;
                    childLevel0=0;
                }else{
                    childSize0=nodeInfo[child0.getNumber()].size;
                    childLevel0=nodeInfo[child0.getNumber()].level;
                }
                
                if(child1.isLeaf()){
                    childSize1=1;
                    childLevel1=0;
                }else{
                    childSize1=nodeInfo[child1.getNumber()].size;
                    childLevel1=nodeInfo[child1.getNumber()].level;
                }
                level=Math.max(childLevel0, childLevel1)+1;
                size=childSize0+childSize1;      

                nodeInfo[ind]=new NodeInfo(level,size);

            }            
            curNode=NodeUtils.postorderSuccessor(curNode);
       }
        
        return nodeInfo;
    }

 private int[] calcLevels(Tree t)
 {
     
     int n=t.getExternalNodeCount();
     int size=n-3;
     int extNodeTLevel[]=new int [size];
     int level=0;
     int id;
      for(int i=0;i<n;i++){
            Node ext=t.getExternalNode(i).getParent();
            
            level=0;
            while(!ext.isRoot()){
               
                level++;
                id=ext.getNumber();
                if (extNodeTLevel[id]<level)
                    extNodeTLevel[id]=level;
                else
                    break;
                 ext=ext.getParent();
            }
        }
     
     return extNodeTLevel;
 }




 private short calcDist(Node n1,Node n2){

     short d=0,d00,d11,d01,d10;
     boolean n1IsLeaf=n1.isLeaf();
     boolean n2IsLeaf=n2.isLeaf();
     Node child10,child11,child20,child21;

     /* never happen
     if (n1IsLeaf && n2IsLeaf){
         if (n1.getIdentifier().getName().equals(n2.getIdentifier().getName()))
             d=1;
         else
             d=0;
     } else
  */
     if (!n1IsLeaf && !n2IsLeaf){
         child10=n1.getChild(0);
         child20=n2.getChild(0);
         child11=n1.getChild(1);
         child21=n2.getChild(1);

         d00=getDist(child10,child20);
         d11=getDist(child11,child21);
         d01=getDist(child10,child21);
         d10=getDist(child11,child20);
         d= (short) (d00 + d11 + d10 + d01);
         assigncost[n1.getNumber()][n2.getNumber()]=d;
         
     }else if (n1IsLeaf && !n2IsLeaf){
         d00=getDist(n1,n2.getChild(0));
         d01=getDist(n1,n2.getChild(1));
         d= (short) (d00 + d01);
         if (d==1)
             leafT1toIntT2[n1.getNumber()][n2.getNumber()]=true;
         else
             leafT1toIntT2[n1.getNumber()][n2.getNumber()]=false;
     }else{ // (!n1.isLeaf() && n2.isLeaf())
         d00=getDist(n1.getChild(0),n2);
         d01=getDist(n1.getChild(1),n2);
         d= (short) (d00 + d01);
         if (d==1)
             leafT2toIntT1[n2.getNumber()][n1.getNumber()]=true;
         else
             leafT2toIntT1[n2.getNumber()][n1.getNumber()]=false;
                 
     }

     return d;
 }
 
 private short getDist(Node n1,Node n2){
     short d=0;
     boolean n1IsLeaf=n1.isLeaf();
     boolean n2IsLeaf=n2.isLeaf();

     if (n1IsLeaf && n2IsLeaf){
         if (n1.getIdentifier().getName().equals(n2.getIdentifier().getName()))
             d=1;
         else
             d= 0;
     } else if (!n1IsLeaf && !n2IsLeaf){
         d=assigncost[n1.getNumber()][n2.getNumber()];
     } else if (n1IsLeaf && !n2IsLeaf){
         d=0;
         if (leafT1toIntT2[n1.getNumber()][n2.getNumber()])
           d=1;
     } else{ //(!n1.isLeaf() && n2.isLeaf())
         d=0;
         if (leafT2toIntT1[n2.getNumber()][n1.getNumber()])
           d=1;
     }
     
     return d;
 }
 
 
 private TreeMap<Integer,Vector<Node>> levelsToMap(int []extNodeTLevel,Tree t){
     TreeMap<Integer,Vector<Node>> levelTtoExtNode=new TreeMap<Integer,Vector<Node>>();
     int level=0;
     Integer levelI;
     for(int i=0;i<extNodeTLevel.length;i++){
         level=extNodeTLevel[i];
         levelI=Integer.valueOf(level);
         Node node=t.getInternalNode(i);
         if(levelTtoExtNode.containsKey(levelI))
             levelTtoExtNode.get(levelI).add(node);
         else{
             Vector<Node> tsNode=new Vector<Node>();
             tsNode.add(node);
             levelTtoExtNode.put(levelI, tsNode);
         }
     }
     return levelTtoExtNode;
 }

  private TreeMap<Integer,Vector<Node>> levelsInfoToMap(NodeInfo[] intNodeInfo,Tree t){
     TreeMap<Integer,Vector<Node>> levelTtoExtNode=new TreeMap<Integer,Vector<Node>>();
     int level=0;
     Integer levelI;
     for(int i=0;i<intNodeInfo.length;i++){
         level=intNodeInfo[i].level;
         levelI=Integer.valueOf(level);
         Node node=t.getInternalNode(i);
         if(levelTtoExtNode.containsKey(levelI))
             levelTtoExtNode.get(levelI).add(node);
         else{
             Vector<Node> tsNode=new Vector<Node>();
             tsNode.add(node);
             levelTtoExtNode.put(levelI, tsNode);
         }
     }
     return levelTtoExtNode;
 }

private Vector<Integer> mergeLevels( TreeMap<Integer,Vector<Node>> t1,TreeMap<Integer,Vector<Node>> t2)
    {

    Vector<Integer> levels=new Vector<Integer>();

    Iterator<Integer> keys1=t1.keySet().iterator();
    Iterator<Integer> keys2=t2.keySet().iterator();
    
    Integer key1=Integer.MAX_VALUE,key2=Integer.MAX_VALUE,key;
    if (keys1.hasNext())
            key1=keys1.next();
    if (keys2.hasNext())
            key2=keys2.next();

    key=Math.min(key1, key2);

    do{

        if(key<Integer.MAX_VALUE)
            levels.add(key);

        if(key1<key2){
            if (keys1.hasNext())
                key1=keys1.next();
            else
                key1=Integer.MAX_VALUE;
        }else if(key1>key2) {
            if (keys2.hasNext())
                key2=keys2.next();
            else
                key2=Integer.MAX_VALUE;
        }else{
            if (keys1.hasNext())
                key1=keys1.next();
            else
                key1=Integer.MAX_VALUE;

            if (keys2.hasNext())
                key2=keys2.next();
            else
                key2=Integer.MAX_VALUE;
        }
        key=Math.min(key1, key2);
        }while(key!=Integer.MAX_VALUE);

    return levels;
}


void calcDistForNodesT1(Integer currentlevel, TreeMap<Integer,Vector<Node>> lT1, TreeMap<Integer,Vector<Node>> lT2,Tree t2){

    Vector<Node> t1Nodes;
    Vector<Node> t2Nodes;
    t1Nodes=lT1.get(currentlevel);
    Integer key;
    if(t1Nodes==null)
        return;
    for(Node n1:t1Nodes){
        //calcdist toleaves
        for(int i=0;i<t2.getExternalNodeCount();i++){
            Node leafT2=t2.getExternalNode(i);
            calcDist(n1,leafT2);
        }
        
        Iterator<Integer> keys=lT2.keySet().iterator();
        while(keys.hasNext()){
            key=keys.next();
            if(key>currentlevel)
                break;
            t2Nodes=lT2.get(key);
            for(Node n2:t2Nodes){
                calcDist(n1,n2);
            }
        }
     }
        
 }
    
  void calcDistForNodesT2(Integer currentlevel, TreeMap<Integer,Vector<Node>> lT2, TreeMap<Integer,Vector<Node>> lT1,Tree t1){

    Vector<Node> t1Nodes;
    Vector<Node> t2Nodes;
    t2Nodes=lT2.get(currentlevel);
    Integer key;
    if(t2Nodes==null)
        return;
    for(Node n2:t2Nodes){
        //calcdist toleaves
        for(int i=0;i<t1.getExternalNodeCount();i++){
            Node leafT1=t1.getExternalNode(i);
            calcDist(leafT1,n2);
        }
        
        Iterator<Integer> keys=lT1.keySet().iterator();
        while(keys.hasNext()){
            key=keys.next();
            if(key>=currentlevel)
                break;
            t1Nodes=lT1.get(key);
            for(Node n1:t1Nodes){
                calcDist(n1,n2);
            }
        }
     }
        
 }


void teransformCosts(Tree t1,Tree t2,int size){
    
    int n1,n2;
    int N=t1.getExternalNodeCount();
    short newCost=0;
    int temp,cost;
    for(int i=0;i<size;i++){
        for(int j=0;j<size;j++){
            n1=infoT1[i].size;
            n2=infoT2[j].size;
            cost= assigncost[i][j];
            cost=cost<<1;
            temp=n1+n2-cost;
            newCost=(short)Math.min(temp, N-temp);
            assigncost[i][j]=newCost;
        }
    }
}


}


class MapEntyHelper{
    public Map.Entry mapEntry;
    public int tree;            
    MapEntyHelper(Map.Entry me,int tree){
        this.mapEntry=me;
        this.tree=tree;
        
    }
}

class NodeInfo{
    public int level;
    public int size;
    NodeInfo(int level,int size){
        this.level=level;
        this.size=size;

    }
}


