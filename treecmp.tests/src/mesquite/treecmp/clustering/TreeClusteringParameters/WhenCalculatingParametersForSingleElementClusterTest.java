package mesquite.treecmp.clustering.TreeClusteringParameters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.TreeVector;
import mesquite.lib.duties.DistanceBetween2Trees;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingParametersForSingleElementClusterTest {
	private ClustersParameters parameters;

	@Before public void because() {
		final String treeDescription = "(A, B, (C, (D, E)))";
		final TreeVector cluster = Create.treeVector(treeDescription);
		final Collection<TreeVector> clusters = Arrays.asList(cluster);
		final DistanceBetween2Trees distance = new MockTreeDistance();
		parameters = TreeClusteringParametersCalculator.getParameters(clusters, distance , cluster.getTaxa());
	}
	
	@Test public void itShouldSetZeroAverageDistanceBetweenTrees() {
		assertEquals(0, parameters.cluster[0].avgDistance, 1e-6);
	}
	
	@Test public void itShouldSetZeroClusterDiameter() {
		assertEquals(0,  parameters.cluster[0].diameter, 1e-6);
	}
}
