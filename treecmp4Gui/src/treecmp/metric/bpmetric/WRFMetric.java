/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.metric.bpmetric;

import java.util.BitSet;
import java.util.Vector;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.SplitSystem;
import pal.tree.SplitUtils;
import pal.tree.Tree;
import treecmp.common.LapSolver;
import treecmp.metric.BaseMetric;
import treecmp.metric.Metric;

/**
 * 
 * @author Tomasz Tretkowski
 */
public class WRFMetric extends BaseMetric implements Metric {

	private Vector<Double> edgeCosts;
	private boolean calcEdgeCosts;

	public boolean isCalcEdgeCosts() {
		return calcEdgeCosts;
	}

	public Vector<Double> getEdgeCosts() {
		return edgeCosts;
	}

	public void setCalcEdgeCosts(boolean calcEdgeCosts) {
		this.calcEdgeCosts = calcEdgeCosts;
	}

	public WRFMetric() {
		super();
		calcEdgeCosts = false;
		edgeCosts = new Vector<Double>();
	}

	public void clearCalcEdgeCosts() {
		edgeCosts.clear();
	}

	public double getDistance(Tree t1, Tree t2) {

		int i, j;
		double metric;

		SplitSystem s1 = SplitUtils.getSplits(t1);
		IdGroup idGroup = s1.getIdGroup();
		SplitSystem s2 = SplitUtils.getSplits(idGroup, t2);

		BitSet[] bs1 = SplitDist.SplitSystem2BitSetArray(s1);
		BitSet[] bs2 = SplitDist.SplitSystem2BitSetArray(s2);
		int size = s1.getSplitCount();

		double[][] assigncost = new double[size][size];

		int[] rowsol = new int[size];
		int[] colsol = new int[size];
		double[] u = new double[size];
		double[] v = new double[size];

		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				// w = SplitDist.getDist1Int(s1.getSplit(i), s2.getSplit(j));
				// w=SplitDist.getDist1Bit(bs1[i], bs2[j],n);
				assigncost[i][j] = getWeight(bs1[i], bs2[j], 
						getBranchLength(t1.getInternalNode(i)), 
						getBranchLength(t2.getInternalNode(j)));
			}
		}

		// long end_matrix = System.currentTimeMillis();

		// System.out.println("Execution of matrix calculation time was "+(end_matrix-start)+" ms.");

		// String split1=s1.toString();
		// String split2=s2.toString();
		//
		// System.out.println("Split1:" +s1+" Split2:"+s2 +"" +"\n");

		metric = LapSolver.lap(size, assigncost, rowsol, colsol, u, v);
		// calc edge Costs
		if (calcEdgeCosts) {
			for (i = 0; i < size; i++) {
				j = rowsol[i];
				edgeCosts.add(assigncost[i][j]);
			}
		}
		
		for (i=0; i<t2.getExternalNodeCount(); i++) {
			Node t2externalNode = t2.getExternalNode(i);
			int t1externalNodeId = idGroup.whichIdNumber(t2externalNode.getIdentifier().getName());
			Node t1externalNode = t1.getExternalNode(t1externalNodeId);
			metric += Math.abs(getBranchLength(t1externalNode) - getBranchLength(t2externalNode));
		}

		// long end = System.currentTimeMillis();
		// System.out.println("Execution of MS time was "+(end-start)+" ms.");
		// System.out.println("Execution of Lap for MS time was "+(end-lapStart)+" ms.");
		return metric;
	}
	
	private double getBranchLength(Node node) {
		double length = node.getBranchLength();
		if (length > Double.NEGATIVE_INFINITY)
			return length;
		return 1;
	}

	private double getWeight(BitSet bs1, BitSet bs2, double edgeLen1,
			double edgeLen2) {		
		BitSet bs1Copy = (BitSet) bs1.clone();
		bs1Copy.xor(bs2);
		if (bs1Copy.cardinality() == 0)
			return Math.abs(edgeLen1 - edgeLen2);
		else
			return edgeLen1 + edgeLen2;
	}
}
