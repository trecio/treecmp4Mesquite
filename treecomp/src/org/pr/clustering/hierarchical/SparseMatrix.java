package org.pr.clustering.hierarchical;

import java.util.ArrayList;
import java.util.List;

import mesquite.treecomp.TreeSetVisualizationV2.DiffMatrix;

import org.pr.clustering.Vector;

/**
 * @author Ahmad
 */
public class SparseMatrix {

	public static void main(String[] args) {
		Vector v0 = new Vector(0, 0);
		Vector v1 = new Vector(1, 1);
		Vector v2 = new Vector(5, 5);
		Vector v3 = new Vector(7, 7);
		Vector v4 = new Vector(20, 20);
		Vector v5 = new Vector(30, 30);

		List<Vector> patternList = new ArrayList<Vector>();
		patternList.add(v0);
		patternList.add(v1);
		patternList.add(v2);
		patternList.add(v3);
		patternList.add(v4);
		patternList.add(v5);
		
		SparseMatrix sm = new SparseMatrix(patternList.toArray(new Vector[patternList.size()]), LinkageCriterion.SINGLE);
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
		System.out.println(sm.getClosestPair());
	}
	
	LinkageCriterion linkageCriterion;
	
	private List<List<Double>> rows;
	
	private DistanceFactorCalculator distanceFactorCalculator; 
	
	public final double average;
	
	public SparseMatrix(DiffMatrix values, LinkageCriterion linkageCriterion) {
		this.linkageCriterion = linkageCriterion;
		distanceFactorCalculator = DistanceFactorCalculator.Factory.create(linkageCriterion);
		
		int counter = 0;
		double sum = 0;
		
		rows = new ArrayList<List<Double>>();
		for (int row=0; row<values.getNumberOfItems(); row++) {
			List<Double> currentRow = new ArrayList<Double>();
			rows.add(currentRow);
			
			for (int col = 0; col < row; col++) {
				double distance = values.getElement(row, col);
				sum += distance;
				counter++;
				currentRow.add(distance);
			}
		}
		
		average = sum / counter;
	}
		
	public SparseMatrix(Vector[] patterns, LinkageCriterion linkageCriterion) {
		this.linkageCriterion = linkageCriterion;
		distanceFactorCalculator = DistanceFactorCalculator.Factory.create(linkageCriterion);
		
		rows = new ArrayList<List<Double>>();
		for (int i = 0; i < patterns.length; i++) {
			rows.add(new ArrayList<Double>());
		}
		
		int counter = 0;
		double sum = 0;
		for (int row = 0; row < patterns.length; row++) {
			for (int col = 0; col < row; col++) {
				double distance = Vector.euclideanDistance(patterns[row], patterns[col]);
				sum += distance;
				counter++;
				rows.get(row).add(distance);
			}
		}
		
		average = sum / (double)counter;
	}
	
	private double getCell(int row, int column) {
		if (row >= column) 
			return rows.get(row).get(column);
		else 
			return rows.get(column).get(row);
	}
	
	private void setCell(int row, int column, double value) {
		if (row >= column) 
			rows.get(row).set(column, value);
		else 
			rows.get(column).set(row, value);
	}
	
	public void merge(int row, int column) {
		int min = Math.min(row, column);
		int max = Math.max(row, column);
		for (int i = 0; i < rows.size(); i++) {
			if (i != row && i != column) {
				double alphaR = distanceFactorCalculator.getAlphaR(row, column, i);
				double alphaS = distanceFactorCalculator.getAlphaS(row, column, i);
				double beta = distanceFactorCalculator.getBeta(row, column, i);
				double gamma = distanceFactorCalculator.getGamma(row, column, i);
				double distance_k_rs 
					= alphaR * getCell(row, i) 
					+ alphaS * getCell(column, i) 
					+ beta * getCell(row, column) 
					+ gamma * Math.abs(getCell(row, i) - getCell(column, i)); 
				setCell(min, i, distance_k_rs);
			}
		}
		rows.remove(max);
		for (int i = max; i < rows.size(); i++) {
			rows.get(i).remove(max);
		}
		// correct the indexes of the sorted distances
	}
	
	public DistanceInfo getClosestPair() {
		if (rows.size() == 1)
			return null;
		
		double min = Double.MAX_VALUE;
		int row = -1;
		int column = -1;
		for (int i = 0; i < rows.size(); i++) {
			for (int j = 0; j < i; j++) {
				if (rows.get(i).get(j) < min) {
					min = rows.get(i).get(j);
					row = i;
					column = j;
				}
			}
		}
		
		return new DistanceInfo(min, row, column);
	}
	
	public int size() {
		return rows.size();
	}
	
	public class DistanceInfo implements Comparable<DistanceInfo> {
		public final double distance;
		public final int row; 
		public final int column;
		
		DistanceInfo (double distance, int row, int column) {
			this.distance = distance;
			this.row = Math.min(row, column);
			this.column = Math.max(row, column);
		}

		public int compareTo(DistanceInfo o) {
			return Double.compare(distance, o.distance);
		}
		
		@Override
		public String toString() {
			return "<" + row + "," + column + "> = " + distance;
		}
		
	}
	
	private interface DistanceFactorCalculator {
		double getAlphaR(int nr, int ns, int nk);
		double getAlphaS(int nr, int ns, int nk);
		double getBeta(int nr, int ns, int nk);
		double getGamma(int nr, int ns, int nk);
		
		static class Factory {
			static DistanceFactorCalculator create(LinkageCriterion linkageCriterion) {
				switch (linkageCriterion) {
				case SINGLE:
					return new SingleLinkFactorCalculator();
				case COMPLETE:
					return new CompleteLinkFactorCalculator();
				case UPGMA:
					return new UPGMAFactorCalculator();
				case WPGMA:
					return new WPGMAFactorCalculator();
				case UPGMC:
					return new UPGMCFactorCalculator();
				case WPGMC:
					return new WPGMCFactorCalculator();
				case Ward:
					return new WardFactorCalculator();
				default:
					return new SingleLinkFactorCalculator();
				}
			}
		}
	}
	
	private static class SingleLinkFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getBeta(int nr, int ns, int nk) {
			return 0.0;
		}

		public double getGamma(int nr, int ns, int nk) {
			return -0.5;
		}
		
	}
	
	private static class CompleteLinkFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getBeta(int nr, int ns, int nk) {
			return 0.0;
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0.5;
		}
		
	}

	private static class UPGMAFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return (double)nr / (double)(nr + ns);
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return (double)ns / (double)(nr + ns);
		}

		public double getBeta(int nr, int ns, int nk) {
			return 0.0;
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0.0;
		}
		
	}
	
	private static class WPGMAFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getBeta(int nr, int ns, int nk) {
			return 0.0;
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0.0;
		}
		
	}
	
	private static class UPGMCFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return (double)nr / (double)(nr + ns);
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return (double)ns / (double)(nr + ns);
		}

		public double getBeta(int nr, int ns, int nk) {
			return (double)(-1 * nr * ns) / (double)((nr + ns) * (nr + ns));
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0;
		}
		
	}
	
	private static class WPGMCFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return 0.5;
		}

		public double getBeta(int nr, int ns, int nk) {
			return -0.25;
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0.0;
		}
		
	}
	
	private static class WardFactorCalculator implements DistanceFactorCalculator {

		public double getAlphaR(int nr, int ns, int nk) {
			return (double)(nr + nk) / (double)(nr + ns + nk);
		}

		public double getAlphaS(int nr, int ns, int nk) {
			return (double)(ns + nk) / (double)(nr + ns + nk);
		}

		public double getBeta(int nr, int ns, int nk) {
			return (double)(-1 * nk) / (double)(nr + ns + nk);
		}

		public double getGamma(int nr, int ns, int nk) {
			return 0.0;
		}
		
	}
	
}
