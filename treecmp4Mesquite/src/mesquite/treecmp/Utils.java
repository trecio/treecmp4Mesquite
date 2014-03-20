package mesquite.treecmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mesquite.lib.Bits;
import mesquite.lib.ColorTheme;
import mesquite.lib.EmployerEmployee;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteFileDialog;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteTree;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.StringUtil;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.clustering.TreeClusteringParameters.TreeClusteringParameters;

public final class Utils {
	private Utils() {}

	public static TreeVector getTrees(TreeSourceDefinite treeSource, Taxa taxa) {
		final TreeVector trees = new TreeVector(taxa);
		for (int i=0; i<treeSource.getNumberOfTrees(taxa); i++) {
			trees.addElement(treeSource.getTree(taxa, i), false);
		}
		return trees;
	}

	public static double[][] calculateDistanceMatrix(DistanceBetween2Trees distance,
			final Trees trees, MesquiteProject project) {

		final int numberOfTrees = trees.size();
		final int numberOfPairs = (numberOfTrees * numberOfTrees - numberOfTrees) / 2;
		final int percentChange = Math.max(numberOfPairs / 100, 1);
		final double[][] distances = new double[numberOfTrees][];
		final MesquiteNumber result = new MesquiteNumber();
		final MesquiteString resultString = new MesquiteString();
		
		for (int i=0; i<numberOfTrees; i++) {
			distances[i] = new double[numberOfTrees];
		}
		
		final ProgressReporter progressMeter = ProgressIndicatorContext.enterFor(project, "Calculating Tree Differences", numberOfPairs);
		try {		
			progressMeter.start();
			int totalPairsCalculated = 0;
			for (int i=0; i<numberOfTrees; i++) {
				final Tree tree1 = trees.getTree(i);
				for (int j=i+1; j<numberOfTrees; j++, totalPairsCalculated++) {
					final Tree tree2 = trees.getTree(j);
					distance.calculateNumber(tree1, tree2, result, resultString);
					distances[i][j] = distances[j][i] = result.getDoubleValue();
					
					if (progressMeter.isAborted()) {
						return null;
					}
					if (totalPairsCalculated % percentChange == 0) {
						progressMeter.setCurrentValue(totalPairsCalculated);
					}
				}
			}
		} finally {
			ProgressIndicatorContext.exit();
		}
		
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
	
	public static Taxa getOrChooseTaxa(MesquiteModule me) {
		final MesquiteProject project = me.getProject();
		if (project.getTaxas().size() == 1) {
			return project.getTaxa(0);
		}
		return project.chooseTaxa(me.containerOfModule(), "Choose the block of taxa:");
	}

	@SuppressWarnings("unchecked")
	public static <T extends MesquiteModule> T hireExactImplementation(
			EmployerEmployee me,
			Class<T> implementationClass) {
		final String employeeName = '#' + implementationClass.getSimpleName();
		return (T) me.hireNamedEmployee(implementationClass, employeeName);
	}

	public static List<TreeVector> inverseClusterAssignments(
			List<Integer> assignments, Trees trees) {
		final Map<Integer, TreeVector> clusters = new HashMap<Integer, TreeVector>();
		for (int i=0; i<assignments.size(); i++) {
			final int clusterNumber = assignments.get(i);
			TreeVector cluster = clusters.get(clusterNumber);
			if (cluster == null) {
				cluster = new TreeVector(trees.getTaxa());
				clusters.put(clusterNumber, cluster);
			}
			cluster.addElement(trees.getTree(i), false);
		}
		return new ArrayList<TreeVector>(clusters.values());
	}

	public static String formatDouble(double value) {
		return String.format(TreeClusteringParameters.DOUBLE_FORMAT, value);
	}

	public static MesquiteFile chooseFile(MesquiteWindow parentWindow, String title, int dialogType) {
		final MesquiteFileDialog dialog = new MesquiteFileDialog(parentWindow, title, dialogType);
		dialog.setBackground(ColorTheme.getInterfaceBackground());
		dialog.setVisible(true);
		final String fileName = dialog.getFile();
		String directoryName = dialog.getDirectory();
		
		if (StringUtil.blank(directoryName)) {
			directoryName = "";
		}
		return StringUtil.blank(fileName)
				? null
				: MesquiteFile.newFile(directoryName, fileName);
	}

	public static BitSet convertToBitSet(Bits bits) {
		final BitSet bitSet = new BitSet(bits.getSize());
		for (int i = bits.firstBitOn(); i>=0; i = bits.nextBit(i+1, true)) {
			bitSet.set(i, true);
		}		
		return bitSet;
	}

	public static MesquiteTree setUnassignedBranchLengthsToOne(
			Tree tree) {
		final double DEFAULT_BRANCH_LENGTH = 1.0;
		final MesquiteTree clonedTree = tree.cloneTree();
		clonedTree.setAllUnassignedBranchLengths(DEFAULT_BRANCH_LENGTH, false);		
		return clonedTree;
	}

	public static void exportToFile(double[][] distanceMatrix, MesquiteFile outputFile) {
		final String separator = "\t";
		final StringBuilder lineBuilder = new StringBuilder();
		for (int i=0; i<distanceMatrix.length; i++) {
			lineBuilder.append(distanceMatrix[i][0]);
			for (int j=1; j<distanceMatrix[i].length; j++) {
				lineBuilder
					.append(separator)
					.append(distanceMatrix[i][j]);
			}
			outputFile.writeLine(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
	}
}
