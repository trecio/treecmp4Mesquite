package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

import java.util.IdentityHashMap;
import java.util.Map;

public class Row {
	private final Map<String, String> values = new IdentityHashMap<String, String>();
	public final String name;
	
	public Row(String name) {
		this.name = name;
	}
	
	public String get(String column) {
		return values.get(column);
	}
	
	public void set(String column, String value) {
		values.put(column, value);
	}
}
