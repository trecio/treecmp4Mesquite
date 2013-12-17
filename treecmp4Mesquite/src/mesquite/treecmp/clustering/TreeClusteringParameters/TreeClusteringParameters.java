package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.Collection;

import mesquite.lib.MesquiteModule;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.clustering.TreeClustering.TreeClustering;

public final class TreeClusteringParameters extends MesquiteModule {	
	public static final String DOUBLE_FORMAT = "%.3G";
		
	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final TreeClustering treeClustering = (TreeClustering) findNearestColleagueWithDuty(TreeClustering.class);
		final Collection<TreeVector> clusters = treeClustering.getClusters();
		final Trees allTrees = treeClustering.allTrees(); 
		final DistanceBetween2Trees distance = treeClustering.getDistance();
		final ClustersParameters parameters = TreeClusteringParametersCalculator.getParameters(allTrees, clusters, distance);
		
		final Table mainTable = new MainTableBuilder().add(parameters).getTable();
		final Table summaryTable = new SummaryHorizontalTableBuilder().add(parameters).getTable();		

		final ClusterParametersWindow window = new ClusterParametersWindow(this, mainTable, summaryTable);		
		window.show();	
		return true;
	}

	@Override
	public Class<?> getDutyClass() {
		return TreeClusteringParameters.class;
	}

	@Override
	public String getName() {
		return "Tree clustering parameters";
	}
}
