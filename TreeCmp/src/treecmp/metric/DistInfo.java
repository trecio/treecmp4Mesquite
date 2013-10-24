/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric;

/**
 *
 * @author Damian
 */
public class DistInfo {
    private double dist;
    private int t1LeafNum;
    private int t2LeafNum;
    private int commonLeafNum;

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public int getT1LeafNum() {
        return t1LeafNum;
    }

    public void setT1LeafNum(int t1LeafNum) {
        this.t1LeafNum = t1LeafNum;
    }

    public int getT2LeafNum() {
        return t2LeafNum;
    }

    public void setT2LeafNum(int t2LeafNum) {
        this.t2LeafNum = t2LeafNum;
    }

    public int getCommonLeafNum() {
        return commonLeafNum;
    }

    public void setCommonLeafNum(int commonLeafNum) {
        this.commonLeafNum = commonLeafNum;
    }  
}
