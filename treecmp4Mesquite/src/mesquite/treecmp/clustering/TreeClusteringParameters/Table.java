package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.Collections;
import java.util.List;


public class Table {
	public final Row[] rows;
	public final int numColumnsTotal;
	public final int numRowsTotal;
	public final List<Column> columns;

	public Table(List<Column> columns, Row[] rows) {
		this.rows = rows;
		this.columns = Collections.unmodifiableList(columns);
		numRowsTotal = rows.length;
		numColumnsTotal = columns.size();
	}

}
