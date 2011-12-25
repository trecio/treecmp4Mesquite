package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.Graphics;
import java.awt.Point;

/**
 * This is a class that draws the little shapes that indicate points for each
 * selection. (x's squares, triangles, etc.) It has no constructor and nothing
 * but static methods. Not very object-oriented but the best way I could think
 * of to share the drawing code among the point display, the color key, and the
 * selection manager.
 * 
 * Each littli icon is uniquely identified by an integer. Non-negative integers
 * are used to indicate selections; the first six correspond to actual shapes,
 * and beyond that digits are just printed. Negative numbers are for other
 * stuff, like plain old point with no selection or modifications and sampling.
 * Eventual, this may support zoom-in indication too.
 * 
 * The methods of this class are static and final and fairly simple, so they
 * should be inlined if you compile with optimizations enabled.
 */
public class PointIcons {
	/** The icon used for a plain, unmodified point */
	public static final int DOT = -1;
	/** The icon used for points in the sample (a wide circle) */
	public static final int SAMPLE = -2;

	/**
	 * Draws point n at location p in graphics context g. This is the only
	 * public method of the class. The state of the graphics context is not
	 * changed by draw(). You should pick the color you want before calling
	 * draw(), and turning on antialiasing is recommended for good-looking
	 * icons.
	 */
	public static final void draw(int n, Graphics g, Point p) {
		switch (n) {
		case DOT:
			drawDot(g, p);
			break;
		case SAMPLE:
			drawSampled(g, p);
			break;
		case 0:
			drawPlus(g, p);
			break;
		case 1:
			drawX(g, p);
			break;
		case 2:
			drawCircle(g, p);
			break;
		case 3:
			drawSquare(g, p);
			break;
		case 4:
			drawDiamond(g, p);
			break;
		case 5:
			drawTriangle(g, p);
			break;
		default:
			drawDigit(n, g, p);
			break;
		}
	}

	private static final void drawDot(Graphics g, Point p) {
		int pointSize = 1;// radius of circles drawn to represent the points
		g.fillOval(p.x - pointSize, p.y - pointSize, 2 * pointSize + 1,
				2 * pointSize + 1);
	}

	private static final void drawSampled(Graphics g, Point p) {
		int pointSize = 5;
		g.drawOval(p.x - pointSize, p.y - pointSize, 2 * pointSize + 1,
				2 * pointSize + 1);
	}

	private static final void drawPlus(Graphics g, Point p) {
		/* draw a plus sign */
		g.drawLine(p.x - 3, p.y, p.x + 3, p.y);
		g.drawLine(p.x, p.y + 3, p.x, p.y - 3);
	}

	public static final void drawX(Graphics g, Point p) {
		/* draw an X */
		g.drawLine(p.x - 3, p.y - 3, p.x + 3, p.y + 3);
		g.drawLine(p.x - 3, p.y + 3, p.x + 3, p.y - 3);
	}

	public static final void drawCircle(Graphics g, Point p) {
		/* draw a circle */
		int radius = 3;
		g.drawOval(p.x - radius, p.y - radius, 2 * radius + 1, 2 * radius + 1);
	}

	public static final void drawSquare(Graphics g, Point p) {
		/* draw a square */
		int size = 3;
		g.drawLine(p.x - size, p.y - size, p.x + size, p.y - size);
		g.drawLine(p.x + size, p.y - size, p.x + size, p.y + size);
		g.drawLine(p.x + size, p.y + size, p.x - size, p.y + size);
		g.drawLine(p.x - size, p.y + size, p.x - size, p.y - size);
	}

	public static final void drawDiamond(Graphics g, Point p) {
		int xSize = 4;
		int ySize = 5;
		/* draw a diamond */
		g.drawLine(p.x, p.y - ySize, p.x + xSize, p.y);
		g.drawLine(p.x + xSize, p.y, p.x, p.y + ySize);
		g.drawLine(p.x, p.y + ySize, p.x - xSize, p.y);
		g.drawLine(p.x - xSize, p.y, p.x, p.y - ySize);
	}

	public static final void drawTriangle(Graphics g, Point p) {
		/* draw a triangle */
		g.drawLine(p.x, p.y - 4, p.x + 3, p.y + 4);
		g.drawLine(p.x + 3, p.y + 4, p.x - 3, p.y + 4);
		g.drawLine(p.x - 3, p.y + 4, p.x, p.y - 3);
	}

	public static final void drawDigit(int n, Graphics g, Point p) {
		/* draw the digit n */
		g.drawString(Integer.toString(n), p.x - 3, p.y + 4);
	}
}

