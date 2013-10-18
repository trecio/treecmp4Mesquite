package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

public class Column {
	public final String title;
	public final String field;

	public Column(String title, String field) {
		this.title = title;
		this.field = field;
	}

	public String getString(Row row) {
		return row.get(field); 
	}
}
