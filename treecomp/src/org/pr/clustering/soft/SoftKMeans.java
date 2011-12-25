package org.pr.clustering.soft;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.pr.clustering.AbstractClusteringAlgorithm;
import org.pr.clustering.Vector;
import org.pr.clustering.util.DoubleUtils;

/**
 * @author Ahmad
 */
public class SoftKMeans extends AbstractClusteringAlgorithm {

	Vector[] patterns;
	int k;
	double m;
	double alpha;
	
	MembershipMatrix mm;
	
	public SoftKMeans(Vector[] patterns, int k, double m, double alpha) {
		this.patterns = patterns;
		this.k = k;
		this.m = m;
		this.alpha = alpha;
	}

	public List<Integer> partition() {
		double Tw = alpha / (double) k;
		
		// [SKM.1] select initial cluster center arbitrary
		mm = new MembershipMatrix(patterns.length, k);
		
		// 2. calculate cluster centers
		List<Vector> newZ = calculateZ();
		
		List<Vector> oldZ;
		
		for (int iterations = 0; ; iterations++) {
			oldZ = newZ;
			
			// i need to calculate distance matrix
			// to be used in the following nested loops to get 
			// xi_zj_distance and xi_zr_distance
			// instead of caculating the same value everytime
			// we need it
			
			double[][] distanceMatrix = calculateDistanceMatrix(newZ); 
			
			// 3. update membership matrix
			for (int i = 0; i < patterns.length; i++) { // loop over patterns
				boolean patternIsCenter = false;
				for (int r = 0; r < k; r++) { // loop over clusters
					// if the patterns is a cluster center
					// set its membership to that cluster to 1
					// and vanish its memberships with the other cluster
					if (DoubleUtils.equal(distanceMatrix[i][r], 0)) {
						patternIsCenter = true;
						mm.matrix[i][r] = 1.0;
						for (int j = 0; j < k; j++) {
							if (j != r)
								mm.matrix[i][r] = 0.0;
						}
						break;
					}
				}
				
				if (! patternIsCenter) {
					for (int j = 0; j < k; j++) { // loop over clusters
//						double lamda = 1.0 / distanceMatrix[i][j];
//						if (lamda <= Tw)
//							mm.matrix[i][j] = 0.0;
//						else {
							double nominator = 1.0 / Math.pow(distanceMatrix[i][j], 1.0 / (m - 1));
							double denominator = 0;
							for (int r = 0; r < k; r++) {
								denominator += 1.0 / Math.pow(distanceMatrix[i][r], 1.0 / (m - 1));
							}
							
							double newWieght = nominator / denominator;
							mm.matrix[i][j] = newWieght < Tw 
								? 0.0
								: newWieght;
//						}
					}
				}
				
				// normalize membership matrix for each pattern
				double weightSum = 0;
				for (int j = 0; j < k; j++) {
					weightSum += mm.matrix[i][j];
				}
				
				for (int j = 0; j < k; j++) {
					mm.matrix[i][j] =  mm.matrix[i][j] / weightSum;
				}
			}
			
			newZ = calculateZ();
			
			if (newZ.equals(oldZ))
				break;
		}
		
		System.out.println(newZ);
		
		// TODO not implemented
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
		sb.append("\t\t\t\tpattern \t\t cluster memberships" + "\n");
		sb.append("-------------------------------------------------------------");
		DecimalFormat membershipFormat = new DecimalFormat("0.0000000000");
		DecimalFormat lineNumberFormat = new DecimalFormat("000");
		
		for (int i = 0; i < patterns.length; i++) { // loop over patterns
			// sb.append("\n" + lineNumberFormat.format(i) + ")\t\t" + patterns[i] + " >> ");
			sb.append("\n" + lineNumberFormat.format(i) + ") ");
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
		SoftKMeans fuzzyCMeans = new SoftKMeans(AbstractClusteringAlgorithm.loadPatterns("C:/Gaussian.in", "\t", true), 2, 2, 0.5);
		fuzzyCMeans.partition();
		System.out.println(fuzzyCMeans.printResults());
	}
	
}
