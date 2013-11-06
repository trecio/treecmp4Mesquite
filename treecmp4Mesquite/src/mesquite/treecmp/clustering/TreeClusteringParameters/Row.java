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
	
	public Row(String name, String value, String explanation) {
		this.name = name;
		values.put("", value);
		this.explanation = explanation;
	}

	public String get(String column) {
		return values.get(column);
	}
	
	public void set(String column, String value) {
		values.put(column, value);
	}
}
