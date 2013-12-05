package mesquite.treecmp.clustering.TreeClusteringBootstrapAnalysis;

import java.util.Collection;
import java.util.List;

import mesquite.lib.ProgressIndicator;
import mesquite.lib.Taxa;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.FileAssistantA;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.ProgressIndicatorContext;
import mesquite.treecmp.ProgressReporter;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.TreeClustering.CachedDistanceBetween2Trees;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClustersParameters;
import mesquite.treecmp.clustering.TreeClusteringParameters.MainTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.SummaryTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.TreeClusteringParametersCalculator;

public class TreeClusteringBootstrapAnalysis extends FileAssistantA {

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final Configuration configuration = new Configuration(10, 1, 10);
		
		final TreeSourceDefinite treeSource = (TreeSourceDefinite) hireEmployee(TreeSourceDefinite.class, "Choose the source trees:");
		final Taxa taxa = Utils.getOrChooseTaxa(this);
		final Trees trees = Utils.getTrees(treeSource, taxa);
		final DistanceBetween2Trees distance = (DistanceBetween2Trees) hireEmployee(DistanceBetween2Trees.class, "");
		final DistanceBetween2Trees cacheDistance = new CachedDistanceBetween2Trees(distance);
		final GroupsForTreeVector groupsBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		
		final int totalProgress = configuration.iterations * (configuration.maxClusters - configuration.minClusters + 1);
		final ProgressReporter progressMeter = ProgressIndicatorContext.enterFor(getProject(), "Analyzing clusters", totalProgress);
		int currentProgress = 0;
		boolean continueCalculations = true;
		final MainTableBuilder mainTableBuilder = new MainTableBuilder();
		final SummaryTableBuilder summaryTableBuilder = new SummaryTableBuilder();
		try {
			progressMeter.start();
			for (int numberOfClusters = configuration.minClusters; numberOfClusters <= configuration.maxClusters && continueCalculations; numberOfClusters++) {
				for (int iteration=0; iteration<configuration.iterations && continueCalculations; iteration++) {
					final List<Integer> clusterAssignment = groupsBuilder.calculateClusters(trees, cacheDistance);
					final Collection<TreeVector> clusters = Utils.inverseClusterAssignments(clusterAssignment, trees);
					final ClustersParameters parameters = TreeClusteringParametersCalculator.getParameters(trees, clusters, cacheDistance);
					
					mainTableBuilder.add(parameters);
					summaryTableBuilder.add(parameters);
					
					continueCalculations = !progressMeter.isAborted();
					progressMeter.setCurrentValue(++currentProgress);
				}
			}
		} finally {
			ProgressIndicatorContext.exit();
		}
		return true;
	}

	@Override
	public String getName() {
		return "Tree Set Clustering Bootstrap Analysis";
	}

}
