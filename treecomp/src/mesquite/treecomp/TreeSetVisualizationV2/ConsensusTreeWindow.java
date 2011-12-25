package mesquite.treecomp.TreeSetVisualizationV2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;

import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Tree;
import mesquite.lib.TreeDisplay;
import mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization.SelectionManager;

public class ConsensusTreeWindow extends MesquiteWindow {
	TreeDisplay consensusTreeDisplay;
	int treeDisplayIndex;
	ScrollPane scrollArea;
	Panel overallPanel;
	Label treeNameLabel;
	String selectionName;
	boolean closingTheWindow = false;
	SelectionManager selectionManager;

	/*
	 * Code change here. A new parameter is added to consensusTreeWindow that is
	 * used to simulate theactions of pressing the "remove selection" button.
	 * The parameter is TreeSetViz's SelectionManager
	 */
	public ConsensusTreeWindow(TreeDisplay newTreeDisplay,
			int newTreeDisplayIndex, String selectionName,
			MesquiteModule ownerModule, SelectionManager selectionManager) {
		super(ownerModule, false);
		this.selectionName = selectionName;
		treeDisplayIndex = newTreeDisplayIndex;
		consensusTreeDisplay = newTreeDisplay;
		scrollArea = new ScrollPane();
		scrollArea.add(consensusTreeDisplay);
		scrollArea.getHAdjustable().setUnitIncrement(10);
		scrollArea.getVAdjustable().setUnitIncrement(10);
		treeNameLabel = new Label();
		Panel treeLabelPanel = new Panel();
		treeLabelPanel.add(treeNameLabel);
		overallPanel = new Panel(new BorderLayout());
		overallPanel.add(scrollArea, BorderLayout.CENTER);
		overallPanel.add(treeLabelPanel, BorderLayout.NORTH);
		addToWindow(overallPanel);
		this.selectionManager = selectionManager;
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				testSimulation();
				setVisible(false);
			}
		});
	}

	public void resetTitle() {
		setTitle(selectionName);
	}

	public void testSimulation() {
		selectionManager.simulateRemoveButton();
	}

	public int getTreeDisplayIndex() {
		return treeDisplayIndex;
	}

	public ScrollPane getScrollPane() {
		return scrollArea;
	};

	public void updateTreeDisplay() {
		Tree tree = consensusTreeDisplay.getTree();
		if (tree != null) {
			int tree_display_width = Math.max(tree.getTaxa().getNumTaxa() * 8,
					scrollArea.getWidth());
			int tree_display_height = Math.max(
					tree.getTaxa().getNumTaxa() * 12, scrollArea.getHeight());
			consensusTreeDisplay.setFieldSize(tree_display_width,
					tree_display_height);
			consensusTreeDisplay.setSize(tree_display_width,
					tree_display_height);
			consensusTreeDisplay.suppressDrawing(false);
			consensusTreeDisplay.setVisible(true);
			consensusTreeDisplay.repaint();
			treeNameLabel.setText(tree.getName());
		} else {
			/* a null tree means there are no trees in the selection. */
			consensusTreeDisplay.suppressDrawing(true);
			treeNameLabel.setText("No trees selected.");
			consensusTreeDisplay.repaint();
		}
		organizeDisplay();
	}

	private void organizeDisplay() {
		overallPanel.setBounds(0, 0, getWidth(), getHeight());
		overallPanel.validate();
		// list();
	}

	public void windowResized() {
		organizeDisplay();
	}

	public Dimension getMinimumSize() {
		return new Dimension(scrollArea.getVScrollbarWidth() + 10, scrollArea
				.getHScrollbarHeight() + 10);
	}
}