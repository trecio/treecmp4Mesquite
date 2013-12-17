package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.IdentityHashMap;
import java.util.Map;

public class Row {
	private final Map<String, String> values = new IdentityHashMap<String, String>();
	public final String name;
	public final String explanation;
	
	public Row(String name) {
		this.name = name;
		this.explanation = "";
	}
	
	public Row(String name, String explanation) {
		this.name = name;
		this.explanation = explanation;
	}

	public String get(Column column) {
		return values.get(column.field);
	}
	
	public void set(Column column, String value) {
		values.put(column.field, value);
	}
}
