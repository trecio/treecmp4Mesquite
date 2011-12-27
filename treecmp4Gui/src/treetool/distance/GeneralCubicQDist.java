package treetool.distance;
import treetool.tree.*;
import java.util.*;
/**Class containing method for calculating the quartet distance
   between two unrooted trees in qubic time (O(n^3)).
*/
public class GeneralCubicQDist extends Distance {
  
  /**Calculates the quartet distance between two binary unrooted
     trees. The computation takes quadratic time. It is assumed that
     the trees have the same amount of leaves, and that there is a one
     to one naming of leaves between the trees.
     @param t1 the first tree
     @param t2 the second tree
     @return the quartet distance     
  */
  protected long calcDistance(Tree t1, Tree t2) {
    if (!t1.isInitialized() || !t2.isInitialized())
      throw new RuntimeException("Both trees must be initialized to use this algorithm");

    long sum = 0;
    int[][] iss = t1.calcInterSizes(t2);

    Leaf[] l1 = t1.getLeaves(); //get the sorted arrays of leaves
    Leaf[] l2 = t2.getLeaves();
    Edge[] edges1 = t1.getEdges();
    Edge[] edges2 = t2.getEdges();
    CenterNode[] centers1, centers2;
    LinkedList path1, path2;
    for (int i=0; i<l1.length-2; i++) {   //O(n)
      for (int j=i+1; j<l1.length-1; j++) { //O(n)   total: O(n^2)
	centers1 = new CenterNode[l1.length];
	centers2 = new CenterNode[l1.length];
	path1 = l1[i].getPathTo(l1[j], null); //find the path from l1[i] to l1[j]
	path2 = l2[i].getPathTo(l2[j], null); //find the path from l2[i] to l2[j]
	fillCenterArray(centers1, path1);
	fillCenterArray(centers2, path2);
	//have fun with the centerarrays
	for (int k=j+1; k<centers1.length; k++) { //O(n)   total O(n^3)
	  if(centers1[k] != null) { //Two of the entries are null, since they are on the path.
	    long mrest2size = t2.numLeaves() -             //all
	      edges2[centers2[k].dir_a].getSubtreeSize() -  //-subtree a
	      edges2[centers2[k].dir_b].getSubtreeSize() -  //-subtree b
	      edges2[centers2[k].dir_c].getSubtreeSize();   //-subtree c
	    
	    // |M_rest \cap M'_rest|
	    long restintersect = mrest2size - 
	      ((edges1[centers1[k].dir_a].getSubtreeSize() -   //M_a 
		(iss[centers1[k].dir_a][centers2[k].dir_a] +   //-|M_a \cap M'_a|
		 iss[centers1[k].dir_a][centers2[k].dir_b] +   //-|M_a \cap M'_b|
		 iss[centers1[k].dir_a][centers2[k].dir_c])) + //-|M_a \cap M'_c|
	       (edges1[centers1[k].dir_b].getSubtreeSize() -   //M_b 
		(iss[centers1[k].dir_b][centers2[k].dir_a] +   //-|M_b \cap M'_a|
		 iss[centers1[k].dir_b][centers2[k].dir_b] +   //-|M_b \cap M'_b|
		 iss[centers1[k].dir_b][centers2[k].dir_c])) + //-|M_b \cap M'_c|
	       (edges1[centers1[k].dir_c].getSubtreeSize() -   //M_c 
		(iss[centers1[k].dir_c][centers2[k].dir_a] +   //-|M_c \cap M'_a|
		 iss[centers1[k].dir_c][centers2[k].dir_b] +   //-|M_c \cap M'_b|
		 iss[centers1[k].dir_c][centers2[k].dir_c]))); //-|M_c \cap M'_c|
	       
	      
	    sum += restintersect +
	      //Butterfly quartets
	      (iss[centers1[k].dir_a][centers2[k].dir_a] + // |M_a \cap M'_a|
	       iss[centers1[k].dir_b][centers2[k].dir_b] + // |M_b \cap M'_b|
	       iss[centers1[k].dir_c][centers2[k].dir_c] + // |M_c \cap M'_c|
	       -3);
	  }
	}
      }
    }
    
    return choose4(l1.length) - sum/4;
  }

  /**This method runs in O(#leaves)*/
  private void fillCenterArray(CenterNode[] centers, LinkedList path) {
    Node current = null;
    Edge to = null, from = null, tmpedge;
    Iterator it, leafit, edgeit;
    Leaf tmpleaf;
    LinkedList leaves;
    it = path.iterator();
    if (it.hasNext())
      to = (Edge)it.next();
    while (it.hasNext()) { //for each edge on the path
      from = to;               //This edge is pointing to...
      current = from.pointsTo();  //this node
      to = (Edge)it.next();    //This edge is pointing away from the current node
      edgeit = current.getEdges();
      while (edgeit.hasNext()) { //for all edges going _from_ the current inner node on the path
	tmpedge = (Edge)edgeit.next();
	if (tmpedge != to && tmpedge != from.getBackEdge()) { //if the edge doesn't point to a node on the path
	  leaves = new LinkedList();
	  tmpedge.pointsTo().findLeaves(tmpedge, leaves); //find all leaves reachable from that edge
	  leafit = leaves.iterator();
	  while(leafit.hasNext()) { //for all these leaves
	    tmpleaf = (Leaf)leafit.next();
	    centers[tmpleaf.getId()] = new CenterNode(current, from.getBackEdge().getId(), to.getId(), tmpedge.getId()); //save info
	  }
	}
      }
    }
  }

  

  /**Computes the number of ways to select two elements from a set of
     size n (also known as 'n choose 2')
     @param n the size of the set
     @return n choose 2
  */
  private long choose2(long n) {
    return (n * (n - 1)) / 2;
  }

  /**Computes the number of ways to select four elements from a set of
     size n (also known as 'n choose 4')
     @param n the size of the set
     @return n choose 4
  */
  private long choose4(int n) {
    long longn = (long)n;
    return (longn * (longn - 1) * (longn - 2) * (longn - 3)) / 24;
  }


  private class CenterNode {
    private Node center;
    private int dir_a, dir_b, dir_c;
    private CenterNode(Node center, int diredgeid_a, int diredgeid_b, int diredgeid_c) {
      this.center = center;
      this.dir_a = diredgeid_a;
      this.dir_b = diredgeid_b;
      this.dir_c = diredgeid_c;
    }
  }
  
}

