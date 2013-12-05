package mesquite.treecmp.TreeDistancesExport;


import java.awt.FileDialog;

import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteProject;
import mesquite.lib.Taxa;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.lib.duties.FileAssistantT;
import mesquite.lib.duties.TreeSourceDefinite;
import mesquite.treecmp.Utils;

public class TreeDistancesExport extends FileAssistantT {

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final MesquiteProject project = employer.getProject(); 
		final Taxa taxa = Utils.getOrChooseTaxa(this);
		final TreeSourceDefinite treeSource = Utils.findColleagueOrHireNew(this, TreeSourceDefinite.class, "Choose the source trees:");
		if (treeSource == null) {
			return sorry("No trees has been chosen.");
		}
			
		final DistanceBetween2Trees distance = (DistanceBetween2Trees) hireEmployee(DistanceBetween2Trees.class, "Choose the tree distance measure you want to use:");
		if (distance == null) {
			return sorry("No tree distance measure has been chosen.");
		}
		
		final MesquiteFile outputFile = Utils.chooseFile(containerOfModule(), "Export to CSV file", FileDialog.SAVE);
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
		final Trees trees = Utils.getTrees(treeSource, taxa);

		return Utils.calculateDistanceMatrix(distance, trees, project);
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
