package mesquite.treecomp.TreeSetVisualizationV2;

/** A simple difference matrix, implemented as a 2D triangular array */
public class DiffMatrix {

	/** The triangular array to hold differences between items */
	private float[][] diffs;

	/**
	 * Constructor taking the number of items. Initializes a matrix with all
	 * differences set to negative one
	 * 
	 * @param n_items
	 *            Number of items among which the matirx will hold differences
	 */
	public DiffMatrix(int n_items) {
		resetNumberOfItems(n_items);
	}

	public void resetNumberOfItems(int newNumberOfItems) {
		if (newNumberOfItems <= 0) { // There must be at least one item.
			newNumberOfItems = 1;
		}

		diffs = new float[newNumberOfItems][];
		for (int i = 0; i < newNumberOfItems; ++i) {
			// In a triangular matrix, the ith row has i elements, plus the
			// diagonal of zeros
			diffs[i] = new float[i + 1];
			for (int j = 0; j < i; ++j) {
				diffs[i][j] = -1; // uninitialized flag value
			}
			diffs[i][i] = 0; // reflexive entry. d(i,i) == 0 for all i
		}
	}

	/**
	 * Initializes the matrix with user-supplied differences. May be used when
	 * the differences are read in from a file rather that computed.
	 * 
	 * @param diffs
	 *            Initial differences. Must be at least triangular. (An
	 *            ArrayIndexOutOfBounds exception will be thrown if it is not.)
	 *            Rectangular input here is OK.
	 */
	public DiffMatrix(float[][] diffs) {
		this.diffs = new float[diffs.length][];
		for (int i = 0; i < diffs.length; ++i) {
			this.diffs[i] = new float[diffs[i].length];
			System.arraycopy(diffs[i], 0, this.diffs[i], 0, diffs[i].length);
		}
	}

	/**
	 * Sets one difference in the matrix. The indices can be specified in any
	 * order. For speed, bounds checking on i and j is not done. You cannot
	 * change the value of the reflexive differences, which are always zero.
	 * 
	 * @param i
	 *            Index to the first element
	 * @param j
	 *            Index to the second element
	 * @param x
	 *            New difference between elements i and j
	 */
	public void setElement(int i, int j, float x) {
		if (i == j
		// bounds checking
		// || i >= n_items || i < 0
		// || j >= n_items || j < 0
		) {
		} // Do nothing
		else if (i > j) {
			diffs[i][j] = x;
		} else { // i < j
			diffs[j][i] = x;
		}
	}

	/**
	 * Returns one difference in the matrix The indices can be specified in any
	 * order. For speed, bounds checking on i and j is not done.
	 * 
	 * @param i
	 *            Index to the first element
	 * @param j
	 *            Index to the second element
	 * @return The current difference between i and j
	 */
	public final float getElement(int i, int j) {
		// bounds checking
		// if ( i >= n_items || i < 0
		// || j >= n_items || j < 0 ) {
		// return -1;
		// } else
		if (i > j) {
			return diffs[i][j];
		} else { // i <= j
			return diffs[j][i];
		}
	}

	/**
	 * Gets the number of items in the difference matrix
	 * 
	 * @return Number of items in the matrix
	 */
	public int getNumberOfItems() {
		return diffs.length;
	}
}

