package mesquite.treecmp.clustering.TreeClusteringParameters;

import mesquite.lib.MesquiteFile;

public class TablePrinter {

	public void print(Table table, MesquiteFile output) {
		final String separator = "\t";
		final StringBuilder lineBuilder = new StringBuilder();
		for (int i=0; i<table.rows.length; i++) {
			final Row row = table.rows[i];
			lineBuilder.append(row.name);
			for (int j=0; j<table.columns.size(); j++) {
				lineBuilder
					.append(separator)
					.append(row.get(table.columns.get(j)));
			}
			output.writeLine(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
	}
	
}
