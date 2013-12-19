package mesquite.treecmp.clustering.TreeClusteringBootstrapAnalysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import mesquite.lib.CommandRecord;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.ListDialog;
import mesquite.lib.Listable;
import mesquite.lib.ListableVector;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModuleInfo;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.StringUtil;
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
import mesquite.treecmp.clustering.TreeClusteringParameters.ClusterParametersWindow;
import mesquite.treecmp.clustering.TreeClusteringParameters.ClustersParameters;
import mesquite.treecmp.clustering.TreeClusteringParameters.MainTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.SummaryVerticalTableBuilder;
import mesquite.treecmp.clustering.TreeClusteringParameters.Table;
import mesquite.treecmp.clustering.TreeClusteringParameters.TreeClusteringParametersCalculator;

public class TreeClusteringBootstrapAnalysis extends FileAssistantA {

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final Configuration configuration = new Configuration(10, 1, 10);
		
		final TreeSourceDefinite treeSource = (TreeSourceDefinite) hireEmployee(TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}
		final Taxa taxa = Utils.getOrChooseTaxa(this);
		final Trees trees = Utils.getTrees(treeSource, taxa);
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
			final DistanceBetween2Trees cacheDistance = new CachedDistanceBetween2Trees(distance);
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
				ProgressIndicatorContext.exit();
			}
			final Table mainTable = mainTableBuilder.getTable();
			final Table summaryTable = summaryTableBuilder.getTable();

			final ClusterParametersWindow window = new ClusterParametersWindow(this, mainTable, summaryTable);		
			window.show();
		}
		
		return true;
	}

	private <T> MesquiteModuleInfo[] hireEmployees(
			Class<T> dutyClass, String message) {
		final ListableVector modules = new ListableVector();
		for (final Listable moduleInfo : MesquiteTrunk.mesquiteModulesInfoVector.getModulesOfDuty(dutyClass, null, module, null)) {
			modules.addElement(moduleInfo, false);
		}
		
		/*Vector hiringPath = CommandRecord.getHiringPathS();
		if (hiringPath != null && hiringPath.size()>0){
			MesquiteModuleInfo mmi = (MesquiteModuleInfo)hiringPath.lastElement();
			if (dutyClass.isAssignableFrom(mmi.getModuleClass()) && (condition == null || mmi.isCompatible(condition, module.getProject(), this))){
				return mmi;
			}
		}*/
		String helpString = MesquiteString.helpString;
		/*if (StringUtil.blank(s)) {
			String duty = MesquiteTrunk.mesquiteTrunk.mesquiteModulesInfoVector.getDutyName(dutyClass);

			s = "<h3>" + duty + "</h3>";
			EmployeeNeed need = findEmployeeNeed(dutyClass);
			if (need != null)
				s += need.getExplanation();
			else {
				s += "<p>(Needed by " + getName() + ")";
				if (MesquiteTrunk.debugMode)
					s += "<br>" + module.getModuleInfo().getShortClassName();
			}

		}*/
		/*if (!StringUtil.blank(compatibilityReport))
			s += "<hr><h4>Note</h4>" +compatibilityReport.toString();*/
		/*if (subChoicesOnInDialogs){
			//embedding hiring subchoices (new to 2. 01)
			int countExtras = 0;
			for (int i=0; i< names.length; i++){
				MesquiteModuleInfo mci = (MesquiteModuleInfo)names[i];
				if (mci.getHireSubchoice()!= null){
					Listable[] sub = MesquiteTrunk.mesquiteModulesInfoVector.getModulesOfDuty(mci.getHireSubchoice(), condition, null, null);
					if (sub != null)
						countExtras += sub.length;
				}
			}
			if (countExtras > 0){
				boolean[] isSubchoice = new boolean[countExtras + names.length];
				Listable[] newList = new Listable[countExtras + names.length];
				int count = 0;
				for (int i=0; i< names.length; i++){
					MesquiteModuleInfo mci = (MesquiteModuleInfo)names[i];
					newList[count] = mci;
					isSubchoice[count++] = false;
					if (mci.getHireSubchoice()!= null){  
						Listable[] sub = MesquiteTrunk.mesquiteModulesInfoVector.getModulesOfDuty(mci.getHireSubchoice(), condition, null, null);
						sub = prioritize(sub, mci.getHireSubchoice());
						if (sub != null){
							for (int k = 0; k< sub.length; k++){
								isSubchoice[count] = true;
								newList[count++] = sub[k];
							}
						}
					}
				}

				int chosen =   ListDialog.queryModuleList(module, "Select", message, s, newList, isSubchoice, true, dutyClass, 0);

				if (!MesquiteInteger.isCombinable(chosen))
					return null;
				if (isSubchoice[chosen]){

					if (arguments != null){
						MesquiteModuleInfo mci = (MesquiteModuleInfo)newList[chosen];
						arguments.setValue("#" + mci.getClassName()); 
					}
					for (int k = chosen-1; k>=0; k--){
						if (!isSubchoice[k])
							return newList[k];
					}
					if (arguments != null)
						arguments.setValue((String)null);
					return null;
				}
				else
					return newList[chosen];
			}
		}*/

		final Listable[] selection = ListDialog.queryListMultiple(getModuleWindow(), "Select", message, helpString, modules, null);
		return Arrays.copyOf(selection, selection.length, MesquiteModuleInfo[].class);
	}

	@Override
	public String getName() {
		return "Tree Set Clustering Bootstrap Analysis";
	}

}
