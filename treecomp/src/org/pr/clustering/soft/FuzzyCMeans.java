package org.pr.clustering.soft;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.pr.clustering.AbstractClusteringAlgorithm;
import org.pr.clustering.Vector;

/**
 * @author Ahmad
 */
public class FuzzyCMeans extends AbstractClusteringAlgorithm {

	Vector[] patterns;
	int k;
	double m;
	
	MembershipMatrix mm;
	
	public FuzzyCMeans(Vector[] patterns, int k, double m) {
		this.patterns = patterns;
		this.k = k;
		this.m = m;
	}

	public List<Integer> partition() {
		// 1. choose an arbitrary membership matrix
		mm = new MembershipMatrix(patterns.length, k);
		
		// 2. calculate cluster centers
		List<Vector> newZ = calculateZ();
		
		List<Vector> oldZ;
		
		double exponent = 2.0 / (m - 1); 
		for (int m = 0; ; m++) {
			oldZ = newZ;
			
			// i need to calculate distance matrix
			// to be used in the following nested loops to get 
			// xi_zj_distance and xi_zr_distance
			// instead of caculating the same value everytime
			// we need it
			
			double[][] distanceMatrix = calculateDistanceMatrix(newZ); 
			
			// 3. update membership matrix
			for (int i = 0; i < patterns.length; i++) { // loop over patterns
				for (int j = 0; j < k; j++) { // loop over clusters
					double denominator = 0;
					double xi_zj_distance = distanceMatrix[i][j];
					for (int r = 0; r < k; r++) {
						double xi_zr_distance = distanceMatrix[i][r];
						double ratio = Math.pow(xi_zj_distance / xi_zr_distance, exponent);
						denominator += ratio;
					}
					
					mm.matrix[i][j] = 1.0 / denominator;
				}
			}
			
			newZ = calculateZ();
			
			if (newZ.equals(oldZ))
				break;
		}
		
		for (Vector vector : newZ) {
			System.out.println(vector);	
		}
		
		// TODO not implmented
		return null;
	}
	
	/**
	 * calculates cluster centers with current clustering configurations.
	 */
	protected List<Vector> calculateZ() {
		List<Vector> C = new ArrayList<Vector>();
		for (int j = 0; j < k; j++) {
			double[] values = new double[this.patterns[0].getDimensionCount()];
			// we need to iteration to get each dimension
			for (int dimIndex = 0; dimIndex < values.length; dimIndex++) {
				double nominator = 0;
				for (int i = 0; i < patterns.length; i++) {
					nominator += Math.pow(mm.matrix[i][j], m) * patterns[i].getDimension(dimIndex); 
				}
				
				double denominator = 0;
				for (int i = 0; i < patterns.length; i++) {
					denominator += Math.pow(mm.matrix[i][j], m);
				}
				
				values[dimIndex] = nominator / denominator;
			}
			C.add(new Vector(values));
		}
		
		return C;
	}
	
	public String printResults() {
		StringBuilder sb = new StringBuilder("");
		sb.append("\tcluster memberships" + "\n");
		sb.append("-------------------------------------------------------------");
		DecimalFormat membershipFormat = new DecimalFormat("0.0000000000");
		DecimalFormat lineNumberFormat = new DecimalFormat("000");
		
		for (int i = 0; i < patterns.length; i++) { // loop over patterns
			// sb.append("\n" + lineNumberFormat.format(i) + ")\t\t" + patterns[i] + " >> ");
			sb.append("\n" + lineNumberFormat.format(i) + ")   ");
			for (int j = 0; j < k; j++) { // loop over clusters
				sb.append(membershipFormat.format(mm.matrix[i][j]));
				if (j < k - 1)
					sb.append("    ");
			}
		}
		
		return sb.toString();
	}

	private double[][] calculateDistanceMatrix(List<Vector> clusterCenters) {
		double[][] distanceMatrix = new double[patterns.length][];
		for (int i = 0; i < distanceMatrix.length; i++) {
			distanceMatrix[i] = new double[k];
			for (int j = 0; j < k; j++) {
				distanceMatrix[i][j] = Vector.euclideanDistance(patterns[i], clusterCenters.get(j));
			}
		}	
		
		return distanceMatrix;
	}
	
	public static void main(String[] args) {
		FuzzyCMeans fuzzyCMeans = new FuzzyCMeans(AbstractClusteringAlgorithm.loadPatterns("C:/Gaussian.in", "\t", true), 2, 2);
		fuzzyCMeans.partition();
		System.out.println(fuzzyCMeans.printResults());
	}
	
}
