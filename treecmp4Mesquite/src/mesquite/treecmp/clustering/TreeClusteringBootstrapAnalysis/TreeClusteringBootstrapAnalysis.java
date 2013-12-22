package mesquite.treecmp.clustering.TreeClusteringBootstrapAnalysis;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mesquite.lib.ListDialog;
import mesquite.lib.Listable;
import mesquite.lib.ListableVector;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteModuleInfo;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.Taxa;
import mesquite.lib.TreeVector;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.FileAssistantA;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.ProgressIndicatorContext;
import mesquite.treecmp.ProgressReporter;
import mesquite.treecmp.Utils;
import mesquite.treecmp.clustering.GroupsForTreeVector;
import mesquite.treecmp.clustering.TreeClustering.PrecomputedDistanceBetween2Trees;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClusterParametersWindow;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClustersParameters;
import mesquite.treecmp.clustering.TreeClusteringParameters.MainTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.SummaryVerticalTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.Table;
import mesquite.treecmp.clustering.TreeClusteringParameters.TablePrinter;
import mesquite.treecmp.clustering.TreeClusteringParameters.TreeClusteringParametersCalculator;

public class TreeClusteringBootstrapAnalysis extends FileAssistantA {

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final Configuration configuration = new Configuration(10, 1, 10, true);
		
		final TreeSourceDefinite treeSource = (TreeSourceDefinite) hireEmployee(TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}
		final Taxa taxa = Utils.getOrChooseTaxa(this);
		final TreeVector trees = Utils.getTrees(treeSource, taxa);
		final MesquiteModuleInfo[] distances = hireEmployees(DistanceBetween2Trees.class, "Choose the tree distance measures you want to use:");
		if (distances == null) {
			return sorry("No tree distance measure has been chosen.");
		}
		final GroupsForTreeVector groupsBuilder = (GroupsForTreeVector) hireEmployee(GroupsForTreeVector.class, "Choose clustering algorithm.");
		if (groupsBuilder == null) {
			return sorry("No tree clustering algorithm has been chosen.");
		}

		for (final MesquiteModuleInfo distanceModule : distances) {
			final DistanceBetween2Trees distance = (DistanceBetween2Trees) hireEmployeeFromModuleInfo(distanceModule, DistanceBetween2Trees.class);
			final PrecomputedDistanceBetween2Trees cacheDistance = new PrecomputedDistanceBetween2Trees();
			if (!cacheDistance.prepare(getProject(), distance, trees)) {
				return false;
			}
			final int totalProgress = configuration.iterations * (configuration.maxClusters - configuration.minClusters + 1);
			final ProgressReporter progressMeter = ProgressIndicatorContext.enterFor(getProject(), "Analyzing clusters", totalProgress);
			int currentProgress = 0;
			boolean continueCalculations = true;
			final MainTableBuilder mainTableBuilder = new MainTableBuilder(false, true, false, false);
			final SummaryVerticalTableBuilder summaryTableBuilder = new SummaryVerticalTableBuilder();
			try {
				progressMeter.start();
				for (int numberOfClusters = configuration.minClusters; numberOfClusters <= configuration.maxClusters && continueCalculations; numberOfClusters++) {
					groupsBuilder.setNumberOfClusters(numberOfClusters);
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
				cacheDistance.persist();
				ProgressIndicatorContext.exit();
			}
			final Table mainTable = mainTableBuilder.getTable();
			final Table summaryTable = summaryTableBuilder.getTable();
			
			if (configuration.dump) {
				final String distanceName = distance.getClass().getSimpleName(); 
				dumpTable(mainTable, distanceName + "main.csv");
				dumpTable(summaryTable, distanceName + "summary.csv");
			}

			final ClusterParametersWindow window = new ClusterParametersWindow(this, mainTable, summaryTable);		
			window.show();
		}
		
		return true;
	}

	private void dumpTable(Table table, String fileName) {
		final File file = new File(fileName);
		final MesquiteFile output = MesquiteFile.newFile(file.getParent(), file.getName());
		final TablePrinter printer = new TablePrinter();
		if (output.openWriting(true)) {
			try {
				printer.print(table, output);
			} finally {
				output.closeWriting();
			}
		}
	}

	private <T> MesquiteModuleInfo[] hireEmployees(
			Class<T> dutyClass, String message) {
		final ListableVector modules = new ListableVector();
		for (final Listable moduleInfo : MesquiteTrunk.mesquiteModulesInfoVector.getModulesOfDuty(dutyClass, null, module, null)) {
			modules.addElement(moduleInfo, false);
		}
		
		String helpString = MesquiteString.helpString;
		final Listable[] selection = ListDialog.queryListMultiple(getModuleWindow(), "Select", message, helpString, modules, null);
		return Arrays.copyOf(selection, selection.length, MesquiteModuleInfo[].class);
	}

	@Override
	public String getName() {
		return "Tree Set Clustering Bootstrap Analysis";
	}

}
