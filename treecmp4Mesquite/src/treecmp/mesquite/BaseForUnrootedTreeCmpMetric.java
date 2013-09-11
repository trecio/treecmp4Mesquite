package treecmp.mesquite;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberFor2Trees;

public abstract class BaseForUnrootedTreeCmpMetric extends NumberFor2Trees {	

	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {

		final PalFacade.Tree palT1 = TreeConverter.getPalFrom(t1);
		final PalFacade.Tree palT2 = TreeConverter.getPalFrom(t2);
		
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
