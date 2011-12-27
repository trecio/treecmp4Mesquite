package treetool.tree;
import java.util.*;
/**Class for representing directed edges in a tree*/
public class Edge {
  protected int id; //the id of this edge
  protected Node from, to; //the nodes connected to this edge
  protected Edge backedge; //the oppositely directed edge of this edge
  private int size;
  
  /**Constructs new edge, connectiong the given nodes
     @param from the node this edge leads from
     @param to the node this edge leads to
  */
  public Edge(Node from, Node to) {
    this.from = from;
    this.to = to;
    size = Integer.MIN_VALUE;
  }

  
  protected void setSize() {
    if (size > 0)
      return;
    if (to instanceof Leaf)
      size = 1;
    else {
      Iterator it = to.getEdges();
      Edge next;
      size = 0;
      while (it.hasNext()) {
	next = (Edge)it.next();
	if (next.to != this.from) { //we don't want this edge's backedge
	  if (next.size <= 0)
	    next.setSize();
	  size += next.size;
	}
      }
    }
  }
 
  /**Calculates the size of the intersection of leaves between the
     rooted trees represented by this directed edge, and the given
     edge. A table is given to utilize dynamic programming. A boolean
     defines how to look up in the table
     @param other the directed edge representing the other tree
     @param sizes the known intersecion sizes
     @param row whether to look up in rows or columns
  */
  public int intSize(Edge other, int[][] sizes, boolean row) {
    int i,j;
    if (row) {
      i = id;
      j = other.id;
    }
    else {
      i = other.id;
      j = id;
    }
    if (sizes[i][j] == Integer.MIN_VALUE) { //compute
      if (to instanceof Leaf) { //no recursion in this tree
	if (other.to instanceof Leaf) //two leaves are easy to compare
	  sizes[i][j] = (to.equals(other.to)) ? 1 : 0;
	else { //recurse on other
	  Iterator otheredges = other.to.getEdges(); //get all outgoing edges from the to-node
	  int sum = 0;
	  Edge next;
	  while(otheredges.hasNext()) {
	    next = (Edge)otheredges.next();
	    if (next.to != other.from) //one of the edges leads back to the from-node
	      sum += next.intSize(this, sizes, !row);
	  }
	  sizes[i][j] = sum;
	}
      }
      else { //recurse on this tree
	Iterator edges = to.getEdges(); //get all outgoing edges from the node this edge points to
	int sum = 0;
	Edge next;
	while(edges.hasNext()) {
	  next = (Edge)edges.next();
	  if (next.to != from) //one of the edges is pointing back to this edge's from-node
	    sum += next.intSize(other, sizes, row);
	}
	sizes[i][j] = sum;
      }    
    }
    return sizes[i][j];
  }

  /**Returns the node this edge points to
     @return the node this edge points to */
  public Node pointsTo() {
    return to;
  }

  /**Returns the node this edge points away from
     @return the node this edge points away from */
  public Node pointsAwayFrom() {
    return from;
  }

  /**Returns the id of this edge
     @return the id
   * */
  public int getId() {
    return id;
  }

  /**Returns the edge pointing the opposite way of this edge
     @return the backedge
     * */
  public Edge getBackEdge() {
    return backedge;
  }

  /**Convenience method for getting the id this edges back-edge
     @return the id
     * */
  public int getBackEdgeId() {
    return backedge.id;
  }

  /**Return the size of the subtree that this edge points to
     measured in leaves*/
  public int getSubtreeSize() {
    return size;
  }


  /**Computes the number of ways to select two elements from a set of
     size n (also known as 'n choose 2')
     @param n the size of the set
     @return n choose 2
  */
  private long choose2(long n) {
    return (n * (n - 1)) / 2;
  }  

  public int compareTo(Object o) {
    return id - ((Edge)o).id;
  }

}
