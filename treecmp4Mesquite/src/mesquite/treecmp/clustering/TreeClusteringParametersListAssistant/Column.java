package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;


public class Column<T> {
	public final String title;
	public final FieldAccessor<T> valueAccessor;

	public Column(String title, String field, Class<T> clazz) {
		this.title = title;
		this.valueAccessor = new FieldAccessor<T>(field, clazz);
	}
}
