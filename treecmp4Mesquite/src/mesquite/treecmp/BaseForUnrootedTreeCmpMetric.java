package mesquite.treecmp;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.DistanceBetween2Trees;

public abstract class BaseForUnrootedTreeCmpMetric extends DistanceBetween2Trees {
	private final boolean requiresBranchLengths;
	
	protected BaseForUnrootedTreeCmpMetric() {
		requiresBranchLengths = false;
	}
	
	protected BaseForUnrootedTreeCmpMetric(boolean requiresBranchLengths) {
		this.requiresBranchLengths = requiresBranchLengths;
	}

	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {

		final PalFacade.Tree palT1 = TreeConverter.getPalFrom(t1, requiresBranchLengths);
		final PalFacade.Tree palT2 = TreeConverter.getPalFrom(t2, requiresBranchLengths);
		
		getDistance(palT1, palT2, result, resultString);
	}

	public void initialize(Tree t1, Tree t2) {
	}

	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}
	
	protected abstract void getDistance(PalFacade.Tree t1, PalFacade.Tree t2, MesquiteNumber number, MesquiteString string);
}
