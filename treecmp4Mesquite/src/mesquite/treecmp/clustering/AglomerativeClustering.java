package mesquite.treecmp.clustering;

import org.pr.clustering.hierarchical.LinkageCriterion;

public interface AglomerativeClustering {
	void configure(int numbeOfClusters, LinkageCriterion linkageCriterion);
}
