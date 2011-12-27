package treetool.distance;
import treetool.tree.*;
import java.util.*;

public class GeneralN2D2QDist extends Distance {
  /**Calculates the quartet distance between two general unrooted
     trees. The computation takes quadratic time for trees that are
     well resolved but up to quartic time depending on how unresulved the tree is.
     @param t1 the first tree
     @param t2 the second tree
     @return the quartet distance
  */
  protected long calcDistance(Tree gt1, Tree gt2) {
    if (!gt1.isInitialized() || !gt2.isInitialized())
      throw new RuntimeException("Both trees must be initialized to use this algorithm");

    //***********************************************************************
    //*****************************PREPROCESSING*****************************
    //***********************************************************************
    LinkedList newedges1 = new LinkedList();
    LinkedList newedges2 = new LinkedList();
    Edge[] map1 = new Edge[gt1.numEdges()];
    Edge[] map2 = new Edge[gt2.numEdges()];

    Tree tree1 = gt1.makeBinary(newedges1, map1);
    Tree tree2 = gt2.makeBinary(newedges2, map2);
    
    int[][] intersect = tree1.calcInterSizes(tree2); //N^2 time

    //arrays telling whether edges are new
    boolean[] isnew1 = new boolean[tree1.numEdges()]; //Linear time
    boolean[] isnew2 = new boolean[tree2.numEdges()]; //Linear time

    //... All are new...
    Arrays.fill(isnew1, true); //Linear time
    Arrays.fill(isnew2, true); //Linear time

    //... Except the ones mapped to
    for (int i = 0; i < map1.length; i++) //Linear time
      isnew1[map1[i].getId()] = false;
    for (int i = 0; i < map2.length; i++) //Linear time
      isnew2[map2[i].getId()] = false;

    Edge[] edges1 = tree1.getEdges();
    Edge[] edges2 = tree2.getEdges();
    int t1, t2, t1prime, t2prime, t3, t3prime, t4, t4prime;
    Edge[] edge1_orig, edge2_orig, edge1_new, edge2_new, tmp;

    Collection[] tmpcol;

    //***********************************************************************
    //***************************END PREPROCESSING***************************
    //***********************************************************************

    long t1timest2, t1timest2prime, t1t2_choices, t3_choices;
    long shared = 0, diff = 0, bq1 = 0, bq2 = 0;
       
    //Calculate shared and different butterfly quartets O(n^2d^2)
    for (int i=0; i<edges1.length; i++) {                                                        //O(n)
      tmp = otherEdges(edges1[i]);
      if (tmp == null) //We are looking at a leaf, no butterfly quartets can be found here
	continue;
      t1 = tmp[0].getId(); t2 = tmp[1].getId();
      tmpcol = accBackwards(edges1[i], isnew1); //O(d)
      edge1_orig = (Edge[])(tmpcol[0].toArray(new Edge[0])); 
      edge1_new = (Edge[])(tmpcol[1].toArray(new Edge[0])); 
      
      for (int j=0; j<edges2.length; j++) {                                                      //O(n)
	tmp = otherEdges(edges2[j]);
	if (tmp == null) //We are looking at a leaf, no butterfly quartets can be found here
	  continue;
	t1prime = tmp[0].getId(); t2prime = tmp[1].getId();

	tmpcol = accBackwards(edges2[j], isnew2); //O(d)
	edge2_orig = (Edge[])(tmpcol[0].toArray(new Edge[0])); 
	edge2_new = (Edge[])(tmpcol[1].toArray(new Edge[0])); 
      
	//Choose one leaf from each of the two subtrees in front of edges1[i]
	//and edges2[j], i.e. from T1, T2 and T1', T2'
	//calculate |T1\cap T1'|*|T2\cap T2'| + |T1\cap T2'|*|T2\cap T1'|
	t1t2_choices = (intersect[t1][t1prime]*intersect[t2][t2prime]+
			intersect[t1][t2prime]*intersect[t2][t1prime]);//O(1)
	
	for (int in=0; in<edge1_orig.length; in++) {                                             //O(d)
	  t3 = edge1_orig[in].getBackEdge().getId();
	  for (int jn=0; jn<edge2_orig.length; jn++) {                                           //O(d)
	    t3prime = edge2_orig[jn].getBackEdge().getId();
	    //Choose two leaves from behind edge1_orig[in] and edge2_orig[jn]
	    //i.e. from T3 and T3'
	    t3_choices = choose2(intersect[t3][t3prime]); //O(1)
	    shared += t1t2_choices*t3_choices;            //O(1)
	    diff += ((intersect[t1][t3prime]*intersect[t2][t2prime]*intersect[t3][t1prime]+
		      intersect[t1][t3prime]*intersect[t2][t1prime]*intersect[t3][t2prime]+
		      intersect[t1][t2prime]*intersect[t2][t3prime]*intersect[t3][t1prime]+
		      intersect[t1][t1prime]*intersect[t2][t3prime]*intersect[t3][t2prime])*
		     intersect[t3][t3prime]);             //O(1)
	  }
	}
      }
    }
    

    //Calculate BQ1 O(nd)
    for (int i=0; i<edges1.length; i++) {                                              //O(n)
      tmp = otherEdges(edges1[i]);
      if (tmp == null) //We are looking at a leaf, no butterfly quartets can be found here
	continue;
      t1timest2 = tmp[0].getSubtreeSize() * tmp[1].getSubtreeSize();
      tmp = (Edge[])(accBackwards(edges1[i], isnew1)[0].toArray(new Edge[0]));//O(d)
      for (int in=0; in<tmp.length; in++)                                              //O(d)
	bq1 += t1timest2 * choose2(tmp[in].getBackEdge().getSubtreeSize());
    }

    //Calculate BQ2 O(nd)
    for (int i=0; i<edges2.length; i++) {                                              //O(n)
      tmp = otherEdges(edges2[i]);
      if (tmp == null) //We are looking at a leaf, no butterfly quartets can be found here
	continue;
      t1timest2 = tmp[0].getSubtreeSize() * tmp[1].getSubtreeSize();
      tmp = (Edge[])(accBackwards(edges2[i], isnew2)[0].toArray(new Edge[0]));//O(d)
      for (int in=0; in<tmp.length; in++)                                              //O(d)
	bq2 += t1timest2 * choose2(tmp[in].getBackEdge().getSubtreeSize());
    }
        
    return bq1 / 2 + bq2 / 2 - shared - diff / 4;
  }

  //Accumulates all old edges pointing to the given edge, using only paths along new
  //edges. Also accumulates all new edges pointing away from the given edge, using only
  //paths along new edges.
  //Runs in time O(d) where d is the degree of the node the given edge points to
  private Collection[] accBackwards(Edge e, boolean[] isnew) {
    LinkedList oldedges = new LinkedList();
    LinkedList newedges = new LinkedList();
    if (!isnew[e.getId()]) { //e is an old edge
      oldedges.add(e);
      return new Collection[] {oldedges, newedges};
    }
    //e is a new edge
    newedges.add(e.getBackEdge());
    Edge[] others = otherEdges(e.getBackEdge());

    Collection[] tmp = accBackwards(others[0].getBackEdge(), isnew);
    oldedges.addAll(tmp[0]);
    newedges.addAll(tmp[1]);

    tmp = accBackwards(others[1].getBackEdge(), isnew);
    oldedges.addAll(tmp[0]);
    newedges.addAll(tmp[1]);
    
    return new Collection[] {oldedges, newedges};
  }

  /**Convenience method, returns the two outgoing from the node the
     edge points to that does not lead to where the edge comes from*/
  private Edge[] otherEdges(Edge e) {
    try {
      Iterator it = e.pointsTo().getEdges();
      Edge[] out = {(Edge)it.next(), (Edge)it.next(), (Edge)it.next()};
      if (e.pointsAwayFrom() == out[0].pointsTo())
	return new Edge[] {out[1], out[2]};
      else if (e.pointsAwayFrom() == out[1].pointsTo())
	return new Edge[] {out[0], out[2]};
      return new Edge[] {out[0], out[1]};
    }
    catch (java.util.NoSuchElementException nsee) {
      return null; //If there are not three edges, we are looking at a leaf
      //We allow no nodes with two edges.
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
}

