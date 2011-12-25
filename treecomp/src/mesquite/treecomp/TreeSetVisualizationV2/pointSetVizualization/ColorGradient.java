package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.Color;

/**
 * A class to encapsulate the details of computing the color at a given position
 * on a color gradient
 * 
 * @author Jeff Klingner
 * @created Spring 2002
 */
public class ColorGradient {
	private Color[] checkpoints;

	public ColorGradient(Color[] checkpoints) {
		this.checkpoints = new Color[checkpoints.length];
		System.arraycopy(checkpoints, 0, this.checkpoints, 0,
				checkpoints.length);
	}

	/**
	 * The range is indexed by floats in the range 0.0 - 1.0 inclusive. Pass in
	 * a value in that range to get the corresponding color. Anything else will
	 * get you black. The color passed back is constructed by this method.
	 */
	public Color computeColor(double value) {
		Color c;
		if (value >= 0.0 && value <= 1.0 && !Double.isNaN(value)
				&& !Double.isInfinite(value)) {
			int precedingCheckpoint;
			int followingCheckpoint;
			double colorIndex;
			int red;
			int green;
			int blue;
			precedingCheckpoint = (int) Math.round(Math.floor(value
					* (checkpoints.length - 1)));
			followingCheckpoint = precedingCheckpoint + 1;
			if (followingCheckpoint >= checkpoints.length) {// only occurs for
															// 1.0
				precedingCheckpoint--;
				followingCheckpoint--;
			}
			colorIndex = (value * (checkpoints.length - 1) - precedingCheckpoint);
			// System.out.println("value = " + value);
			// System.out.println("preceding checkpoint: " + precedingCheckpoint
			// + "   following = " + followingCheckpoint + "     color index = "
			// + colorIndex);
			red = (int) Math.round((1 - colorIndex)
					* checkpoints[precedingCheckpoint].getRed() + colorIndex
					* checkpoints[followingCheckpoint].getRed());
			green = (int) Math.round((1 - colorIndex)
					* checkpoints[precedingCheckpoint].getGreen() + colorIndex
					* checkpoints[followingCheckpoint].getGreen());
			blue = (int) Math.round((1 - colorIndex)
					* checkpoints[precedingCheckpoint].getBlue() + colorIndex
					* checkpoints[followingCheckpoint].getBlue());
			// System.out.println("red = " + red + ", green = " + green +
			// ", blue = " + blue);
			c = new Color(red, green, blue);
		} else {
			c = new Color(0, 0, 0);
		}
		return c;
	}
}

