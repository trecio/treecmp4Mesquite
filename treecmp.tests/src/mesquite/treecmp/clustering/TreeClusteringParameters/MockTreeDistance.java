package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.List;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;

public class MockTreeDistance extends DistanceBetween2Trees {
	private final List<StoredDistanceBetweenTrees> distances = new ArrayList<StoredDistanceBetweenTrees>();
	
	public MockTreeDistance between(Tree tree1, Tree tree2, double distance) {
		distances.add(new StoredDistanceBetweenTrees(tree1, tree2, distance));
		return this;
	}


	@Override
	public void initialize(Tree t1, Tree t2) {
	}

	@Override
	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		for (final StoredDistanceBetweenTrees storedDistance : distances) {
			if (storedDistance.matches(t1, t2)) {
				result.setValue(storedDistance.value);
				return;
			} else {
				throw new RuntimeException("Unknown pair of trees.");
			}
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "Mock tree distance";
	}

	public class StoredDistanceBetweenTrees {
		private final Tree tree1;
		private final Tree tree2;
		public final double value;

		public StoredDistanceBetweenTrees(Tree tree1, Tree tree2,
				double distance) {
			this.tree1 = tree1;
			this.tree2 = tree2;
			this.value = distance;
		}

		public boolean matches(Tree t1, Tree t2) {
			return (tree1.equals(t1) && tree2.equals(t2))
					|| (tree1.equals(t2) && tree2.equals(t1));
		}

	}
}
