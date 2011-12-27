package treetool.distance;
public class DistResult {
  private long qdist, qsim, q1, q2, starbf, bfstar;

  /**Construcs an object representing the results of a quartet distance
   * calculation between two trees T1 and T2.
   * @param qdist the quartet distance between prune(T1) and prune(T2),
   * where prune removes all leaves T1 and T2 do not have in common.
   * @param dsim the quartets the two trees have in common (topology must
   * be the same).
   * @param q1 is the number of quartets that exist in T1 but not in T2
   * due to leaves present in T1 that are not in T2.
   * @param q2 vice versa.
   */
  

  /**Convenience constructor.*/
  public DistResult(long qdist, long qsim, long q1, long q2) {
    this.qdist = qdist;
    this.qsim = qsim;
    this.q1 = q1;
    this.q2 = q2;
  }

  
  public String toString() {
    return ""+qdist;
  }
  public void print() {
    String tmp = "";
    tmp+="Quartet distance between trees T1 and T2:                               "+(qdist+q1+q2)+"\n";
    tmp+="Number of quartets with the same topology in both input trees:          "+qsim+"\n";
    tmp+="Number of quartets present in both trees, but  with different topology: "+qdist+"\n";
    tmp+="Number of quartets present in T1 but not in T2:                         "+q1+"\n";
    tmp+="Number of quartets present in T2 but not in T1:                         "+q2+"\n";
    System.out.print(tmp);
  }
  public long qdist() {
    return qdist;
  }

  public long qsim() {
    return qsim;
  }

  public long q1() {
    return q1;
  }

  public long q2() {
    return q2;
  }

  public boolean equals(Object o) {
    if (o instanceof DistResult) {
      DistResult other = (DistResult)o;
      return (other.qdist == qdist   &&
	      other.qsim  == qsim    &&
	      other.q1    == q1      &&
	      other.q2    == q2);
    }
    return false;
  }
}
