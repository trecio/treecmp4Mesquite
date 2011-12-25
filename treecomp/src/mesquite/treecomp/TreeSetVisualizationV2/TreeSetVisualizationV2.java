/*
 * This software is part of the Tree Set Visualization module for Mesquite,
 * written by Jeff Klingner, Fred Clarke, and Denise Edwards.
 *
 * Copyright (c) 2002 by the University of Texas
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted under the GNU Lesser General 
 * Public License, as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version, 
 * provided that this entire notice is included in all copies of any 
 * software which are or include a copy or modification of this software
 * and in all copies of the supporting documentation for such software.
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR THE UNIVERSITY OF TEXAS
 * AT AUSTIN MAKE ANY REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE 
 * MERCHANTABILITY OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 * IN NO CASE WILL THESE PARTIES BE LIABLE FOR ANY SPECIAL, INCIDENTAL, 
 * CONSEQUENTIAL, OR OTHER DAMAGES THAT MAY RESULT FROM USE OF THIS SOFTWARE.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

	Last change:  DE   16 Apr 2003   11:18 am
 */

package mesquite.treecomp.TreeSetVisualizationV2;

import mesquite.lib.CommandChecker;
import mesquite.lib.CommandRecord;
import mesquite.lib.MesquiteCommand;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteSubmenuSpec;
import mesquite.lib.MesquiteThread;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Notification;
import mesquite.lib.Snapshot;
import mesquite.lib.Taxa;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.DrawTreeCoordinator;
import mesquite.lib.duties.FileAssistantN;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.lib.duties.TreeSource;

/**
 * === Class TreeSetModule === This is the Wrapper Class for visualizing trees.
 * It handles the hiring and firing of other modules and also menu construction.
 * It includes a TreeSetViz object which has the embedding display panel for the
 * points and the tree display panel. TreeSetViz is a subclass of
 * PointSetVisualization which is an abstract class contains the
 * EmbeddingDisplay and the MDS code/objects.
 * 
 * @authors
 */
public class TreeSetVisualizationV2 extends FileAssistantN {
	/** The main window that handles user interaction, display, and computations */
	TreeSetViz mainWindow;
	/** module that provides trees to this module */
	TreeSource treeSourceTask;
	/** Command to react to menu choice of tree source */
	MesquiteCommand tstC;
	/** String for use in submenu for tree source */
	MesquiteString treeSourceTaskName;
	/** needed to get trees from the tree source */
	Taxa taxa;
	/** module that will handle the drawing of the selected tree(s) */
	DrawTreeCoordinator treeDrawCoordTask;
	/** module that calculates consensus trees */
	Consenser consensusTask;
	/** used in menu construction */
	MesquiteString consensusTaskName;
	/** Command to react to menu choice of consensus calculator */
	MesquiteCommand ctC;
	/** module that calculates inter-tree distances */
	NumberFor2Trees treeDifferenceTask;
	/** String for use in submenu for distance metric */
	MesquiteString treeDifferenceTaskName;
	/** Command to react to menu choice of tree difference metric */
	MesquiteCommand tdC;

	public String getName() {
		return "Tree Set Visualization " + getVersion();
	}

	public String getVersion() {
		return "2.0";
	}

	public String getYearReleased() {
		return "2002";
	}

	public boolean showCitation() {
		return true;
	}

	public String getPackageName() {
		return "Tree Comparison Package";
	}

	public boolean getUserChoosable() {
		return true;
	}

	public boolean isPrerelease() {
		return false;
	}

	public boolean isSubstantive() {
		return false;
	}

	public String getCitation() {
		return "\n" + getYearReleased() + ". " + getAuthors() + "\n";
	}

	public String getAuthors() {
		return "Jeff Klingner, The University of Texas at Austin, with Denise Edwards and Fred Clark, City University of New York";
	}

	public String getExplanation() {
		return "This is a tool for the exploration and understanding of large sets of\n"
				+ "phylogenetic trees.  Load a NEXUS file containing at least one tree\n"
				+ "block and then start up this module to see what it's all about\n"
				+ "For more documentation and explanation, see:\n"
				+ "http://www.cs.utexas.edu/users/jmk/visualization";
	}

	/**
	 * Gathers the state of the module (which other modules it has hired and the
	 * state of the window for saving and restoration.
	 * 
	 * @param file
	 *            Description of Parameter
	 * @return The snapshot value
	 */
	public Snapshot getSnapshot(MesquiteFile file) {
		if (mainWindow == null) {
			return null;
		}
		Snapshot temp = new Snapshot();
		temp.addLine("setTaxa " + getProject().getTaxaReference(taxa));
		temp.addLine("setConsenser ", consensusTask);
		temp.addLine("setTreeDifferenceTask ", treeDifferenceTask);
		temp.addLine("setTreeSource ", treeSourceTask);

		Snapshot fromWindow = mainWindow.getSnapshot(file);
		temp.addLine("makeWindow");
		temp.addLine("tell It");
		temp.incorporate(fromWindow, true);
		temp.addLine("endTell");
		temp.addLine("showWindow");

		return temp;
	}

	/**
	 * Gets the name that should be used for this module's menu item. (the menu
	 * selection that starts up the MDS Module and opens its window).
	 * 
	 * @return The nameForMenuItem value
	 */
	public String getNameForMenuItem() {
		return "Tree Set Visualization " + getVersion();
	}

	/**
	 * Used for initialization instead of the constructor
	 */
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		CommandRecord commandRec = MesquiteThread.getCurrentCommandRecord();

		// All of our menu items and all of the menu items of modules I hire go
		// in this menu.
		makeMenu("Visualization");
		// In case there is more than one taxa block, querey the user to choose
		// which one to use.
		if (!commandRec.recordIsScripting()) {// added by W. Maddison
			taxa = getProject().chooseTaxa(containerOfModule(),
					"Choose the block of taxa to visualize:");
		}

		// ***** Hiring action number one: TreeSource *******//
		// Hire the "Stored Trees" module that will provide the trees to be
		// visualized.
		int numberOfTrees;
		treeSourceTask = (TreeSource) hireEmployee(TreeSource.class,
				"What trees do you want to visualize?");
		if (treeSourceTask == null) {
			return sorry(getName()
					+ " couldn't start because no source of trees obtained.");
		} else {
			numberOfTrees = treeSourceTask.getNumberOfTrees(taxa);
		}
		// This part makes the tree source selectable from a sub-menu, if more
		// than one is available.
		tstC = makeCommand("setTreeSource", this);
		treeSourceTask.setHiringCommand(tstC);
		treeSourceTaskName = new MesquiteString(treeSourceTask.getName());
		if (numModulesAvailable(TreeSource.class) > 1) {
			MesquiteSubmenuSpec mss = addSubmenu(null, "Tree Source", tstC,
					TreeSource.class);
			mss.setList(TreeSource.class);
			mss.setSelected(treeSourceTaskName);
		}

		// ******* Hiring action number two: DrawTreeCoordinator ********//
		// Hire a module to handle the tree drawing. I think that only one of
		// these exists.
		treeDrawCoordTask = (DrawTreeCoordinator) hireEmployee(
				DrawTreeCoordinator.class, "place explanation here");
		if (treeDrawCoordTask == null) {
			return sorry(getName()
					+ " couldn't start because no tree draw coordinating module was obtained.");
		}

		// ****** Hiring action number three: Consensus Tree Calculator
		// ********//
		// Hire a module to compute the consensus trees
		consensusTask = (Consenser) hireEmployee(Consenser.class,
				"Choose the type of consensus trees to use:");
		if (consensusTask == null) {
			return sorry(getName()
					+ " couldn't start because no tree consensus module was obtained.");
		}
		// This part makes the tree consensus calculator selectable from a
		// sub-menu, if more than one is available.
		ctC = makeCommand("setConsenser", this);
		consensusTask.setHiringCommand(ctC);
		consensusTaskName = new MesquiteString(consensusTask.getName());
		if (numModulesAvailable(Consenser.class) > 1) {
			MesquiteSubmenuSpec mss = addSubmenu(null, "Consensus Tree Type",
					ctC, Consenser.class);
			mss.setList(Consenser.class);
			mss.setSelected(consensusTaskName);
		}

		// ****** Hiring decision number four: Tree Distance Metric ********//
		// Hire a module to compute the inter-tree distances
		treeDifferenceTask = (NumberFor2Trees) hireEmployee(
				NumberFor2Trees.class,
				"Choose the tree distance measure you want to use:");
		if (treeDifferenceTask == null) {
			return sorry(getName()
					+ " couldn't start because no tree distance module was obtained.");
		}
		// This part makes the tree distance calculator selectable from a
		// sub-menu, if more than one is available.
		tdC = makeCommand("setTreeDifferenceTask", this);
		treeDifferenceTask.setHiringCommand(tdC);
		treeDifferenceTaskName = new MesquiteString(treeDifferenceTask
				.getName());
		if (numModulesAvailable(NumberFor2Trees.class) > 1) {
			MesquiteSubmenuSpec mss = addSubmenu(null,
					"Tree Difference Metric", tdC, NumberFor2Trees.class);
			mss.setList(NumberFor2Trees.class);
			mss.setSelected(treeDifferenceTaskName);
		}

		// Add a menu item for saving the visualization view as postscript
		addMenuItem("Save as postscript...", makeCommand("saveAsPostscript",
				this));

		// --- edited by W. Maddison
		if (!commandRec.recordIsScripting()) {
			mainWindow = new TreeSetViz(this, treeDrawCoordTask,
					treeSourceTask, consensusTask, treeDifferenceTask, taxa,
					numberOfTrees);
			setModuleWindow(mainWindow);
			mainWindow.copyTreeSelectionFromMesquite();
			mainWindow.setVisible(true);
			resetContainingMenuBar();
			resetAllWindowsMenus();
		} // ---
		return true;
	}// startJob

	/**
	 * Called when this module is fired. Halts running threads and fires
	 * sub-modules.
	 */
	public void endJob() {
		super.endJob();
		if (mainWindow != null) {
			mainWindow.haltThreads();
		}
		mainWindow.removeAllTreeWindows();
		mainWindow.hide();
		mainWindow.dispose();
		closeDownAllEmployees(this);

	}

	/**
	 * Called when someone hits the "go away" button of my window
	 * 
	 * @param whichWindow
	 *            Description of Parameter
	 */
	public void windowGoAway(MesquiteWindow whichWindow) {
		if (whichWindow.equals(mainWindow)) {
			whichWindow.hide();
			whichWindow.dispose();
			iQuit();
		}
	}// windowGoAway

	public Object doCommand(String commandName, String arguments,
			CommandRecord commandRec, CommandChecker checker) {
		// --- W. Maddison
		if (checker.compare(this.getClass(), "Sets the taxa block",
				"[block reference, number, or name]", commandName, "setTaxa")) {
			Taxa t = getProject().getTaxa(checker.getFile(),
					parser.getFirstToken(arguments));
			if (t != null) {
				taxa = t;
				return taxa;
			}
		} else if (checker.compare(this.getClass(),
				"Makes the window (for use when scripting)", null, commandName,
				"makeWindow")) {
			mainWindow = new TreeSetViz(this, treeDrawCoordTask,
					treeSourceTask, consensusTask, treeDifferenceTask, taxa,
					treeSourceTask.getNumberOfTrees(taxa));
			setModuleWindow(mainWindow);
			resetContainingMenuBar();
			resetAllWindowsMenus();
			return mainWindow;

		} else if (checker.compare(this.getClass(),
				"Sets the module that supplies trees",
				"Name of the tree source module", commandName, "setTreeSource")) {
			TreeSource temp = (TreeSource) replaceEmployee(TreeSource.class,
					arguments, "TreeSource", treeSourceTask);
			if (temp != null) {
				treeSourceTask = temp;
				treeSourceTask.setHiringCommand(tstC);
				treeSourceTaskName.setValue(treeSourceTask.getName());
				// Pass along the new consensus calculator to the MDS window
				if (mainWindow != null) {
					mainWindow.setTreeSourceTask(treeSourceTask, taxa);
				}
			}
			return treeSourceTask;
		} else if (checker.compare(this.getClass(),
				"Sets the consensus tree type",
				"Name of the consensus calculating module", commandName,
				"setConsenser")) {
			Consenser temp = (Consenser) replaceEmployee(Consenser.class,
					arguments, "Consensus Tree Calculator", consensusTask);
			if (temp != null) {
				consensusTask = temp;
				consensusTask.setHiringCommand(ctC);
				consensusTaskName.setValue(consensusTask.getName());
				// Pass along the new consensus calculator to the MDS window
				if (mainWindow != null) {
					mainWindow.setConsenser(consensusTask);
				}
			}
			return consensusTask;
		} else if (checker
				.compare(
						this.getClass(),
						"Sets the tree distance metric",
						"Name of the module calculating tree differences\nShould be of the duty class \"NumberFor2Trees\".",
						commandName, "setTreeDifferenceTask")) {
			NumberFor2Trees temp = (NumberFor2Trees) replaceEmployee(
					NumberFor2Trees.class, arguments, "Tree Distance Measure",
					treeDifferenceTask);
			if (temp != null) {
				treeDifferenceTask = temp;
				treeDifferenceTask.setHiringCommand(tdC);
				treeDifferenceTaskName.setValue(treeDifferenceTask.getName());
				// Pass along the new distance metric to the MDS window
				if (mainWindow != null) {
					mainWindow.setTreeDifferenceTask(treeDifferenceTask);
				}
			}
			return treeDifferenceTask;
		} else if (checker.compare(this.getClass(),
				"saves the current visualization view in postscript format",
				null, commandName, "saveAsPostscript")) {
			if (mainWindow != null) {
				mainWindow.saveAsPostscript();
			}
			return null;

		} else { // I don't recognize this command. Pass it on to the command
					// handler in the MesquiteModule superclass.
			return super.doCommand(commandName, arguments, checker);
		}
		// Neither I nor my superclass knows what to do
		return null;
	}// doCommand

	/* Respond correctly to a change of tree block */
	public void employeeParametersChanged(MesquiteModule employee,
			MesquiteModule source, Notification notification,
			CommandRecord commandRec) {
		if (source == treeSourceTask && source.getName().equals("Stored Trees")) {
			if (mainWindow != null) {
				mainWindow.setTreeSourceTask(treeSourceTask, taxa);
			}
		} else {
			super.employeeParametersChanged(employee, source, notification);
		}
	}

}// TreeSetModule
