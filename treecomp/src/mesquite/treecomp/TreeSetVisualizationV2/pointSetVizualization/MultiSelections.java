package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * An encapsulation of the multiple selections of points that come and go as The
 * user explores the tree set. This is mostly a convenience class to keep
 * everything together. It does not watch its state carefully, and because it
 * exports direct references to its data structures, it is possible for a caller
 * to trash it. As all callers are in the TreeSetModule package, this should be
 * OK.
 */
public class MultiSelections {
	/**
	 * A reference to the main program window, used so that the creation,
	 * updating, and removal of selections can be signalled and action can be
	 * taken (like creating a secondary window with a consensus tree in it.)
	 */
	private PointSetVisualization mainWindow;
	/**
	 * The number of items being visualized. Determines the length of the bit
	 * vectors
	 */
	private int numberOfItems;
	/**
	 * The active selection is the one that is modified by mouse events in the
	 * point display.
	 */
	private int activeSelection;
	/**
	 * dynamically size data structure to hold all of the selection bit vectors
	 * (and associated data)
	 */
	private ArrayList selections;

	/**
	 * A small inner class to encompass all the details of a selection. Used
	 * like a C++ struct
	 */
	private class Selection {
		BitSet bitSet;
		int size;
		int userNumber;
		int iconNumber;
		boolean unused;

		public Selection(BitSet b, int s, int u, int i) {
			this.bitSet = b;
			this.size = s;
			this.userNumber = u;
			this.iconNumber = i;
			this.unused = true;
		}
	}

	/**
	 * Simple constructor that initializes the data structures and creates the
	 * default initial selection. This selection is called "Selection 1", has
	 * icon 0 (a plus sign), and initially contains no points.
	 */
	public MultiSelections(int numberOfItems, PointSetVisualization mainWindow) {
		this.numberOfItems = numberOfItems;
		this.mainWindow = mainWindow;
		selections = new ArrayList(8);
		selections.add(new Selection(new BitSet(numberOfItems), 0, 1, 0));
		activeSelection = 0;
	}

	/*
	 * Here are a bunch of very simple wrapper-style accessor methods. This is
	 * what I meant by saying that the data structures are not well protected.
	 * These methods instead serve to hide the internal organization of the
	 * selections and to spare the caller the hassle (and syntactic mess) of
	 * casting
	 */
	public final int getNumberOfSelections() {
		return selections.size();
	}

	public final int getSelectionSize(int n) {
		return ((Selection) selections.get(n)).size;
	}

	public final BitSet getSelection(int n) {
		return ((Selection) selections.get(n)).bitSet;
	}

	public final int getUserNumber(int n) {
		return ((Selection) selections.get(n)).userNumber;
	}

	public final int getActiveSelection() {
		return activeSelection;
	}

	public final int getIconNumber(int n) {
		return ((Selection) selections.get(n)).iconNumber;
	}

	private final boolean getUnused(int n) {
		return ((Selection) selections.get(n)).unused;
	}

	private final void markUsed(int n) {
		((Selection) selections.get(n)).unused = false;
	}

	public final void setActiveSelection(int n) {
		activeSelection = n;
		mainWindow.activateSelection("Selection " + getUserNumber(n));
		mainWindow.toFront();
	}

	/**
	 * This method is called to modify an existing selection. That selection is
	 * updated internally and the call to the main window is made in order to
	 * induce the creation of tree windows.
	 */
	public final void setSelection(int n, BitSet s) {
		if (getUnused(n)) { // this is a virgin selection, so we will make a
							// tree window for it
			mainWindow.newSelection((BitSet) s.clone(), "Selection "
					+ getUserNumber(n));
			markUsed(n);
		} else { // a tree window already exists; change it
			mainWindow.updateSelection((BitSet) s.clone(), "Selection "
					+ getUserNumber(n));
		}
		((Selection) selections.get(n)).bitSet = (BitSet) s.clone();
		((Selection) selections.get(n)).size = countBits(s);
	}

	/**
	 * Creates a new empty selection. It picks an unused point icon if one
	 * exists. (This method depends on the fact that there are 6 icons that are
	 * worth recycling. (recycling = reused after being part of a selection that
	 * was deleted) If that changes, you'll have to fix it.
	 */
	public void addSelection() {
		// Pick an icon number -- the lowest not already in use
		int newUserNumber;
		if (getNumberOfSelections() > 0) {
			newUserNumber = getUserNumber(getNumberOfSelections() - 1) + 1;
		} else {
			newUserNumber = 1;
		}
		int newIconNumber = -1; // -1 is a dummy value that indicates unassigned
		boolean taken;
		for (int i = 0; i < 6; ++i) { // for each icon from smallest to largest
			taken = false;
			for (int j = 0; j < getNumberOfSelections(); ++j) { // check all
																// selections to
																// see if it is
																// in use
				if (getIconNumber(j) == i) {
					taken = true;
					break; // don't check any more selections
				}
			}
			if (!taken) {
				newIconNumber = i;
				break; // don't check any more icon numbers
			}
		}
		if (newIconNumber == -1) { // all icons taken; use a digit instead
			newIconNumber = newUserNumber;
		}
		selections.add(new Selection(new BitSet(numberOfItems), 0,
				newUserNumber, newIconNumber));
		activeSelection = getNumberOfSelections() - 1;
	}

	/**
	 * Removes a selection permanently. If it used an interesting icon (numbers
	 * 0-5) that icon may be recycled in the future, but for a selection with a
	 * different user number. Together with addSelection and setSelection, this
	 * method uses the three abstract methods in PSV to effect the module-level
	 * response to selection changes. In the case of tree set viz, that means
	 * making a window to display the consensus tree of the trees in the
	 * selection.
	 */
	public void removeSelection() {
		if (!getUnused(activeSelection)) { // a tree window was made, get rid of
											// it
			mainWindow.removeSelection("Selection "
					+ getUserNumber(activeSelection));
		}
		selections.remove(activeSelection);
		if (activeSelection >= getNumberOfSelections()) {
			activeSelection--;

		}

	}

	public void removeAllSelections() {
		for (int i = 0; i <= getNumberOfSelections() - 1; i++) {
			removeSelection();
		}
	}

	/**
	 * Counts the number of bits that are true in the BitSet b. Used internally
	 * to determine the size of changed selection sets
	 */
	private static int countBits(BitSet b) {
		int count = 0;
		for (int i = 0; i < b.length(); ++i) {
			if (b.get(i)) {
				++count;
			}
		}
		return count;
	}
}

