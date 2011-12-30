/*
 * This software is part of the Tree Set Visualization module for Mesquite,
 * 
 * written by Jeff Klingner, Fred Clarke, and Denise Edwards.
 *
 * Copyright (c) 2002 by the University of Texas
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee under the GNU Public License is hereby granted, 
 * provided that this entire notice  is included in all copies of any software 
 * which is or includes a copy or modification of this software and in all copies
 * of the supporting documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR THE UNIVERSITY OF TEXAS
 * AT AUSTIN MAKE ANY REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE 
 * MERCHANTABILITY OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

package mesquite.treecomp.metrics.WeightedRFtreeDifference;

/*~~  */

import java.util.HashMap;

import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.treecomp.common.LrnwStack;
import mesquite.treecomp.common.PSWTree;
import mesquite.treecomp.common.PalFacade;
import mesquite.treecomp.metrics.BaseForTreeCmpMetric;

/**
 * This is a weighted version of RFtreeDifference. In addition to considering
 * which edges are present/absent in each of the two trees, it also uses their
 * weights.
 */

public class WeightedRFtreeDifference extends BaseForTreeCmpMetric {
	private static final int INITIAL_HASHMAP_CAPACITY = 500;

	public String getName() {
		return "Weighted Robinson-Foulds Tree Difference";
	}

	public String getVersion() {
		return "2.1";
	}

	public String getYearReleased() {
		return "2011";
	}

	public boolean showCitation() {
		return true;
	}

	public String getPackageName() {
		return "Tree Comparison Package";
	}

	public boolean getUserChoosable() {
		return false;
	}

	public boolean isPrerelease() {
		return false;
	}

	public boolean isSubstantive() {
		return true;
	}

	public String getCitation() {
		return "\n" + getYearReleased() + ". " + getAuthors() + "\n";
	}

	public String getAuthors() {
		return "Tomasz Tretkowski, Gdansk University of Technology";
	}

	public String getExplanation() {
		return "Calculates the Weighted Robinson-Foulds distance\n"
				+ "between two trees.  This is just like the\n"
				+ "Robinson-Foulds distance, except the difference\n"
				+ "is weighted by the branch lengths of the tree.\n"
				+ "If a very long branch is present in one tree but\n"
				+ "missing from the other, it will contribute more to\n"
				+ "the total difference than similarly placed short\n"
				+ "branch would.  Missing branch lengths in the input\n"
				+ "are treated as having unit length.";
	}

	/**
	 * Called to provoke any necessary initialization. This helps prevent the
	 * module's intialization queries to the user from happening at inopportune
	 * times (e.g., while a long chart calculation is in mid-progress)
	 */
	public void initialize(Tree t1, Tree t2) {
	}

	@Override
	protected void getDistanceForRootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void getDistanceForUnrootedTree(
			mesquite.treecomp.common.PalFacade.Tree t1,
			mesquite.treecomp.common.PalFacade.Tree t2, MesquiteNumber number,
			MesquiteString string) {
		PalFacade.TreeCmpMetric metric = new PalFacade.TreeCmpMetric("treecmp.metric.WRFMetric");
		double distance = metric.getDistance(t1, t2);
		number.setValue(distance);
		if (string != null)
			string.setValue("Weighted Robinson-Foulds tree distance: " + distance);

	}
}