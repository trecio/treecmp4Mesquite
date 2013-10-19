package mesquite.treecmp.clustering.TreeClustering;

import java.util.WeakHashMap;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;

public class CachedDistanceBetween2Trees extends DistanceBetween2Trees {
	private final DistanceBetween2Trees distance;
	private final WeakHashMap<Pair<Tree, Tree>, Double> cache = new WeakHashMap<Pair<Tree, Tree>, Double>();

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
		} else {
			distance.initialize(t1, t2);
			distance.calculateNumber(t1, t2, result, null);
			cache.put(pair, result.getDoubleValue());
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
