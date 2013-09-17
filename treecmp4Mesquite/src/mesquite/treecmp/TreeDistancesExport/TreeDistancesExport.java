package mesquite.treecmp.TreeDistancesExport;

import java.awt.FileDialog;

import org.apache.commons.lang.StringUtils;

import mesquite.lib.ColorTheme;
import mesquite.lib.ExporterDialog;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteFileDialog;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteString;
import mesquite.lib.ProgressIndicator;
import mesquite.lib.StringUtil;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.FileAssistantT;
import mesquite.lib.duties.FileCoordinator;
import mesquite.lib.duties.TreeSourceDefinite;

public class TreeDistancesExport extends FileAssistantT {

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final MesquiteProject project = employer.getProject(); 
		final Taxa taxa = project.chooseTaxa(containerOfModule(), "Choose the block of taxa:");
		final TreeSourceDefinite treeSource = (TreeSourceDefinite) hireEmployee(TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}
			
		final DistanceBetween2Trees distance = (DistanceBetween2Trees) hireEmployee(DistanceBetween2Trees.class, "Choose the tree distance measure you want to use:");
		if (distance == null) {
			return sorry("No tree distance measure has been chosen.");
		}
		
		final MesquiteFile outputFile = chooseFile();
		if (outputFile == null) {
			return sorry("No output file was selected.");
		}
		if (!outputFile.openWriting(true)) {
			return sorry("Could not open file \"" + outputFile.getFileName() + "\" for writing.");
		}
		try {			
			final double[][] distanceMatrix = calculateTreeDistances(project, treeSource, taxa, distance);
			if (distanceMatrix == null) {
				return false;
			}
			exportToFile(distanceMatrix, outputFile);
		} finally {
			outputFile.closeWriting();
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Export distances between trees to file.";
	}
	
	@Override
	public String getNameForMenuItem() {
		return "Export distances between trees";
	}

	private double[][] calculateTreeDistances(MesquiteProject project, TreeSourceDefinite treeSource,
			Taxa taxa, DistanceBetween2Trees distance) {
		final int numberOfTrees = treeSource.getNumberOfTrees(taxa);
		final int numberOfPairs = (numberOfTrees * numberOfTrees - numberOfTrees) / 2;
		final int percentChange = numberOfPairs / 100;
		final double[][] distances = new double[numberOfTrees][];
		final MesquiteNumber result = new MesquiteNumber();
		final MesquiteString resultString = new MesquiteString();
		
		ProgressIndicator progressMeter = new ProgressIndicator(project, "Calculating Tree Differences", numberOfPairs, true);
		progressMeter.start();
		
		for (int i=0; i<numberOfTrees; i++) {
			distances[i] = new double[numberOfTrees];
		}
		
		int totalPairsCalculated = 0;
		for (int i=0; i<numberOfTrees; i++) {
			final Tree tree1 = treeSource.getTree(taxa, i);
			for (int j=i+1; j<numberOfTrees; j++, totalPairsCalculated++) {
				final Tree tree2 = treeSource.getTree(taxa, j);
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

	private MesquiteFile chooseFile() {
		final MesquiteFileDialog dialog = new MesquiteFileDialog(containerOfModule(), "Export to CSV file", FileDialog.SAVE);
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
	
	private void exportToFile(double[][] distanceMatrix, MesquiteFile outputFile) {
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
