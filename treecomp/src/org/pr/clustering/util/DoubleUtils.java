package org.pr.clustering.util;

/**
 * @author Ahmad
 *
 */
public class DoubleUtils {

	public static final double EPSILON = 0.0001;
	
	public static boolean equal(double first, double second) {
		return equal(first, second, EPSILON);
	}

	public static boolean equal(double first, double second, double epsilon) {
		return Math.abs(first - second) <= epsilon;
	}
	
	
}
