package mesquite.treecmp.clustering.TreeClusteringParametersListAssistant;

import java.lang.reflect.Field;

public class FieldAccessor<T> {
	private final Field field;

	public FieldAccessor(String fieldName, Class<T> clazz) {
		try {
			field = clazz.getField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Could not create field accessor.", e);
		} catch (SecurityException e) {
			throw new RuntimeException("Could not create field accessor.", e);
		}
	}

	public Object get(T obj) {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Could not get value of field: " + field.getName());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not get value of field: " + field.getName());
		}
	}

}
