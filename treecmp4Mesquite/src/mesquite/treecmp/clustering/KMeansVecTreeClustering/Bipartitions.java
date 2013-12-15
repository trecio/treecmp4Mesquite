package mesquite.treecmp.clustering.KMeansVecTreeClustering;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mesquite.consensus.lib.Bipartition;
import mesquite.consensus.lib.BipartitionVector;
import mesquite.lib.Tree;
import mesquite.treecmp.Utils;

public class Bipartitions {
	private Map<BitSet, Double> bipartitions;

	public Bipartitions(Tree tree) {
		BipartitionVector bipartitionVector = BipartitionVector.getBipartitionVector(tree);
		bipartitionVector.setUseWeights(true);
		final int numberOfBipartitions = bipartitionVector.size();
		bipartitions = new HashMap<BitSet, Double>(2*numberOfBipartitions);
		for (int i=0; i<numberOfBipartitions; i++) {
			final Bipartition bipartition = bipartitionVector.getBipart(i);
			final BitSet bitSet = Utils.convertToBitSet(bipartition.getBits());
			bipartitions.put(bitSet, 1.);
		}
	}

	public Bipartitions(Map<BitSet, Double> bipartitions) {
		this.bipartitions = bipartitions;
	}

	public Map<BitSet, Double> asMap() {
		return Collections.unmodifiableMap(bipartitions);
	}
}
