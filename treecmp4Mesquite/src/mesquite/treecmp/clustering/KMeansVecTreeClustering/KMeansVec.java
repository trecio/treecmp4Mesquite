package mesquite.treecmp.clustering.KMeansVecTreeClustering;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mesquite.treecmp.clustering.ClusterCentresCalculation;

class KMeansVec implements ClusterCentresCalculation<Bipartitions> {
	private final List<Bipartitions> bipartitions;
	
	public KMeansVec(List<Bipartitions> listOfBipartitions) {
		this.bipartitions = listOfBipartitions;
	}

	public List<Bipartitions> computeCentres(
			List<Collection<Integer>> associations) {
		final List<Bipartitions> centres = new ArrayList<Bipartitions>(associations.size());
		for (final Collection<Integer> association : associations) {
			centres.add(computeCentre(association));
		}
		return centres;
	}

	private Bipartitions computeCentre(Collection<Integer> association) {
		final Map<BitSet, Double> centreBipartitions = new HashMap<BitSet, Double>();
		final double normalizedOne = 1./association.size();
		for (final Integer index : association) {
			final Bipartitions tree = bipartitions.get(index);
			for(final Map.Entry<BitSet, Double> entry : tree.asMap().entrySet()) {
				final BitSet bipartition = entry.getKey();
				final Double existingFrequency = centreBipartitions.get(bipartition);
				if (existingFrequency != null) {
					centreBipartitions.put(bipartition, existingFrequency + normalizedOne);
				} else {
					centreBipartitions.put(bipartition, normalizedOne);
				}
			}
		}
		return new Bipartitions(centreBipartitions);
	}

	public double getDistanceFromCenterToTree(Bipartitions center,
			Bipartitions tree) {
		final Map<BitSet, Double> centerBipartitions = center.asMap();
		final Map<BitSet, Double> treeBipartitions = tree.asMap();
		
		final Set<BitSet> unionOfBipartitions = new HashSet<BitSet>(centerBipartitions.keySet());
		unionOfBipartitions.addAll(treeBipartitions.keySet());
		
		double distance = 0;
		
		for (final BitSet bipartition : unionOfBipartitions) {
			final Double frequencyInCenter = centerBipartitions.get(bipartition);
			final Double frequencyInTree = treeBipartitions.get(bipartition);
			double difference;
			if (frequencyInCenter != null && frequencyInTree == null) {
				difference = frequencyInCenter;
			} else if (frequencyInCenter != null && frequencyInTree != null) {
				difference = frequencyInCenter - frequencyInTree;
			} else if (frequencyInCenter == null && frequencyInTree != null) {
				difference = frequencyInTree;
			} else {
				throw new RuntimeException("Unreachable code executed.");
			}
			distance += difference * difference;
		}
		return distance;
	}
}
