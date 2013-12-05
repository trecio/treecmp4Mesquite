package mesquite.treecmp.metrics.TreeDistancesImport;

import java.awt.FileDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;

public class TreeDistancesImport extends DistanceBetween2Trees {
	private double[][] data;

	@Override
	public void initialize(Tree t1, Tree t2) {
	}

	@Override
	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		final int t1Idx = t1.getFileIndex();
		final int t2Idx = t2.getFileIndex();
		
		if (0 <= t1Idx && t1Idx < data.length) {
			final double[] dataRow = data[t1Idx];
			
			if (0 <= t2Idx && t2Idx < dataRow.length) {
				result.setValue(dataRow[t2Idx]);
			} else {
				result.setToUnassigned();
			}
		} else {
			result.setToUnassigned();
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final MesquiteFile file = Utils.chooseFile(containerOfModule(), "Select CSV file", FileDialog.LOAD);
		if (file == null) {
			return sorry("No file was selected.");
		}
		if (!file.openReading()) {
			return sorry("Could not open file \"" + file.getFileName() + "\" for reading.");
		}
		try { 
			data = importFromFile(file);		
		} finally {
			file.closeReading();
		}
		return true;
	}

	private double[][] importFromFile(MesquiteFile file) {
		final List<double[]> results = new ArrayList<double[]>();
		String line;
		while (!StringUtils.isBlank(line = file.readNextDarkLine())) {
			final StringTokenizer tokenizer = new StringTokenizer(line);
			final double[] row = new double[tokenizer.countTokens()];
			for (int i=0; i<row.length; i++) {
				row[i] = Double.parseDouble(tokenizer.nextToken());
			}
			results.add(row);
		}
		return results.toArray(new double[0][]);
	}

	@Override
	public String getName() {
		return "Import from CSV file";
	}
}
