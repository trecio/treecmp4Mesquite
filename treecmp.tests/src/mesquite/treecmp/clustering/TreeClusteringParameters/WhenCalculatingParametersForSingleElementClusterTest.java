package mesquite.treecmp.clustering.TreeClusteringParameters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingParametersForSingleElementClusterTest {
	private ClustersParameters parameters;

	@Before public void because() {
		final String treeDescription = "(A, B, (C, (D, E)))";
		final TreeVector cluster = Create.treeVector(treeDescription);
		final Trees allTrees = cluster;
		final Collection<TreeVector> clusters = Arrays.asList(cluster);
		final DistanceBetween2Trees distance = new MockTreeDistance();
		parameters = TreeClusteringParametersCalculator.getParameters(allTrees, clusters, distance);
	}
	
	@Test public void itShouldSetZeroAverageDistanceBetweenTrees() {
		assertEquals(0, parameters.cluster[0].avgDistance, 1e-6);
	}
	
	@Test public void itShouldSetZeroClusterDiameter() {
		assertEquals(0,  parameters.cluster[0].diameter, 1e-6);
	}
	
	@Test public void itShouldSetZeroInformationLoss() {
		assertEquals(0, parameters.informationLoss.KL, 1e-6);
		assertEquals(0, parameters.informationLoss.L1, 1e-6);
		assertEquals(0, parameters.informationLoss.L2, 1e-6);
		assertEquals(0, parameters.informationLoss.Linf, 1e-6);
	}
}
