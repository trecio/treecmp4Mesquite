package mesquite.treecmp.clustering;

import java.util.List;

import mesquite.lib.MesquiteModule;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;

public abstract class GroupsForTreeVector extends MesquiteModule {
	@Override
	public Class<?> getDutyClass() {
		return GroupsForTreeVector.class;
	}

	public abstract List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance);
}
