package mesquite.treecmp.clustering;

import java.util.Collection;
import java.util.List;

public interface ClusterCentresCalculation<T> {
	List<T> computeCentres(List<Collection<Integer>> associations);	
	double getDistanceFromCenterToTree(T center, T tree);
}
