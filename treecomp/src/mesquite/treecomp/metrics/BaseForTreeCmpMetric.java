package mesquite.treecomp.metrics;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.common.TreeConverter;

public abstract class BaseForTreeCmpMetric extends NumberFor2Trees {	

	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {

		PalFacade.Tree palT1 = TreeConverter.getPalFrom(t1);
		PalFacade.Tree palT2 = TreeConverter.getPalFrom(t2);
		
		if (isTreeRooted(t1) && isTreeRooted(t2))
			getDistanceForRootedTree(palT1, palT2, result, resultString);			
		else if (!isTreeRooted(t1) && !isTreeRooted(t2))
			getDistanceForUnrootedTree(palT1, palT2, result, resultString);
		else
			throw new IllegalArgumentException("Cannot calculate the distance betweeen a rooted and unrooted tree.");
	}

	public void initialize(Tree t1, Tree t2) {
	}

	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}
	
	protected abstract void getDistanceForRootedTree(PalFacade.Tree t1, PalFacade.Tree t2, MesquiteNumber number, MesquiteString string);
	protected abstract void getDistanceForUnrootedTree(PalFacade.Tree t1, PalFacade.Tree t2, MesquiteNumber number, MesquiteString string);
	
	protected final boolean isTreeRooted(Tree t) {
		int daughters = t.numberOfDaughtersOfNode(t.getRoot());
		return daughters < 3;
	}
}
