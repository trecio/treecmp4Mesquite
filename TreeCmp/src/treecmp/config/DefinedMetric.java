package treecmp.config;

import treecmp.metric.BaseMetric;

public class DefinedMetric {
	public final Class<? extends BaseMetric> implementation;
	public final String name;
	public final String commandName;
	public final String description;
	public String uniformFileName;
	public String yuleFileName;
	public String alnFileSuffix;
	public boolean diffLeaves;
	
	public DefinedMetric(Class<? extends BaseMetric> implementation, String name, String commandName, String description) {
		this.implementation = implementation;
		this.name = name;
		this.commandName = commandName;
		this.description = description;
	}
}
