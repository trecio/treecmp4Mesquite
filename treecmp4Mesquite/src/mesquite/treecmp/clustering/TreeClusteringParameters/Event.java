package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.util.ArrayList;
import java.util.Collection;

public class Event {
	private final Collection<Runnable> listeners = new ArrayList<Runnable>();

	public void fire() {
		for (final Runnable listener : listeners) {
			listener.run();
		}
	}

	public void subscribe(Runnable listener) {
		listeners.add(listener);
	}

}
