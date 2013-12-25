package mesquite.treecmp.clustering.TreeClusteringParameters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingParametersForThreeClustersWithNotDisjointBoundingBallsTest {
	private ClustersParameters parameters;

	@Before public void because() {
		final Trees allTrees = Create.treeVector(
				"(C,(A,(B,(D,E))));",
				"(C,(B,(A,(D,E))));",
				"(C,(D,(E,(A,B))));",
				"(C,(E,(D,(A,B))));",
				"(C,((A,B),(D,E)));");
		
		final TreeVector cluster1 = Create.treeVector(allTrees, 0, 1);
		//Strict consensus tree for the first cluster: (A,B,C,(D,E));
		final TreeVector cluster2 = Create.treeVector(allTrees, 2, 3);
		//Strict consensus tree for the second cluster: ((A,B),C,D,E);
		final TreeVector cluster3 = Create.treeVector(allTrees, 4);
		//Strict consensus tree for the third cluster: (C,((A,B),(D,E)));
		//Bounding balls union size = 5, ((A,B),C,(D,E)) tree is in bounding ball of all three clusters
		
		final Collection<TreeVector> clusters = Arrays.asList(cluster1, cluster2, cluster3);
		final DistanceBetween2Trees distance = new MockTreeDistance();
		parameters = TreeClusteringParametersCalculator.getParameters(allTrees, clusters, distance);
	}
	
	@Test public void itShouldReturnInformationLossLinfNorm() {
		assertEquals(0, parameters.informationLoss.Linf, 1e-6);
	}
	
	@Test public void itShouldReturnInformationLossL1Norm() {
		assertEquals(0, parameters.informationLoss.L1, 1e-6);
	}
	
	@Test public void itShouldReturnInformationLossL2Norm() {
		assertEquals(0, parameters.informationLoss.L2, 1e-6);
	}
	
	@Test public void itShouldReturnInformationLossKLDistance() {
		assertEquals(0, parameters.informationLoss.KL, 1e-6);
	}
}
