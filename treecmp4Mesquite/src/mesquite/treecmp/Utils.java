package mesquite.treecmp;

import java.util.ArrayList;
import java.util.List;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.ProgressIndicator;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;

public abstract class Utils {

	public static List<Tree> getTrees(TreeSourceDefinite treeSource, Taxa taxa) {
		final int numberOfTrees = treeSource.getNumberOfTrees(taxa);
		final List<Tree> trees = new ArrayList<Tree>(numberOfTrees);
		for (int i=0; i<trees.size(); i++) {
			trees.add(treeSource.getTree(taxa, i));
		}
		return trees;
	}

	public static double[][] calculateDistanceMatrix(DistanceBetween2Trees distance,
			final List<Tree> trees, ProgressIndicator progressMeter) {
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
			final Tree tree1 = trees.get(i);
			for (int j=i+1; j<numberOfTrees; j++, totalPairsCalculated++) {
				final Tree tree2 = trees.get(j);
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

}
