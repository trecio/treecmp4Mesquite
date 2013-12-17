package mesquite.treecmp.clustering;

import java.util.List;

import mesquite.lib.MesquiteModule;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;

public abstract class GroupsForTreeVector extends MesquiteModule {
	protected int numberOfClusters;
	
	@Override
	public Class<?> getDutyClass() {
		return GroupsForTreeVector.class;
	}
	
	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	public abstract List<Integer> calculateClusters(Trees trees,
			DistanceBetween2Trees distance);
}
