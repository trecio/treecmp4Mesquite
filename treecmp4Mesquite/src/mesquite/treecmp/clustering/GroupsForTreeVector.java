package mesquite.treecmp.clustering;

import java.util.List;

import mesquite.lib.MesquiteModule;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;

public abstract class GroupsForTreeVector extends MesquiteModule {
	@Override
	public Class<?> getDutyClass() {
		return GroupsForTreeVector.class;
	}

	public abstract List<Integer> calculateClusters(List<Tree> trees,
			DistanceBetween2Trees distance);
}
