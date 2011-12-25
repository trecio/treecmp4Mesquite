package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.BitSet;

public class SelectionManager extends Panel {
	private MultiSelections ms;
	private EmbeddingDisplayPanel embeddingDisplay;
	private ColorKey colorKey;
	private Component selectionDisplay;
	private ScrollPane selectionPane;
	private static final int selectionBoxWidth = 130;
	private static final int selectionBoxHeight = 15;

	public SelectionManager(int numberOfPoints, PointSetVisualization mainWindow) {
		ms = new MultiSelections(numberOfPoints, mainWindow);

		selectionDisplay = new Component() {
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				for (int i = 0; i < ms.getNumberOfSelections(); ++i) {
					drawSelection(i == ms.getActiveSelection(), ms
							.getIconNumber(i), ms.getUserNumber(i), ms
							.getSelectionSize(i), 1 + (i % 2)
							* (selectionBoxWidth + 2), 1 + (i / 2)
							* (selectionBoxHeight + 2), g2);
				}
			}

			public Dimension getPreferredSize() {
				return new Dimension(4 + 2 * selectionBoxWidth, 1
						+ (selectionBoxHeight + 2)
						* ((ms.getNumberOfSelections() + 1) / 2));
			}
		};
		/*
		 * selectionDisplay.addMouseListener(new MouseAdapter() { public void
		 * mouseClicked(MouseEvent e) { System.out.println("Click at " +
		 * e.getPoint()); } public void mousePressed(MouseEvent e) {
		 * System.out.println("Press at " + e.getPoint()); } public void
		 * mouseReleased(MouseEvent e) { System.out.println("Release at " +
		 * e.getPoint()); } });
		 */
		selectionDisplay.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				// Check if the click happened in a selection box
				Rectangle r = new Rectangle();
				r.width = selectionBoxWidth;
				r.height = selectionBoxHeight;
				for (int i = 0; i < ms.getNumberOfSelections(); ++i) {
					r.x = 1 + (i % 2) * (selectionBoxWidth + 2);
					r.y = 1 + (i / 2) * (selectionBoxHeight + 2);
					if (r.contains(e.getPoint())) {
						ms.setActiveSelection(i);
						selectionDisplay.repaint();
						if (colorKey != null) {
							colorKey.repaint();
						}
						return;
					}
				}
			}
		});

		selectionPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED) {
			public Dimension getPreferredSize() {
				Dimension d = selectionDisplay.getPreferredSize();
				d.width += getVScrollbarWidth();
				d.height = 1 + (selectionBoxHeight + 2) * 3;
				return new Dimension(d.width, d.height);
			}
		};

		Button addSelectionButton = new Button("New Selection");
		addSelectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ms.addSelection();
				selectionDisplay.invalidate(); // Preferred size may have
												// changed
				selectionDisplay.repaint();
				selectionPane.validate(); // create scrollbars if needed
				selectionPane.repaint();
				if (colorKey != null) {
					colorKey.repaint();
				}
			}
		});
		Button removeSelectionButton = new Button("Remove Selection");
		removeSelectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ms.getNumberOfSelections() > 1) {
					ms.removeSelection();
					selectionDisplay.invalidate(); // size may be different
					selectionDisplay.repaint();
					selectionPane.validate(); // create scrollbars if needed
					selectionPane.repaint();
				} else if (ms.getNumberOfSelections() == 1) {
					// remove all points from the first selection by removing it
					// and adding a new one.
					ms.removeSelection();
					// at this point, MS is in an inconsistant state
					ms.addSelection();
					// sanity is restored
				}
				selectionDisplay.repaint();
				embeddingDisplay.repaint();
				if (colorKey != null) {
					colorKey.repaint(); // because the active selection may have
										// changed.
				}
			}
		});

		selectionPane.getVAdjustable().setUnitIncrement(selectionBoxHeight + 2);
		selectionPane.add(selectionDisplay);

		Panel buttonPanel = new Panel(new GridLayout(2, 1));
		buttonPanel.add(addSelectionButton);
		buttonPanel.add(removeSelectionButton);
		Panel fullPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		fullPanel.add(buttonPanel);
		fullPanel.add(selectionPane);

		setLayout(new GridLayout(1, 1));
		add(fullPanel);
	}

	public void simulateRemoveButton() {
		if (ms.getNumberOfSelections() > 1) {
			ms.removeSelection();
			selectionDisplay.invalidate(); // size may be different
			selectionDisplay.repaint();
			selectionPane.validate(); // create scrollbars if needed
			selectionPane.repaint();
		} else if (ms.getNumberOfSelections() == 1) {
			// remove all points from the first selection by removing it and
			// adding a new one.
			ms.removeSelection();
			// at this point, MS is in an inconsistant state
			ms.addSelection();
			// sanity is restored
		}
		selectionDisplay.repaint();
		embeddingDisplay.repaint();
		if (colorKey != null) {
			colorKey.repaint(); // because the active selection may have
								// changed.
		}
	}// simulateRemoveButton

	public void resetNumberOfItems(int newNumberOfItems,
			PointSetVisualization mainWindow) {
		for (int i = 0; i < ms.getNumberOfSelections(); ++i) {
			ms.removeSelection();
		}
		ms = new MultiSelections(newNumberOfItems, mainWindow);
	}

	public void setEmbeddingDisplay(EmbeddingDisplayPanel ed) {
		this.embeddingDisplay = ed;
	}

	public void setColorKey(ColorKey ck) {
		this.colorKey = ck;
	}

	public MultiSelections getSelections() {
		return ms;
	}

	public void selectionEvent(BitSet selectionSet, int modifierKeys) {
		boolean withCtrl = (modifierKeys & InputEvent.CTRL_MASK) != 0;
		boolean withShift = (modifierKeys & InputEvent.SHIFT_MASK) != 0;
		// control takes precedence over shift
		if (withCtrl) { // add to the selection
			selectionSet.or(ms.getSelection(ms.getActiveSelection()));
		} else if (withShift) { // toggle selection
			selectionSet.xor(ms.getSelection(ms.getActiveSelection()));
		}
		if (!selectionSet.equals(ms.getSelection(ms.getActiveSelection()))) { // the
																				// selection
																				// has
																				// changed
			// update selection data structure
			ms.setSelection(ms.getActiveSelection(), selectionSet);
			// repaint self (probably a new number of points in the active
			// selection)
			selectionDisplay.repaint();
			// induce repaint on the embedding display because selection has
			// changed
			embeddingDisplay.repaint();
			// likewise for the colorKey, but only if it exists
			if (colorKey != null) {
				colorKey.repaint();
			}
		}
	}// selectionEvent

	private void drawSelection(boolean active, int iconNumber, int userNumber,
			int numberOfPoints, int x, int y, Graphics2D g2) {
		g2.setColor(Color.white);
		g2
				.fillRect(x + 1, y + 1, selectionBoxWidth - 1,
						selectionBoxHeight - 1);
		g2.setColor(Color.black);
		g2.drawRect(x, y, selectionBoxWidth, selectionBoxHeight);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		PointIcons.draw(iconNumber, g2, new Point(x + 9, y + selectionBoxHeight
				/ 2));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		String selectionString;
		if (numberOfPoints == 1) {
			selectionString = "Selection " + userNumber + ": 1 point";
		} else {
			selectionString = "Selection " + userNumber + ": " + numberOfPoints
					+ " points";
		}
		g2.drawString(selectionString, x + 19, y + selectionBoxHeight / 2 + 4);
		if (active) {
			g2.setXORMode(Color.white);
			g2.fillRect(x + 1, y + 1, selectionBoxWidth - 1,
					selectionBoxHeight - 1);
			g2.setPaintMode();
		}
	}
}
