package mesquite.treecmp.clustering.TreeClustering;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import mesquite.Mesquite;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;

public class CachedDistanceBetween2Trees extends DistanceBetween2Trees {
	private final DistanceBetween2Trees distance;
	private final Map<Pair<Tree, Tree>, Double> cache = new HashMap<Pair<Tree, Tree>, Double>();
	private long hits = 0;
	private long misses = 0;

	public CachedDistanceBetween2Trees(DistanceBetween2Trees distance) {
		this.distance = distance;
	}

	@Override
	public void initialize(Tree t1, Tree t2) {		
	}

	@Override
	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		Pair<Tree, Tree> pair = new Pair<Tree, Tree>(t1, t2);
		Double value = cache.get(pair);
		if (value != null) {
			result.setValue(value);
			hits += 1;
		} else {
			distance.initialize(t1, t2);
			distance.calculateNumber(t1, t2, result, null);
			cache.put(pair, result.getDoubleValue());
			misses += 1;
		}
		if (((misses+hits)%10000) == 0) {
			final String logMessage = "cache size: " + cache.size() + " hits: " + hits + " misses: " + misses + " ratio: " + 1.*hits / (hits+misses); 
			System.err.println(logMessage);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return distance.startJob(arguments, condition, hiredByName);
	}

	@Override
	public String getName() {
		return distance.getName();
	}

}
