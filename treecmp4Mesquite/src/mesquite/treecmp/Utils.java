package mesquite.treecmp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mesquite.lib.EmployerEmployee;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.ProgressIndicator;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;

public abstract class Utils {

	public static Trees getTrees(TreeSourceDefinite treeSource, Taxa taxa) {
		final TreeVector trees = new TreeVector(taxa);
		for (int i=0; i<treeSource.getNumberOfTrees(taxa); i++) {
			trees.addElement(treeSource.getTree(taxa, i), false);
		}
		return trees;
	}

	public static double[][] calculateDistanceMatrix(DistanceBetween2Trees distance,
			final Trees trees, ProgressIndicator progressMeter) {
		final int numberOfTrees = trees.size();
		final int numberOfPairs = (numberOfTrees * numberOfTrees - numberOfTrees) / 2;
		final int percentChange = numberOfPairs / 100;
		final double[][] distances = new double[numberOfTrees][];
		final MesquiteNumber result = new MesquiteNumber();
		final MesquiteString resultString = new MesquiteString();
		progressMeter.setTotalValue(numberOfPairs);
		
		for (int i=0; i<numberOfTrees; i++) {
			distances[i] = new double[numberOfTrees];
		}
		
		progressMeter.start();
		int totalPairsCalculated = 0;
		for (int i=0; i<numberOfTrees; i++) {
			final Tree tree1 = trees.getTree(i);
			for (int j=i+1; j<numberOfTrees; j++, totalPairsCalculated++) {
				final Tree tree2 = trees.getTree(j);
				distance.calculateNumber(tree1, tree2, result, resultString);
				distances[i][j] = distances[j][i] = result.getDoubleValue();
				
				if (progressMeter.isAborted()) {
					progressMeter.goAway();
					return null;
				}
				if (totalPairsCalculated % percentChange == 0) {
					progressMeter.setCurrentValue(totalPairsCalculated);
				}
			}
		}
		progressMeter.goAway();
		
		return distances;
	}

	public static List<Integer> convertToAssignments(
			int n, Collection<Collection<Integer>> partitioning) {
		final Integer[] results = new Integer[n];
		int partitionNumber = 1;
		for (final Collection<Integer> partition : partitioning) {
			for (final Integer idx : partition) {
				results[idx] = partitionNumber;
			}
			partitionNumber++;
		}
		
		return Arrays.asList(results);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MesquiteModule> T findColleagueOrHireNew(
			EmployerEmployee me, Class<T> dutyClass,
			String explanation) {
		final T foundEmployee = (T) me.findNearestColleagueWithDuty(dutyClass);
		return foundEmployee != null
				? foundEmployee
				: (T) me.hireEmployee(dutyClass, explanation);
	}
}
