package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

public class Column {
	public final String title;
	public final String field;

	public Column(String title) {
		this.field = "";
		this.title = title;
	}

	public Column(String title, String field) {
		this.field = field;
		this.title = title;
	}

	public String getString(Row row) {		
		return row.get(field); 
	}
}
