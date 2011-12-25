package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.BitSet;

/**
 * A class to encapsulate the key to the optimality coloring feature. It draws a
 * gradient color bar, indexed by scores so that the viewer can associate colors
 * with specific scores. Also, little arrows can be drawn pointing at a spot in
 * the gradient to correspond to selected items.
 * 
 *@author Jeff Klingner
 *@created Spring 2002
 */
public class ColorKey extends Panel {

	// Color constants
	private final static Color textColor = Color.black;
	private final static Color arrowColor = Color.black;

	// How many index labels to write along the length of the color bar
	private final static int numberOfLabels = 6;
	// Endpoints of the key's range
	private double max;
	private double min;

	// Defines the color range across the key
	private ColorGradient colorGradient;
	// values for each item index, used to determint where each arrow lies
	private double[] optimalityScores;
	// reference to the selection manager is used to find which points are in
	// the active selection and what their icon is
	private SelectionManager selectionManager;
	// only arrows with indices <= lastShown will be drawn; -1 means draw them
	// all
	private int lastShown;

	// A whole bunch of constant parameters to determine the appearnce of the
	// key
	private static final int labelMargin = 5;// pixels right of the labels
	private static final double labelAngle = -(Math.PI / 2);// radians clockwise
															// - negative sign
															// makes it
															// counter-clockwise
	private static final int barMargin = 10;// pixels on each side of the color
											// bar
	private static final int arrowMargin = 5;// pixels beside the arrows' bases
	private static final int arrowWidth = 6;// pixels across the base of the
											// arrows
	private static final int iconWidth = 10; // pixels of space given to
												// selection set icons

	// These next three specify division of the space between the margins, thes
	// should add up to one.
	private static final double arrowPortion = 0.3;
	private static final double barPortion = 0.3;
	private static final double labelPortion = 0.40;
	// The totol width of the color key in pixels
	private static final int colorKeyWidth = 90;

	public ColorKey(double[] optimalityScores, ColorGradient colorGradient,
			SelectionManager selectionManager) {
		super();// Panel's constructor
		this.optimalityScores = optimalityScores;
		this.selectionManager = selectionManager;
		max = optimalityScores[0];
		min = optimalityScores[0];
		for (int i = 0; i < optimalityScores.length; ++i) {
			max = Math.max(optimalityScores[i], max);
			min = Math.min(optimalityScores[i], min);
		}
		this.colorGradient = colorGradient;
		lastShown = optimalityScores.length;
	}

	/**
	 * called by the animation thread to effect the sequential appearance of
	 * indicator arrows
	 */
	public void setLastShown(int last) {
		lastShown = last;
	}

	/** establishes a minimum width for proper window component layout */
	public Dimension getPreferredSize() {
		return new Dimension(colorKeyWidth, 0);
	}

	/**
	 * The main event: draws the color bar, the index labels, and the arrows
	 * 
	 * @param g
	 *            graphics context in which to do the drawing. Must be of type
	 *            Graphics2D.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		int width = getBounds().width;
		int height = getBounds().height;
		int x;
		int y;

		// We have three things to draw: the labels, the color bar, and the
		// arrows.

		// Start with the labels
		x = arrowMargin
				+ (int) ((width - labelMargin - arrowMargin) * (arrowPortion + barPortion));
		String currentLabel;
		for (int i = 0; i < numberOfLabels; ++i) {
			y = (height - barMargin)
					- (int) ((height - 2 * barMargin) * ((float) i / (float) (numberOfLabels - 1)));
			currentLabel = Double.toString(min
					+ (i * (max - min) / numberOfLabels));
			g2.drawString(currentLabel, x, y);
		}

		// Second, draw the color bar
		y = height - barMargin;
		// set x to the left edge of the bar
		x = arrowMargin
				+ (int) ((width - labelMargin - arrowMargin) * (arrowPortion));
		while (y >= barMargin) {
			g2.setColor(colorGradient
					.computeColor((double) (height - barMargin - y)
							/ (double) (height - 2 * barMargin)));
			g2
					.drawLine(
							x,
							y,
							x
									+ (int) ((width - labelMargin - arrowMargin) * (barPortion)),
							y);
			--y;
		}

		// Finally, draw the arrows
		// Define the arrow shape
		Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);// apex
		arrow
				.addPoint(
						(int) -(((width - labelMargin - arrowMargin) * (arrowPortion)) - iconWidth),
						arrowWidth / 2);// upper base
		arrow
				.addPoint(
						(int) -(((width - labelMargin - arrowMargin) * (arrowPortion)) - iconWidth),
						-arrowWidth / 2);// lower base
		g2.setPaint(arrowColor);
		// g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_MITER));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
		// RenderingHints.VALUE_STROKE_PURE);

		// set x to the left edge of the bar.
		x = arrowMargin
				+ (int) ((width - labelMargin - arrowMargin) * (arrowPortion));
		double arrowTotal = 0.0;
		int numberOfArrowsDrawn = 0;
		MultiSelections selections = selectionManager.getSelections();
		BitSet arrowSet = selections.getSelection(selections
				.getActiveSelection());
		int iconNumber = selections.getIconNumber(selections
				.getActiveSelection());
		for (int i = 0; i <= lastShown; ++i) {
			if (arrowSet.get(i)) {
				y = (height - barMargin)
						- (int) ((height - 2 * barMargin)
								* (optimalityScores[i] - min) / (max - min));
				arrow.translate(x, y);
				g2.fill(arrow);
				PointIcons.draw(iconNumber, g2, new Point(arrowMargin
						+ iconWidth / 2, y));
				arrow.translate(-x, -y);
				numberOfArrowsDrawn++;
				arrowTotal += optimalityScores[i];
			}
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
		// RenderingHints.VALUE_STROKE_DEFAULT);

		// If any arrows were drawn, draw their average line
		if (numberOfArrowsDrawn > 0) {
			double arrowAverage = arrowTotal / numberOfArrowsDrawn;
			g2.setColor(Color.black);
			y = (height - barMargin)
					- (int) ((height - 2 * barMargin) * (arrowAverage - min) / (max - min));
			x = arrowMargin
					+ (int) ((width - labelMargin - arrowMargin) * (arrowPortion));
			g2
					.drawLine(
							x,
							y,
							x
									+ (int) ((width - labelMargin - arrowMargin) * (barPortion)),
							y);
		}
	}
}