package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.util.Collection;

import mesquite.treecomp.TreeSetVisualizationV2.DiffMatrix;

public interface IClusteringAlgorithm {
	public Collection<Collection<Integer>> computeClusters(DiffMatrix matrix);	
	public void setNumberOfClusters(int number);
}
