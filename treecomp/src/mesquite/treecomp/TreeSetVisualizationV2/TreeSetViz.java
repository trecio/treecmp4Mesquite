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
 */

package mesquite.treecomp.TreeSetVisualizationV2;

import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import mesquite.lib.CommandChecker;
import mesquite.lib.CommandRecord;
import mesquite.lib.Listened;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteMenuItemSpec;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteTree;
import mesquite.lib.Notification;
import mesquite.lib.ProgressIndicator;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.TreeDisplay;
import mesquite.lib.TreeVector;
import mesquite.lib.duties.Consenser;
import mesquite.lib.duties.DrawTree;
import mesquite.lib.duties.DrawTreeCoordinator;
import mesquite.lib.duties.NumberFor2Trees;
import mesquite.lib.duties.TreeSource;
import mesquite.treecomp.TreeSetVisualizationV2.clustering.ClusteringAlgorithmFactory;
import mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization.MultiSelections;
import mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization.PointSetVisualization;

/**
 * Class TreeSetViz is a subclass derived from PointSetVisualization(PSV) which
 * displays <em>Point Sets</em>. Note the PSV extends MesquiteWindow
 * 
 * @version 1.0, 03/06/02
 * @authors J. Klingner, F. Clarke, S. Neris, D. Edwards Last change: DE 05 June
 *          2002 2:25 pm
 */

public class TreeSetViz extends PointSetVisualization implements
		MesquiteListener {

	/*
	 * TreeSetViz inherits the embedding display panel. The PSV originally has
	 * only that display panel added to its window. When TSV takes it, it will
	 * have to expand the window and add the right panel to it. The right panel
	 * will display the trees.
	 */
	
	private CommandRecord commandRec;
	private MesquiteProject project;
	private Taxa taxa;
	private TreeSource treeSourceTask;// module providing the trees
	private Consenser consensusTask; // module computing the consensus trees
	private DrawTreeCoordinator treeDrawCoordTask; // module drawing the tree in
													// the right pane
	private NumberFor2Trees treeDifferenceTask; // module computing inter-tree
												// distances
	private TreeDisplay treeDisplay;// the right-hand pane, where
									// treeDrawCoordTask will do its thing
	private ScrollPane treeDisplayHolder;// holder to encompas the treeDisplay
	private TextField treeDescriptionField;// a line of text above the drawn
											// tree describing it
	private MesquiteTree currentTree;// the tree that is currenty being drawn in
										// the right pane
	private Panel rightPanel;
	private MesquiteMenuItemSpec numTreesItem;
	private int numberOfTrees;

	private Map treeWindowMap;
	private TreeDisplay[] treeDisplayPool;
	private int treeDisplayPoolSize = 100;
	private int nextAvailableTreeDisplay;
	private static int treeWindowWidth = 400;
	private static int treeWindowHeight = 600;
	private static int treeWindowXLocation = 550;
	private static int treeWindowYLocation = 50;

	public TreeSetViz(MesquiteModule ownerModule,
			DrawTreeCoordinator treeDrawCoordTask, TreeSource treeSourceTask,
			Consenser consensusTask, NumberFor2Trees treeDifferenceTask,
			Taxa taxa, int numberOfTrees) {
		super(ownerModule, 
				numberOfTrees, 
				new ClusteringAlgorithmFactory(treeDifferenceTask, treeSourceTask, taxa));
		project = ownerModule.getProject();
		this.consensusTask = consensusTask;
		setConsenser(consensusTask);
		this.treeDrawCoordTask = treeDrawCoordTask;
		this.treeSourceTask = treeSourceTask;
		treeSourceTask.initialize(taxa);
		this.commandRec = commandRec;
		this.taxa = taxa;
		this.numberOfTrees = numberOfTrees;
		setTreeDifferenceTask(treeDifferenceTask);

		/*
		 * set myself up as a listener in order to synchronize my first
		 * selection with Mesquite's general selection facility.
		 */
		if (treeSourceTask.getSelectionable() != null) {
			treeSourceTask.getSelectionable().addListener(this);
		}

		treeWindowMap = new HashMap(); // efficient map implementation; sorting
										// is not needed
		treeDisplayPool = treeDrawCoordTask.createTreeDisplays(
				treeDisplayPoolSize, taxa, this);
		nextAvailableTreeDisplay = 0;
		// attempt to command the tree drawing module to make it's lines
		// narrower.
		((DrawTree) treeDrawCoordTask.doCommand("getTreeDrawer", null,
				new CommandChecker())).doCommand("setEdgeWidth", "4",
				new CommandChecker());
		((DrawTree) treeDrawCoordTask.doCommand("getTreeDrawer", null,
				new CommandChecker())).doCommand("orientRight", null,
				new CommandChecker());

		// Set it up so that if the main window goes away, so do all the tree
		// windows
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				java.util.Iterator iter = treeWindowMap.keySet().iterator();
				String selectName = "";
				while (iter.hasNext()) {
					selectName = (String) iter.next();
					((ConsensusTreeWindow) treeWindowMap.get(selectName))
							.hide();
					((ConsensusTreeWindow) treeWindowMap.get(iter
							.next())).dispose();
					treeWindowMap.remove(selectName);
				}
			}
		});

		toFront();
		repaintAll();
	}// constructor

	/*
	 * Mesquite was designed with the notion that a module can only have one
	 * window. For this module, TreeSetViz (the main window with the embedding
	 * display) is that window. But there are other windows too: the small
	 * windows in which trees are displayed. When a module's employee's employee
	 * is changed out, for example by the tree form of the tree display
	 * changing, Mesquite will dutifully propogate any menu changes through to
	 * that module's window. Since I can't tell mesquite about my other windows,
	 * I override the resetMenus() method to propagate the change to them
	 * myself.
	 */
	public void resetMenus() {
		// reset the main window's menus as usual
		super.resetMenus();
		// but also reset the menus of the tree windows
		java.util.Iterator iter = treeWindowMap.keySet().iterator();
		while (iter.hasNext()) {
			((ConsensusTreeWindow) treeWindowMap.get(iter.next()))
					.resetMenus();
		}
	}

	/*
	 * used during shutdown to remove and clean up all the consensus tree
	 * windows
	 */
	public void removeAllTreeWindows() {
		java.util.Iterator iter = treeWindowMap.keySet().iterator();
		while (iter.hasNext()) {
			((ConsensusTreeWindow) treeWindowMap.get(iter.next()))
					.dispose();
		}
	}

	/**
	 * Called by controling module when the consensus calculator has changed
	 * (usually in response to a menu selection by the user.
	 * 
	 * @param consensusTask
	 *            The new consenser value
	 */
	public void setConsenser(Consenser consensusTask) {
		if (consensusTask != null) {
			this.consensusTask = consensusTask;
			if (treeWindowMap != null) { // so we don't do this on startup
				MultiSelections ms = selectionManager.getSelections();
				for (int i = 0; i < ms.getNumberOfSelections(); ++i) {
					updateSelection(ms.getSelection(i), "Selection "
							+ ms.getUserNumber(i));
				}
			}
		}
	}

	/**
	 * Sets which module is used to calculate inter tree distances. Induces a
	 * recalculation of the difference matrix.
	 * 
	 * @param treeDifferenceTask
	 *            The new treeDifferenceTask value
	 */
	public void setTreeDifferenceTask(NumberFor2Trees treeDifferenceTask) {
		if (treeDifferenceTask != null) {
			this.treeDifferenceTask = treeDifferenceTask;
			computeDM();
		}
	}// setTreeDifferenceTask

	public void setTreeSourceTask(TreeSource treeSourceTask, Taxa taxa) {
		this.treeSourceTask = treeSourceTask;
		treeSourceTask.initialize(taxa);

		/*
		 * set myself up as a listener in order to synchronize my first
		 * selection with Mesquite's / general selection facility.
		 */
		if (treeSourceTask.getSelectionable() != null) {
			treeSourceTask.getSelectionable().addListener(this);
		}

		// Figure out how many trees are available from the new source
		int reportedTreesAvailable = treeSourceTask.getNumberOfTrees(taxa);
		int newNumberOfTrees;
		final int DEFAULT_NUMBER_OF_TREES = 100;
		if (reportedTreesAvailable == MesquiteInteger.infinite) {
			newNumberOfTrees = MesquiteInteger.queryInteger(this,
					"How many trees do you want to use from "
							+ treeSourceTask.getName() + "?",
					"Number of Trees:", DEFAULT_NUMBER_OF_TREES);
		} else {
			newNumberOfTrees = reportedTreesAvailable;
		}

		if (newNumberOfTrees != numberOfTrees) {
			resetNumberOfItems(newNumberOfTrees);
			numberOfTrees = newNumberOfTrees;
		}
		computeDM();
	}

	/**
	 * computes the contents of the big difference matrix
	 * 
	 * @return true if the calculation was completed, false if it was cancelled
	 *         by the user before completion.
	 */
	public boolean computeDM() {
		int numberOfTrees = getNumberOfTrees();
		Tree tree1;
		Tree tree2;
		MesquiteNumber result = new MesquiteNumber();
		int totalToDo = ((numberOfTrees * numberOfTrees - numberOfTrees) / 2);
		int onePercent = totalToDo / 100;
		if (onePercent == 0) {
			onePercent = 1;
		}
		ProgressIndicator progressMeter = new ProgressIndicator(project,
				"Calculating Tree Differences", totalToDo, true);
		progressMeter.start();
		String progressString;
		int numberOfDistancesCalculated = 0;

		boolean sawLengthlessTree = false;
		for (int i = 0; i < numberOfTrees; ++i) {
			tree1 = treeSourceTask.getTree(taxa, i);
			for (int j = 0; j < i; j++) {
				tree2 = treeSourceTask.getTree(taxa, j);
				treeDifferenceTask.calculateNumber(tree1, tree2, result, null);
				// Diagnostic echo for checking distance calculators
				// System.out.println("d(" + i + "," + j + ") = " +
				// result.toString());
				itemDiffMatrix
						.setElement(i, j, (float) result.getDoubleValue());
				numberOfDistancesCalculated++;
				if (numberOfDistancesCalculated % onePercent == 0) {
					progressString = "Computed " + numberOfDistancesCalculated
							+ "/" + totalToDo + " differences ("
							+ ((numberOfDistancesCalculated * 100) / totalToDo)
							+ "%)";
					progressMeter.setCurrentAndText(
							numberOfDistancesCalculated, progressString);
					// progressMeter.setCurrentValue(numberOfDistancesCalculated);
				}
				if (progressMeter.isAborted()) {
					progressMeter.goAway();
					return false;// Calculation aborted
				}
			}
			if (treeDifferenceTask.getName().equals(
					"Weighted Robinson-Foulds Tree Difference")
					&& !((MesquiteTree) tree1).allLengthsAssigned()) {
				sawLengthlessTree = true;
			}
		}
		if (sawLengthlessTree) {
			System.out
					.println("Warning: At least one tree has an unassigned branch length.");
			System.out
					.println("Unassigned branch lengths are treated as having unit length.");
		}

		progressMeter.goAway();
		return true;// Calculation completed
	}// computeDM

	private int getNumberOfTrees() {
		return numberOfTrees;
	}

	private MesquiteTree computeConsensusTree(BitSet b) {
		TreeVector selectedTrees;
		int selectionCount = 0;
		if (b.length() == 0) {
			// no tree selected
			return null;
		} else {
			selectedTrees = new TreeVector(taxa);
			selectedTrees.removeAllElements(true);
			for (int i = 0; i < b.length(); ++i) {
				if (b.get(i)) {
					selectedTrees.addElement(treeSourceTask.getTree(taxa, i),
							true);
					++selectionCount;
				}
			}
			if (selectionCount == 1) {
				MesquiteTree tempTree = selectedTrees.getTree(0).cloneTree();
				// tempTree.standardize(tempTree.getRoot(),false);
				tempTree.setName(selectedTrees.getTree(0).getName());
				return tempTree;
			} else {
				MesquiteTree tempTree = (MesquiteTree) consensusTask
						.consense(selectedTrees);
				tempTree.setName(consensusTask.getName() + " of "
						+ selectionCount + " selected trees");
				return tempTree;
			}
		}
	}

	/** Description of the Method */
	public void resetTitle() {// the only abstract method in MesquiteWindow
		setTitle(getOwnerModule().getName());
	}// resetTitle

	public void copyTreeSelectionFromMesquite() {
		TreeVector trees = (TreeVector) treeSourceTask.getSelectionable();
		if (trees != null) {
			java.util.BitSet mesquiteSelection = new java.util.BitSet(
					numberOfTrees);
			for (int i = 0; i < numberOfTrees; ++i) {
				if (trees.getSelected(i)) {
					mesquiteSelection.set(i);
				}
			}
			selectionManager.getSelections().setActiveSelection(0);
			selectionManager.selectionEvent(mesquiteSelection, 0); // zero means
																	// no
																	// modifier
																	// keys
		}
	}

	protected void newSelection(java.util.BitSet selection, String selectionName) {
		int newTreeDisplayIndex = nextAvailableTreeDisplay++;
		TreeDisplay newTreeDisplay = treeDisplayPool[newTreeDisplayIndex];
		ConsensusTreeWindow newWindow = new ConsensusTreeWindow(newTreeDisplay,
				newTreeDisplayIndex, selectionName, getOwnerModule(),
				selectionManager);
		treeWindowMap.put(selectionName, newWindow);
		newWindow.setWindowSize(treeWindowWidth, treeWindowHeight);
		newWindow.setLocation(treeWindowXLocation + 20 * newTreeDisplayIndex,
				treeWindowYLocation + 20 * newTreeDisplayIndex);
		newWindow.show();
		newWindow.resetTitle();
		newWindow.resetMenus();
		updateSelection(selection, selectionName);
		newWindow.getScrollPane().getHAdjustable().setValue(
				newWindow.getScrollPane().getHAdjustable().getMaximum());
	}

	protected void updateSelection(java.util.BitSet selection,
			String selectionName) {
		if (treeWindowMap.containsKey(selectionName)) {
			ConsensusTreeWindow window = (ConsensusTreeWindow) treeWindowMap
					.get(selectionName);
			MesquiteTree tree = computeConsensusTree(selection);

			// temp
			/**
			 * if (tree != null) { System.out.println("\n about to draw: " +
			 * tree.writeTree(MesquiteTree.BY_NAMES)); }
			 */

			int treeWindowIndex = window.getTreeDisplayIndex();

			treeDisplayPool[treeWindowIndex].setTree(tree);
			window.updateTreeDisplay();
			if (!window.isVisible()) {
				window.setVisible(true);
			}
			toFront();

			// The first selection is synchronized with Mesquite's seleciton
			// facility; pass the changes out.
			if (selectionName.equals("Selection 1")) {
				TreeVector trees = (TreeVector) treeSourceTask
						.getSelectionable();
				if (trees != null) {
					trees.deselectAll();
					for (int i = 0; i < numberOfTrees; ++i) {
						trees.setSelected(i, selection.get(i));
					}
					int[] notificationParameters = null;
					((Listened) trees).notifyListeners(this,
							new Notification(
									MesquiteListener.SELECTION_CHANGED,
									notificationParameters));
				}
			}
		} else {
			// System.out.println("Warning: tried to update the display of a non-existent tree window: "
			// + selectionName);
		}
	}

	protected void removeSelection(String selectionName) {
		if (treeWindowMap.containsKey(selectionName)) {
			((ConsensusTreeWindow) treeWindowMap.get(selectionName)).dispose();
			treeWindowMap.remove(selectionName);
		} else {
			// System.out.println("Warning: tried to remove a non-existent tree window: "
			// + selectionName);
		}
	}

	protected void activateSelection(String selectionName) {
		if (treeWindowMap.containsKey(selectionName)) {
			ConsensusTreeWindow activatedWindow = (ConsensusTreeWindow) treeWindowMap
					.get(selectionName);
			if (!activatedWindow.isVisible()) {
				activatedWindow.setVisible(true);
			}
			activatedWindow.toFront();
		} else {
			// System.out.println("Warning: tried to activate a non-existent tree window: "
			// + selectionName);
		}
	}

	/*
	 * These three methods implement the MesquiteListener interface, which I do
	 * in order to synchronize my first selection with Mesquite's selection
	 * facility.
	 */
	public void changed(Object caller, Object obj, int code, int[] parameters,
			CommandRecord commandRec) {
		// I only care if the thing that changed is the tree vector of my tree
		// source
		if (obj != null && // something changed
				obj == treeSourceTask.getSelectionable() && // it was my tree
															// source's tree
															// vector
				code == MesquiteListener.SELECTION_CHANGED && // the selection
																// is what
																// changed about
																// it
				caller != this) { // I didn't do the changing myself
			copyTreeSelectionFromMesquite();
		}
	}

	public void disposing(Object obj) {
	}

	public boolean okToDispose(Object obj, int queryUser) {
		return true;
	}
}