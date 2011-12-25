package org.pr.clustering;

/**
 * it's a representation of the many-to-one relationship between a pattern and its cluster.
 */
public class PatternMembership {

	public final Vector pattern;
	public final int cluster;
	
	public PatternMembership(Vector pattern, int cluster) {
		this.cluster = cluster;
		this.pattern = pattern;
	}
	
}
