package treetool.distance;
import treetool.tree.*;
import java.util.*;

public class GeneralN2DQDist extends Distance {
  /**Calculates the quartet distance between two general unrooted
     trees. The computation takes O(n^2d) time for trees that are
     well resolved but up to quartic time depending on how unresulved the tree is.
     @param t1 the first tree
     @param t2 the second tree
     @return the quartet distance
  */
  protected long calcDistance(Tree gt1, Tree gt2) {
    if (!gt1.isInitialized() || !gt2.isInitialized())
      throw new RuntimeException("Both trees must be initialized to use this algorithm");

    int[][] intersect = gt1.calcInterSizes(gt2);
    
    long shared = 0, diff = 0;
    InnerNode[] inodes1 = gt1.getInnerNodes();
    InnerNode[] inodes2 = gt2.getInnerNodes();

    for (int i = 0; i < inodes1.length; i++) { //O(#inner nodes in t1)
      Edge[] outedges1 = inodes1[i].getEdgesAsArray(); 
      
      for (int j = 0; j < inodes2.length; j++) { //O(#inner nodes in t2)
	Edge[] outedges2 = inodes2[j].getEdgesAsArray(); 

	long[] c2_in_t1_allout_t2 = new long[inodes1[i].getNumEdges()];
	long[] c2_in_t2_allout_t1 = new long[inodes2[j].getNumEdges()];

	long[] c2_out_t1_allout_t2 = new long[inodes1[i].getNumEdges()];
	long[] c2_out_t2_allout_t1 = new long[inodes2[j].getNumEdges()];
	
	long c2_allout_t1_allout_t2 = 0;

	
	long[] fixed_t1_allout_t2 = new long[inodes1[i].getNumEdges()];
	long[] fixed_t2_allout_t1 = new long[inodes2[j].getNumEdges()];
	
	//Preprocess edges leading from the pair of inner nodes
	for (int pos1 = 0; pos1 < outedges1.length; pos1++) { //O(degree of current node in t1)
	  for (int pos2 = 0; pos2 < outedges2.length; pos2++) { //O(degree of current node in t2)
	    long out_i_out_j = choose2(intersect[outedges1[pos1].getId()][outedges2[pos2].getId()]);
	    long in_i_out_j = choose2(intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()]);
	    long out_i_in_j = choose2(intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()]);

	    c2_in_t1_allout_t2[pos1] += in_i_out_j;
	    c2_in_t2_allout_t1[pos2] += out_i_in_j;

	    c2_out_t1_allout_t2[pos1] += out_i_out_j;
	    c2_out_t2_allout_t1[pos2] += out_i_out_j;

	    c2_allout_t1_allout_t2 += out_i_out_j;

	    fixed_t1_allout_t2[pos1] +=
	      intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()] *
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getId()];

	    fixed_t2_allout_t1[pos2] +=
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()] *
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getId()];
	  }	
	}

	//More preprocessing, for diff butterflys
	long[][] diff_innersums = new long[inodes1[i].getNumEdges()][inodes1[i].getNumEdges()];

	for (int pos1 = 0; pos1 < outedges1.length; pos1++) { //O(degree of current node in t1)
	  for (int row = 0; row < outedges1.length; row++) { //O(degree of current node in t1)
	    for (int col = 0; col < outedges2.length; col++) { //O(degree of current node in t2)
	      if (pos1 != row) {
		diff_innersums[pos1][row] +=
		  intersect[outedges1[row].getId()][outedges2[col].getId()] *
		  intersect[outedges1[pos1].getId()][outedges2[col].getId()];
	      }
	    }
	  }
	}
	
	//Now do the actual processing of edges leading to the nodes
	for (int pos1 = 0; pos1 < outedges1.length; pos1++) { //O(degree of current node in t1)
	  for (int pos2 = 0; pos2 < outedges2.length; pos2++) { //O(degree of current node in t2)
	    shared +=
	      choose2(intersect[outedges1[pos1].getId()][outedges2[pos2].getId()]) *
	      (choose2(intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getBackEdgeId()]) -
	       (c2_in_t2_allout_t1[pos2] - choose2(intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()])) -
	       (c2_in_t1_allout_t2[pos1] - choose2(intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()])) +
	       c2_allout_t1_allout_t2 - c2_out_t1_allout_t2[pos1] - c2_out_t2_allout_t1[pos2] +
	       choose2(intersect[outedges1[pos1].getId()][outedges2[pos2].getId()]));

	    diff +=
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getId()] *
	      (intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getBackEdgeId()] *
	      intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()] *
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()] -
	      intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()] *
	      (fixed_t1_allout_t2[pos1] -
	       intersect[outedges1[pos1].getId()][outedges2[pos2].getId()] *
	       intersect[outedges1[pos1].getBackEdgeId()][outedges2[pos2].getId()]) -	      
	      intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()] *
	      (fixed_t2_allout_t1[pos2] -
	       intersect[outedges1[pos1].getId()][outedges2[pos2].getBackEdgeId()] *
	       intersect[outedges1[pos1].getId()][outedges2[pos2].getId()]));	      

	    //Add diff butterflys that was deducted twice
	    for (int pos3 = 0; pos3 < outedges1.length; pos3++) { //O(degree of current node in t1) {
	      if (pos1 != pos3) {
		diff +=
		  intersect[outedges1[pos1].getId()][outedges2[pos2].getId()] *
		  intersect[outedges1[pos3].getId()][outedges2[pos2].getId()] *
		  (diff_innersums[pos1][pos3] -
		   intersect[outedges1[pos3].getId()][outedges2[pos2].getId()] *
		   intersect[outedges1[pos1].getId()][outedges2[pos2].getId()]);
	      }
	    }
	  }
	}
      }
    }
    
    //All shared quartets are counted twice - divide with 2 to get actual number
    shared /= 2;

    //All different quartets are counted four times - divide by 4 to get actual number
    diff /= 4;
    
    //Calculate BQs O(nd)
    long bq1 = calcBQ(gt1);
    long bq2 = calcBQ(gt2);

    //Use the found values to calculate and return the qdist
    return bq1 + bq2 - 2 * shared - diff;
  }

  /**Computes the number of ways to select two elements from a set of
     size n (also known as 'n choose 2')
     @param n the size of the set
     @return n choose 2
  */
  private long choose2(long n) {
    return (n * (n - 1)) / 2;
  }

  /**Convenience method, returns the two outgoing from the node the
     edge points to that does not lead to where the edge comes from*/
  private Edge[] otherEdges(Edge e) {
    Iterator it = e.pointsTo().getEdges();
    LinkedList ll = new LinkedList();
    while (it.hasNext()) {
      Edge next = (Edge)it.next();
      if (e.getBackEdge() != next)
	ll.add(next);
    }
    if (ll.size() == 0)
      return null; //we are looking at a leaf
    return (Edge[])ll.toArray(new Edge[0]);
  }
  
  /**Calculates the number of butterfly quartets in the given tree
     @param t the tree
     @return the number of butterflys in the tree
  */
  private long calcBQ(Tree t) {
    Edge[] edges = t.getEdges();
    long res = 0;
    for (int i = 0; i < edges.length; i++) {
      Edge[] other = otherEdges(edges[i]);
      if (other == null)
	continue;
      long sum = 0;
      for (int j = 0; j < other.length; j++)
	sum += choose2(other[j].getSubtreeSize());
      res +=
	choose2(edges[i].getBackEdge().getSubtreeSize()) *
	(choose2(edges[i].getSubtreeSize()) - sum);
    }
    return res / 2;
  }
}
