package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.BitSet;

/**
 * Panel in which the point embedding is displayed
 */
public class EmbeddingDisplayPanel extends Panel {
	// Some constants that define the behavior of the embedding panel's user
	// interface
	private final static Color embeddingBackGroundColor = Color.black;
	private final static Color embeddingUnselectedPointColor = Color.white;
	private final static Color embeddingSelectedPointColor = Color.red;
	private final static Color embeddingSelectionBoxColor = Color.white;
	private final static Color embeddingSampledPointColor = Color.blue;

	// Bigger sensitivity means you can clicker farther from a point and still
	// select it.
	private final static int singleClickSensitivity = 4;

	/** Needed for double-buffering (see paint() method) */
	private Image im;
	private Graphics2D buf;

	/** The local, transformed for display, embedding */
	private Point[] localPoints;
	/** number of dimensions in the embedding */
	private int dimensions;
	/** the rectangle that is dragged around points to select them */
	private Box selectionBox;
	/** So that this panel can pass along selection events */
	private SelectionManager selectionManager;
	/** a convenience member used for resetting of selectionSet */
	private BitSet emptySet;
	/** An all-true bitset used for drawing of all points in spite of selection */
	private BitSet fullSet;
	/**
	 * The points selected by the user in a selection event; passed to the
	 * selection manager
	 */
	private BitSet selectionSet;
	/**
	 * a temporary bit vector to keep track of which points aren't in any
	 * selections; they are drawn plainly
	 */
	private BitSet unselectedPoints;
	/**
	 * representation of which points should be drawn differently because they
	 * are in the MDS sample
	 */
	private BitSet sampleSet;
	/** shared data with the mds thread */
	private SharedPoints sharedPoints;
	/** local copy of the current embedding (as set by MDS) */
	private float[][] pointsFromMDS;
	/** Flag for activation of point coloring by optimality */
	private boolean optimalityColoring;
	private Color[] optimalityColors;

	/**
	 * Only points with indices 0-lastShown are drawn. set by animation thread
	 * to cause the sequetial appearance of points
	 */
	private int lastShown;

	public EmbeddingDisplayPanel(SelectionManager selectionManager,
			SharedPoints sharedPoints) {
		super();
		this.selectionManager = selectionManager;
		this.sharedPoints = sharedPoints;
		dimensions = 2;
		localPoints = new Point[sharedPoints.getPoints().length];
		for (int i = 0; i < localPoints.length; i++) {
			localPoints[i] = new Point();
		}
		selectionBox = new Box();
		selectionSet = new BitSet(localPoints.length);
		unselectedPoints = new BitSet(localPoints.length);
		emptySet = new BitSet(localPoints.length);
		sampleSet = new BitSet(localPoints.length); // initially empty
		fullSet = new BitSet(localPoints.length);
		for (int i = 0; i < localPoints.length; i++) {
			fullSet.set(i);
		}
		optimalityColoring = false;
		optimalityColors = null;

		lastShown = localPoints.length - 1;

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Record the press location, in case this is the start of a
				// drag selection
				// This point is also used as the location of a single click if
				// the mouse button
				// come up without any dragging.
				selectionBox.anchor = e.getPoint();
			}

			public void mouseClicked(MouseEvent e) {
				// Select closest point. If no points are nearby, select
				// nothing.
				// If the Ctrl key was also pressed, the selection is cumulative
				selectFromPoint(selectionBox.anchor, e.getModifiers());
			}

			public void mouseReleased(MouseEvent e) {
				// If this was a dragging event, turn off the dragging box and
				// select every point that was in it.
				// If the Ctrl key was also pressed, the selection is cumulative
				// The conditional here is false if the relase event is from a
				// click rather than a drag.
				if (selectionBox.enabled) {
					selectFromBox(selectionBox, e.getModifiers());
				}
				selectionBox.enabled = false;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				// Enter (or remain in) the box selection state. Update the
				// dragging box
				selectionBox.enabled = true;
				selectionBox.floater = e.getPoint();
				repaint();// because the selection box has changed
			}
		});
	}

	public void resetNumberOfItems(int newNumberOfItems) {
		localPoints = new Point[newNumberOfItems];
		for (int i = 0; i < localPoints.length; i++) {
			localPoints[i] = new Point();
		}
		selectionSet = new BitSet(newNumberOfItems);
		unselectedPoints = new BitSet(newNumberOfItems);
		emptySet = new BitSet(newNumberOfItems);
		sampleSet = new BitSet(newNumberOfItems); // initially empty
		fullSet = new BitSet(newNumberOfItems);
		for (int i = 0; i < newNumberOfItems; i++) {
			fullSet.set(i);
		}
		lastShown = newNumberOfItems - 1;
	}

	/**
	 * Override Panel's setBounds so that I can replace the internal drawing
	 * buffer with one of the appropriate dimensions whenever the embedding
	 * panel is re-sized.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		im = null; // setting these to null signals that they need to be
					// reconstructed
		buf = null; // to account for the new panel size
	}

	/**
	 * This method draws the embedding. Colors are defined by static class
	 * variables
	 */
	public void drawEmbedding(Graphics2D g2) {
		// I draw my own background
		g2.setPaint(embeddingBackGroundColor);
		g2.fillRect(0, 0, getBounds().width, getBounds().height);

		// Eventually, I will put the transformation from the point set provided
		// by MDS to
		// the point set of the 2D display, possibly incorporating a viewing
		// angle if the
		// visualization is in 3D. For now, only 2D Display works.

		if (dimensions != 2) {
			g2.setColor(embeddingUnselectedPointColor);
			g2.drawString("Sorry, only 2D drawing is supported for now.", 10,
					10);
		} else {
			// Draw the points
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			MultiSelections selections = selectionManager.getSelections();

			// First, draw all of the selected points, and keep track of which
			// ones never show up
			unselectedPoints.or(fullSet); // at first, no points are known to be
											// selected
			for (int i = 0; i < selections.getNumberOfSelections(); ++i) { // for
																			// each
																			// selection
				drawPointSet(selections.getSelection(i), selections
						.getIconNumber(i), embeddingSelectedPointColor, g2);
				unselectedPoints.andNot(selections.getSelection(i));
			}
			// Next draw all points in the unselected Color
			drawPointSet(unselectedPoints, PointIcons.DOT,
					embeddingUnselectedPointColor, g2);
			// Finally, draw the sample highlights
			drawPointSet(sampleSet, PointIcons.SAMPLE,
					embeddingSampledPointColor, g2);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);

			// Next, draw the dragging box, if it exists
			if (selectionBox.enabled) {
				g2.setColor(embeddingSelectionBoxColor);
				g2.drawLine(selectionBox.anchor.x, selectionBox.anchor.y,
						selectionBox.anchor.x, selectionBox.floater.y);
				g2.drawLine(selectionBox.anchor.x, selectionBox.anchor.y,
						selectionBox.floater.x, selectionBox.anchor.y);
				g2.drawLine(selectionBox.anchor.x, selectionBox.floater.y,
						selectionBox.floater.x, selectionBox.floater.y);
				g2.drawLine(selectionBox.floater.x, selectionBox.anchor.y,
						selectionBox.floater.x, selectionBox.floater.y);
			}
		}
	}

	private final void drawPointSet(BitSet b, int iconNumber, Color c,
			Graphics g) {
		g.setColor(c);
		for (int i = 0; i <= lastShown; ++i) {
			if (b.get(i)) {
				if (optimalityColoring && optimalityColors != null) {
					g.setColor(optimalityColors[i]);
				}
				PointIcons.draw(iconNumber, g, localPoints[i]);
			}
		}
	}

	public void drawInPostscript(java.io.PrintWriter psOutput) {
		/*
		 * the whole page is 612x792 points (72 points per inch). The bounding
		 * box used will be a square 7.5 inches wide, centered on both axes of
		 * the page
		 */
		/* origin of postscript coordinate system is the lower left corner */
		Point lowerLeft = new Point(36, 126); // 0.5 in from right edge, 1.75 in
												// from botton
		Point upperRight = new Point(576, 666); // 0.5 in from left edge, 1.75
												// in from top
		int pointRadius = 2;

		/* Print the header of the postscript file, as given by Dr. Amenta */
		psOutput.println("%!PS-Adobe-2.0 EPSF-1.2");
		psOutput.println("%%BoundingBox: " + lowerLeft.x + " " + lowerLeft.y
				+ " " + upperRight.x + " " + upperRight.y);
		psOutput.println("/inch {72 mul} def");
		psOutput.println("0.008 inch 0.008 inch scale");
		psOutput.println();
		psOutput.println("1 setlinewidth");
		psOutput.println("1 setlinecap");
		psOutput.println("1 setlinejoin");
		psOutput.println();

		Color currentColor;
		for (int i = 0; i < localPoints.length; ++i) {
			/* choose the color */
			if (optimalityColoring && optimalityColors != null) {
				currentColor = optimalityColors[i];
			} else {
				currentColor = Color.black;
			}
			psOutput.println(currentColor.getRed() / 255.0f + " "
					+ currentColor.getGreen() / 255.0f + " "
					+ currentColor.getBlue() / 255.0f + " setrgbcolor");

			/* draw the point */
			psOutput.println("newpath");
			psOutput.println((localPoints[i].x + lowerLeft.x) + " "
					+ (localPoints[i].y + lowerLeft.y) + " " + pointRadius
					+ " 0 360 arc");
			psOutput.println("closepath ");
			psOutput.println("fill");
			psOutput.println();
		}

		/* print the footer of the file */
		psOutput.println("showpage");
	}

	/**
	 * called when the embedding display needs to be repainted; implements
	 * double-buffering.
	 */
	public void paint(Graphics g) {
		// System.out.println("Display thread did a draw.");
		// Access the shared data area to get the latest embedding written by
		// the mds thread
		updatePoints();

		if (im == null) { // true on first run or after a resize
			// Create a buffer for drawing to do double buffering and avoid
			// flicker
			im = createImage(getSize().width, getSize().height);
			buf = (Graphics2D) im.getGraphics();
		}
		// Draw the panel in the internal buffer
		drawEmbedding(buf);
		// Flash it to the screen all at once
		g.drawImage(im, 0, 0, this);
	}

	/**
	 * Trival overriding of Component's update() to eliminate the unnecessary
	 * background wipe and reduce flicker
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/** called by the main window when the sampling set is changed */
	protected void setSample(BitSet sample) {
		this.sampleSet = sample;
	}

	/**
	 * called by the main window when the user activates/deactivates optimality
	 * coloring
	 */
	protected void setPointColoring(boolean b) {
		optimalityColoring = b;
	}

	/**
	 * called the first time optimality coloring is enabled to pass a reference
	 * to the color vector
	 */
	protected void setPointColors(Color[] c) {
		optimalityColors = c;
	}

	/**
	 * This is the accessor used to implement animation. It is called by the
	 * animation thread. Synchronization is not necessary because an
	 * out-of-sync-by-one value for lastShown does not cause an error.
	 */
	protected void setLastShown(int last) {
		lastShown = last;
	}

	/**
	 * Called when the user clicks in the picture without dragging. Selects the
	 * nearest point, or no no points if none are near the click.
	 * selectinBox.anchor was set on the mousePressedEvent.
	 * 
	 * @param p
	 *            click location
	 * @param modifierKeys
	 *            were Control or Shift (or both) pressed?
	 */
	private void selectFromPoint(Point p, int modifierKeys) {
		int closestPoint = -1;// Dummy value, means no nearby point has been
								// found so far
		double closestDistance = 0;
		double currentDistance;
		for (int i = 0; i < localPoints.length; i++) {
			// If the point is within n pixels of the click, check the distance
			if ((Math.abs(p.x - localPoints[i].x) < singleClickSensitivity)
					&& (Math.abs(p.y - localPoints[i].y) < singleClickSensitivity)) {
				currentDistance = Math.sqrt((p.x - localPoints[i].x)
						* (p.x - localPoints[i].x) + (p.y - localPoints[i].y)
						* (p.y - localPoints[i].y));
				if (closestPoint == -1 || currentDistance <= closestDistance) {
					closestPoint = i;
					closestDistance = currentDistance;
				}
			}
		}
		// initialize selection set by setting all bits to false
		selectionSet.and(emptySet);
		if (closestPoint != -1) { // click was close to at least one point
			selectionSet.set(closestPoint);
		} // otherwise, no points at all are in this selection set
		// Pass the selection event to the selection manager
		selectionManager.selectionEvent(selectionSet, modifierKeys);
		repaint();
	}

	/**
	 * Called after a selection box has been dragged. All points inside the box
	 * (edges inclusive) are selected.
	 * 
	 * @param b
	 *            the box made by the user by clicking and dragging
	 */
	private void selectFromBox(Box b, int modifierKeys) {
		// I make a rectangle out of the box here so I can utilize the
		// convenient Rectangle.contains method.
		Rectangle rec = new Rectangle(Math.min(b.anchor.x, b.floater.x), Math
				.min(b.anchor.y, b.floater.y), Math.abs(b.anchor.x
				- b.floater.x), Math.abs(b.anchor.y - b.floater.y));
		// initialize the selection set by clearing all of its bits
		selectionSet.and(emptySet);
		for (int i = 0; i < localPoints.length; i++) {
			// All points within the rectangle are selected
			if (rec.contains(localPoints[i])) {
				selectionSet.set(i);
			}
		}
		// Pass the selection event to the selection manager
		selectionManager.selectionEvent(selectionSet, modifierKeys);
		repaint(); // because the selection box needs to vanish
	}

	/**
	 * Description of the Method
	 */
	private void updatePoints() {
		// Access the shared data area for the points written by the MDS thread
		pointsFromMDS = sharedPoints.getPoints();

		float min_x = pointsFromMDS[0][0];
		float min_y = pointsFromMDS[0][1];
		float max_x = pointsFromMDS[0][0];
		float max_y = pointsFromMDS[0][1];
		// First, pass over the points once to find the minimums and maximums
		// for each dimension.
		for (int i = 0; i < pointsFromMDS.length; i++) {
			if (pointsFromMDS[i][0] < min_x) {
				min_x = pointsFromMDS[i][0];
			}
			if (pointsFromMDS[i][1] < min_y) {
				min_y = pointsFromMDS[i][1];
			}
			if (pointsFromMDS[i][0] > max_x) {
				max_x = pointsFromMDS[i][0];
			}
			if (pointsFromMDS[i][1] > max_y) {
				max_y = pointsFromMDS[i][1];
			}
		}
		// Now to find the embeddings, translate the points so the minimum is at
		// zero, scale them
		// so that the maximum is at the display width (minus margins), and
		// round them to integers
		// To avoid aspect distortion, the smaller of the two scale factors is
		// used for both axes.
		// Points are centered on the larger axis.
		int margin = 5;
		int embeddingWidth = getWidth() - (2 * margin);
		int embeddingHeight = getHeight() - (2 * margin);
		float scale_x = (embeddingWidth) / (max_x - min_x);
		float scale_y = (embeddingHeight) / (max_y - min_y);
		float scale;
		float x_offset;
		float y_offset;
		if (scale_x < scale_y) {
			scale = scale_x;
			x_offset = 0;
			y_offset = (embeddingHeight - (max_y - min_y) * scale_x) / 2;
		} else {
			scale = scale_y;
			x_offset = (embeddingWidth - (max_x - min_x) * scale_y) / 2;
			y_offset = 0;
		}
		for (int i = 0; i < pointsFromMDS.length; i++) {
			localPoints[i].setLocation((margin + Math
					.round(((pointsFromMDS[i][0] - min_x) * scale)
							+ x_offset)), (margin + Math
					.round(((pointsFromMDS[i][1] - min_y) * scale)
							+ y_offset)));
		}
	}

	/**
	 * A small class (like a struct, really) used internally to encapsulate
	 * details of the point-selection box
	 */
	public class Box {
		/** true during dragging */
		boolean enabled;
		/** origin of the box (the location of the mousePressed event) */
		Point anchor;
		/** corner opposite the orgin (locatin of the pointer during dragging */
		Point floater;
	}
}
