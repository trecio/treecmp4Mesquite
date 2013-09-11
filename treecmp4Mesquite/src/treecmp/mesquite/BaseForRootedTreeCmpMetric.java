package treecmp.mesquite;

import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberFor2Trees;

public abstract class BaseForRootedTreeCmpMetric extends NumberFor2Trees {

	@Override
	public void initialize(Tree t1, Tree t2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		if (!t1.getRooted() || !t2.getRooted()) {
			throw new IllegalArgumentException("Provided tree is not rooted.");
		}

		final PalFacade.Tree palT1 = TreeConverter.getPalFrom(t1);
		final PalFacade.Tree palT2 = TreeConverter.getPalFrom(t2);

		getDistance(palT1, palT2, result, resultString);
	}

	protected abstract void getDistance(treecmp.mesquite.PalFacade.Tree palT1,
			treecmp.mesquite.PalFacade.Tree palT2, MesquiteNumber result,
			MesquiteString resultString);

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}
}
