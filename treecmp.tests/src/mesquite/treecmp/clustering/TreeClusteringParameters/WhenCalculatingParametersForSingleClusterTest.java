package mesquite.treecmp.clustering.TreeClusteringParameters;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.TreeVector;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClustersParameters;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingParametersForSingleClusterTest {
	private static final double FURTHERST_DISTANCE_BETWEEN_TREES = 100;
	private ClustersParameters parameters;

	@Before public void because() {
		final String[] cluster1Description = new String[] {
				"(A, B, (C, (D, E)))",
				"(A, B, (D, (C, E))"
		};

		final TreeVector cluster1 = Create.treeVector(cluster1Description); 
		final Collection<TreeVector> clusters = Arrays.asList(
				cluster1
			);
		DistanceBetween2Trees distance = new MockTreeDistance()
			.between(cluster1.getTree(0), cluster1.getTree(1), FURTHERST_DISTANCE_BETWEEN_TREES);
		
		parameters = TreeClusteringParametersCalculator.getParameters(clusters, distance, cluster1.getTaxa());
	}
	
	@Test public void itShouldReturnCorrectAverageDistanceBetweenTrees() {
		assertEquals(FURTHERST_DISTANCE_BETWEEN_TREES, parameters.cluster[0].avgDistance, 1e-6);
	}

	@Test public void itShouldReturnCorrectClusterDiameter() {
		assertEquals(FURTHERST_DISTANCE_BETWEEN_TREES, parameters.cluster[0].diameter, 1e-6);
	}
	
	@Test public void itShouldReturnCorrectSpecificity() {
		assertEquals(.5, parameters.cluster[0].specificity, 1e-6);
	}
	
	@Test public void itShouldReturnCorrectDensity() {
		assertEquals(2./3, parameters.cluster[0].density, 1e-6);
	}
}
