package mesquite.treecmp.clustering;

import org.pr.clustering.hierarchical.LinkageCriterion;

public interface AgglomerativeClustering {
	void configure(int numbeOfClusters, LinkageCriterion linkageCriterion);
}
