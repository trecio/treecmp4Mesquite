package mesquite.treecmp.AsymmetricYuleSimulatedTrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import mesquite.lib.ExponentialDistribution;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteLong;
import mesquite.lib.MesquiteTree;
import mesquite.lib.ObjectContainer;
import mesquite.lib.RandomBetween;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.duties.TreeSimulate;

public class AsymmetricYuleSimulatedTrees extends TreeSimulate {
	private final RandomBetween randomTaxon = new RandomBetween();
	private final ExponentialDistribution waitingTime = new ExponentialDistribution();

	private double scaling;
	private double pLeft;
	private double pRight;

	@Override
	public int getNumberOfTrees(Taxa taxa) {
		return MesquiteInteger.infinite;
	}

	@Override
	public void initialize(Taxa taxa) {		
	}

	@Override
	public Tree getSimulatedTree(Taxa taxa, Tree tree, int treeNumber,
			ObjectContainer extra, MesquiteLong seed) {
		randomTaxon.setSeed(seed.getValue());
		waitingTime.setSeed(seed.getValue());
		if (tree==null || !(tree instanceof MesquiteTree))
			 tree = new MesquiteTree(taxa);
		final MesquiteTree mTree = ((MesquiteTree)tree);
		mTree.setToDefaultBush(2, false);
		
		for (int taxon = 2; taxon < taxa.getNumTaxa(); taxon++) {
			final List<Integer> leftDaughterTerminals = new ArrayList<Integer>(mTree.getNumTaxa());
			final List<Integer> rightDaughterTerminals = new ArrayList<Integer>(mTree.getNumTaxa());
			accumulateLeftAndRightDaughterTerminals(mTree, tree.getRoot(), leftDaughterTerminals, rightDaughterTerminals);
			final int terminalToSplit = drawTerminalNode(leftDaughterTerminals, rightDaughterTerminals);
			final int whichTaxon = mTree.taxonNumberOfNode(terminalToSplit);
			addLengthToAllTerminals(mTree, leftDaughterTerminals, getWaitingTime(taxon));
			addLengthToAllTerminals(mTree, rightDaughterTerminals, getWaitingTime(taxon));			
			mTree.splitTerminal(whichTaxon, taxon, false);
		}
/*			if (positions == null || positions.length != mTree.numberOfTerminalsInClade(mTree.getRoot()))
				positions = new double[mTree.numberOfTerminalsInClade(mTree.getRoot())];*/
		mTree.reshuffleTerminals(randomTaxon); //added after 1.03
		
		//adding to all terminals waiting time uniformly distributed between 0 and waiting time to next speciation
		
		final Collection<Integer> leftDaughterTerminals = new ArrayList<Integer>(mTree.getNumTaxa());
		final Collection<Integer> rightDaughterTerminals = new ArrayList<Integer>(mTree.getNumTaxa());
		accumulateLeftAndRightDaughterTerminals(mTree, tree.getRoot(), leftDaughterTerminals, rightDaughterTerminals);
		addLengthToAllTerminals(mTree, leftDaughterTerminals, waitingTime.nextDouble()*getWaitingTime(taxa.getNumTaxa()));
		addLengthToAllTerminals(mTree, rightDaughterTerminals, waitingTime.nextDouble()*getWaitingTime(taxa.getNumTaxa()));
		final double depth = mTree.tallestPathAboveNode(tree.getRoot());
		if (depth>0) {
			final double scaleFactor = scaling/depth;
 			for (int i=0; i<mTree.getNumNodeSpaces(); i++)
 				if (mTree.nodeExists(i)) {
 					final double b = mTree.getBranchLength(i, MesquiteDouble.unassigned);
 					if (MesquiteDouble.isCombinable(b))
 						mTree.setBranchLength(i, b*scaleFactor, false);
 				}
		}
		seed.setValue(randomTaxon.nextLong());  //see for next time
   		return mTree;
	}

	private int drawTerminalNode(List<Integer> leftDaughterTerminals,
			List<Integer> rightDaughterTerminals) {
		final double leftDaughterThreshold = leftDaughterTerminals.size() * pLeft; 
		final double scale = leftDaughterThreshold + rightDaughterTerminals.size() * pRight;
		final double r = randomTaxon.getDouble(0, scale);
		if (r < leftDaughterThreshold) {
			final int idx = (int) (r / pLeft);	//floor
			return leftDaughterTerminals.get(idx);
		} else {
			final int idx = (int) ((r - leftDaughterThreshold) / pRight);
			return rightDaughterTerminals.get(idx);
		}
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		final MesquiteInteger buttonPressed = new MesquiteInteger(AsymmetricYuleOptionsDialog.defaultCANCEL);
		final AsymmetricYuleOptionsDialog optionsDialog = new AsymmetricYuleOptionsDialog(containerOfModule(), buttonPressed);
		try {
			optionsDialog.completeAndShowDialog(true);
			if (buttonPressed.getValue() == AsymmetricYuleOptionsDialog.defaultOK) {
				final double imbalance = optionsDialog.getImbalance();
				pLeft = .5 - .5*imbalance;
				pRight = .5 + .5*imbalance;
				scaling = optionsDialog.getTotalTreeDepth();
				return true;
			}
		} finally {
			optionsDialog.dispose();
		}
		return false;
	}

	@Override
	public String getName() {
		return "Asymmetric Yule";
	}

	private static void addLengthToAllTerminals(MesquiteTree tree, Iterable<Integer> terminalNodes,
			double increment) {
		for (final Integer terminalNode : terminalNodes) {
			double current = tree.getBranchLength(terminalNode,
					MesquiteDouble.unassigned);
			if (MesquiteDouble.isCombinable(current))
				tree.setBranchLength(terminalNode, current + increment, false);
			else
				tree.setBranchLength(terminalNode, increment, false);
		}
	}

	private double getWaitingTime(int numTaxa) {
		return waitingTime.nextExponential(1.0 / numTaxa);
	}
	
	private void accumulateLeftAndRightDaughterTerminals(MesquiteTree tree,
			int root, Collection<Integer> leftDaughterTerminals,
			Collection<Integer> rightDaughterTerminals) {
		final Stack<Integer> stack = new Stack<Integer>();
		stack.add(root);
		while (!stack.empty()) {
			final Integer currentNode = stack.pop();
			if (tree.nodeIsTerminal(currentNode)) {
				final int mother = tree.motherOfNode(currentNode);
				final int firstChildOfMother = tree.firstDaughterOfNode(mother);
				if (firstChildOfMother == currentNode) {
					leftDaughterTerminals.add(currentNode);
				} else {
					rightDaughterTerminals.add(currentNode);
				}
			} else {
				for (int daughter = tree.firstDaughterOfNode(currentNode); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter)) {
					stack.add(daughter);
				}
			}
		}
	}
}
