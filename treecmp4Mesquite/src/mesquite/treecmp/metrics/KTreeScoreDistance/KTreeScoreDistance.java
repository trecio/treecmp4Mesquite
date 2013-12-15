package mesquite.treecmp.metrics.KTreeScoreDistance;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import mesquite.consensus.lib.Bipartition;
import mesquite.consensus.lib.BipartitionVector;
import mesquite.lib.Bits;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteTree;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;

public class KTreeScoreDistance extends DistanceBetween2Trees {
	@Override
	public void initialize(Tree t1, Tree t2) {
	}

	@Override
	public void calculateNumber(Tree t1, Tree t2,
			MesquiteNumber result, MesquiteString resultString) {
		final Map<BitSet, Double> t1Biparts = createBipartitions(t1);
		final Map<BitSet, Double> t2Biparts = createBipartitions(t2);
		
		double matchingBipartsScalarProduct = 0;
		double allT2BranchesSumOfSquares = 0;
		for (final Map.Entry<BitSet, Double> t2Bipart : t2Biparts.entrySet()) {
			final Double t2SplitLength = t2Bipart.getValue();
			final Double t1SplitLength = t1Biparts.get(t2Bipart.getKey());
			if (t1SplitLength != null) {
				matchingBipartsScalarProduct += t2SplitLength * t1SplitLength;
			}
			allT2BranchesSumOfSquares += t2SplitLength * t2SplitLength;
		}
		final double K = matchingBipartsScalarProduct / allT2BranchesSumOfSquares;
		double distance = 0;
		for (final Map.Entry<BitSet, Double> t1Bipart : t1Biparts.entrySet()) {
			final Double t2SplitLength = t2Biparts.get(t1Bipart.getKey());
			final double difference = t1Bipart.getValue()
					- K * (t2SplitLength != null
						? t2SplitLength
						: 0);
			distance += difference * difference;
		}
		for (final Map.Entry<BitSet, Double> t2Bipart : t2Biparts.entrySet()) {
			final boolean isAlreadyCounted = t1Biparts.containsKey(t2Bipart.getKey());
			if (!isAlreadyCounted) {
				final double difference = 0 - K * t2Bipart.getValue();
				distance += difference * difference;
			}
		}
		
		distance = Math.sqrt(distance);
		
		result.setValue(distance);
		if (resultString != null) {
			resultString.setValue("K Tree Score: " + distance);
		}
	}

	private Map<BitSet, Double> createBipartitions(Tree tree) {
		final MesquiteTree treeWithoutUnassignedBranchLengths = Utils.setUnassignedBranchLengthsToOne(tree);
		final BipartitionVector bipartitionVector = new BipartitionVector();
		final Taxa taxa = treeWithoutUnassignedBranchLengths.getTaxa();
		final int numTaxa = taxa.getNumTaxa();
		bipartitionVector.setRooted(false);
		bipartitionVector.setTaxa(taxa);
		bipartitionVector.addTree(treeWithoutUnassignedBranchLengths);
		
		final Map<BitSet, Double> result = new HashMap<BitSet, Double>();
		for (int i=0; i<bipartitionVector.size(); i++) {
			final Bipartition bipartition = bipartitionVector.getBipart(i);
			final Bits bits = bipartition.getBits();
			if (!bits.allBitsOn()) {	//ignore split including all taxa on one side
				final BitSet bitSet = Utils.convertToBitSet(bipartition.getBits());
				flipIfZeroNotSet(bitSet, numTaxa);
				final double splitLength = bipartition.getSplitLength();				
				result.put(bitSet, splitLength);
			}
		}
		
		for (int node=treeWithoutUnassignedBranchLengths.firstInPostorder(); node!=0; node = treeWithoutUnassignedBranchLengths.nextInPostorder(node)) {
			if (treeWithoutUnassignedBranchLengths.nodeIsTerminal(node)) {
				final int taxonNumber = treeWithoutUnassignedBranchLengths.taxonNumberOfNode(node);
				final BitSet bitSet = new BitSet(taxa.getNumTaxa());
				bitSet.set(taxonNumber);
				flipIfZeroNotSet(bitSet, numTaxa);
				final double branchLength = treeWithoutUnassignedBranchLengths.getBranchLength(node); 
				result.put(bitSet, branchLength);
			}
		}
		
		return result;
	}

	private void flipIfZeroNotSet(BitSet bitSet, int size) {
		if (!bitSet.get(0)) {
			bitSet.flip(0, size);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	@Override
	public String getName() {
		return "K Tree Score (Unrooted) [TREECMP]";
	}
}
