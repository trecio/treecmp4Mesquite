package mesquite.treecmp.clustering.TreeClusteringParameters;

public class Column {
	public final String title;
	public final String field;
	public final String explanation;

	public Column(String title) {
		this(title, "");
	}

	public Column(String title, String field) {
		this.explanation = "";
		this.field = field;
		this.title = title;
	}

	public String getString(Row row) {		
		return row.get(field); 
	}
}
