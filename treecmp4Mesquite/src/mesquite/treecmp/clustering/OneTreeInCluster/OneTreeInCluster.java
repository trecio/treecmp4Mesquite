package mesquite.treecmp.clustering.OneTreeInCluster;

import java.util.ArrayList;
import java.util.List;

import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.clustering.GroupsForTreeVector;

public class OneTreeInCluster extends GroupsForTreeVector {

	@Override
	public List<Integer> calculateClusters(List<Tree> trees,
			DistanceBetween2Trees distance) {
		final List<Integer> assignments = new ArrayList<Integer>(trees.size());
		for (int i=0; i<trees.size(); i++) {
			assignments.add(i+1);
		}
		return assignments;
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "";
	}

}
