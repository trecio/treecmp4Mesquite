package org.pr.clustering;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;


/**
 * @author Ahmad
 */
public class Vector implements Cloneable {

	private double EPSILON = 0.000000001;
	
	public double[] values;
	
	private String name = "un-named";
	
	public Vector(double... values) {
		this.values = values;
	}
	
	public Vector(String name, double... values) {
		this.name = name;
		this.values = values;
	}
	
	/**
	 * calculates the euclidean distance between two vectors
	 */
	public static double euclideanDistance(Vector first, Vector second) {
		if (first.values.length != second.values.length)
			throw new IllegalArgumentException();
		
		double sumOfSquares = 0;
		for (int i = 0; i < first.values.length; i++) {
			sumOfSquares 
				+= (first.values[i] - second.values[i]) * (first.values[i] - second.values[i]);  
		}
		
		return Math.sqrt(sumOfSquares);
	}
	
	@Override
	public boolean equals(Object obj) {
		Vector other = (Vector) obj;
		for (int i = 0; i < values.length; i++) {
			if (Math.abs((values[i] - other.values[i])) > EPSILON)
				return  false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		DecimalFormat format = new DecimalFormat("000.000");
		
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < values.length; i++) {
			sb.append(format.format(values[i]));
			if (i < values.length - 1)
				sb.append(", ");
		}
		
		
		return sb.append("]").toString();
	}
	
	public int getNearestPointIndex(List<Vector> points) {
		if (points == null || points.size() == 0)
			throw new IllegalArgumentException();
		
		int nearestVectorIndex = 0;
		double minDistance = euclideanDistance(this, points.get(0));
		for (int i = 1; i < points.size(); i++) {
			double distance = euclideanDistance(this, points.get(i));
			if (distance < minDistance) {
				nearestVectorIndex = i;
				minDistance = distance;
			}
		}
		
		return nearestVectorIndex;
	}
	
	public static void main(String[] args) { 
		Vector first = new Vector(1, 2, 3, 4, 5);
		Vector second = new Vector(6, 7, 8, 9, 10);
		
		System.out.println(Vector.euclideanDistance(first, second));
		System.out.println(Vector.calculateCenter(first, second));
	}
	
	public static Vector calculateCenter(Vector... vectors) {
		if (vectors == null || vectors.length == 0)
			throw new IllegalArgumentException();
		
		Vector center = new Vector(Arrays.copyOf(vectors[0].values, vectors[0].values.length));
		for (int i = 1; i < vectors.length; i++) {
			if (vectors[i].values.length != center.values.length)
				throw new IllegalArgumentException("vector " + i + "'s dimension is not compatible with first vector!");
			
			for (int j = 0; j < center.values.length; j++) {
				center.values[j] += vectors[i].values[j]; 
			}
		}
		
		for (int i = 0; i < center.values.length; i++) {
			center.values[i] = center.values[i] / vectors.length; 
		}
		
		return center;
		
	}
	
	public int getDimensionCount() {
		return values.length;
	}
	
	public double getDimension(int dimIndex) {
		return values[dimIndex];
	}

	@Override
	public Object clone() {
		double[] _values = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			_values[i] = values[i]; 
		}
		// TODO Auto-generated method stub
		return new Vector(_values);
	}
	
}
