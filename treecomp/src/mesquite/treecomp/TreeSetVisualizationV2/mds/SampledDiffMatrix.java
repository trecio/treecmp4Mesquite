package mesquite.treecomp.TreeSetVisualizationV2.mds;

import mesquite.treecomp.TreeSetVisualizationV2.DiffMatrix;

/**
 * A Difference Matrix that supports sampling (only some subset of the distances
 * are necessarily maintained.)
 */
public class SampledDiffMatrix extends DiffMatrix {

	/**
	 * True if we are sampling a set of points from the overall set of n points.
	 * This means that if point p is in the sample, then for all other points x,
	 * (x,p) is active. Note that (p,x) is not necesarily also active.
	 */
	private boolean samplingByPoint;
	/**
	 * true if we are sampling some set of differences in the matrix. Not all
	 * the differences for one particular point are necessarily in the sample.
	 * samplingByPoint and samplingByDiff are never both true.
	 */
	private boolean samplingByDiff;

	/**
	 * A bit vector to keep track of which points are in the sample (used for
	 * sampling by point)
	 */
	private boolean[] pointSample;
	/**
	 * a 2D bit vertor to keep track of which ordered point pairs are in the
	 * sample (used for sampling by difference)
	 */
	private boolean[][] diffSample;

	/** constructor to creates a diff matrix with sampling turned off initially */
	public SampledDiffMatrix(int n_items) {
		super(n_items);
		disableSampling();
		pointSample = null;
		diffSample = null;
	}

	public void resetNumberOfItems(int newNumberOfItems) {
		super.resetNumberOfItems(newNumberOfItems);
		disableSampling();
		pointSample = null;
		diffSample = null;
	}

	/** Returns true iff sampling by point is enabled */
	public boolean getSamplingByPoint() {
		return samplingByPoint;
	}

	/** returns true iff sampliny by difference is enabled */
	public boolean getSamplingByDiff() {
		return samplingByDiff;
	}

	/** returns true iff any kind of sampling is enabled */
	public boolean getSampling() {
		return (samplingByPoint || samplingByDiff);
	}

	/** turns off all sampling */
	public void disableSampling() {
		samplingByPoint = false;
		samplingByDiff = false;
	}

	/**
	 * Returns the current sample size. This is the number of points if sampling
	 * by point (0..n) or the number of differences if sampling by diffs
	 * (0..n^2). Returns zero if sampling is not enabled.
	 */
	public int getSampleSize() {
		int sampleSize = 0;
		if (samplingByPoint) {
			for (int i = 0; i < pointSample.length; ++i) {
				if (pointSample[i]) {
					++sampleSize;
				}
			}
		} else if (samplingByDiff) {
			for (int i = 0; i < diffSample.length; ++i) {
				for (int j = 0; j < diffSample.length; ++j) {
					if (diffSample[i][j]) {
						++sampleSize;
					}
				}
			}
		}
		return sampleSize;
	}

	/**
	 * Chooses sampleSize random points from the n possibilities. Note that if
	 * point p is in the sample, then for all other points x, (x,p) is active
	 * and that (p,x) is not necesarily therefore also active.
	 */
	public void sampleByPoint(int sampleSize) {
		samplingByPoint = true;
		samplingByDiff = false;

		int n = getNumberOfItems();

		// create the sample array if it doesn't already exist.
		if (pointSample == null) {
			pointSample = new boolean[n];
		}

		// First wipe the sample by removing every point from it.
		for (int i = 0; i < n; ++i) {
			pointSample[i] = false;
		}

		java.util.Random randomSource = new java.util.Random(); // seed here for
																// repeatability
		int currentRandomChoice;
		int k;
		for (int i = 0; i < sampleSize; ++i) {
			// loop invariant: of the n total points, i have been chosen and
			// (n - i) are available for the next random selection.)
			currentRandomChoice = randomSource.nextInt(n - i);
			// find the (currentRandomChoice+1)th number in the overall set that
			// hasn't already been chosen.
			k = 0;
			while (pointSample[k]) {
				++k;
			} // skip initial trues
			for (int j = 0; j < currentRandomChoice; j++) {
				// loop invariant: we have passed over j falses; sample[k] =
				// false
				++k; // pass over the (j+1)th false
				while (pointSample[k]) {
					++k;
				} // skip trues
			}
			pointSample[k] = true; // k is now the index of the
									// (currenRandomChoice+1)th false
		}
	}

	/**
	 * Chooses sampleSize different differences at random from the n^2
	 * possibilities. Note that d(x,y) is considered to be independent of d(y,x)
	 * for this sampling even thought d(x,y) always equals d(y,x)
	 */
	public void sampleByDiff(int sampleSize) {
		samplingByPoint = false;
		samplingByDiff = true;

		int n = getNumberOfItems();

		// create the sample array if it doesn't already exist.
		if (diffSample == null) {
			diffSample = new boolean[n][n];
		}

		// First wipe the sample by removing every difference from it.
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				diffSample[i][j] = false;
			}
		}

		java.util.Random randomSource = new java.util.Random(); // seed here for
																// repeatability
		int currentRandomChoice;
		int k;
		// We will think of the the 2D array of booleans as one long linear
		// array, so that
		// we can step through it with the same logic that was used for
		// sampleByPoint().
		// k is the index into this big unrolled 1D boolean array
		for (int i = 0; i < sampleSize; ++i) {
			// loop invariant: of the n^2 total differences, i have been chosen
			// and (n^2 - i) are available for the next random selection.)
			currentRandomChoice = randomSource.nextInt(n * n - i);
			// find the (currentRandomChoice+1)th number in the overall set that
			// hasn't already been chosen.
			k = 0;
			while (diffSample[k / n][k % n]) {
				++k;
			} // skip initial trues
			for (int j = 0; j < currentRandomChoice; j++) {
				// loop invariant: we have passed over j falses; sample[k] =
				// false
				++k; // pass over the (j+1)th false
				while (diffSample[k / n][k % n]) {
					++k;
				} // skip trues
			}
			diffSample[k / n][k % n] = true; // k is now the index of the
												// (currenRandomChoice+1)th
												// false
		}
	}

	/**
	 * Checks whether or not a point is in the sample. Returns false if sampling
	 * by point is not enabled.
	 */
	public final boolean pointInSample(int n) {
		// relies on short-circuiting of the && operator
		return (samplingByPoint && (n >= 0 && n < getNumberOfItems()) && pointSample[n]);
	}

	/**
	 * Checks whether or not a specified difference (order of points matters) is
	 * currently in the sample. Returns false if sampling is not enabled. If
	 * samplingByPoint, diffInSample(x,p) is true if p is in the sample. Because
	 * this method is called in the inner loop of MDS, it does not check the
	 * bounds of x and y for the sake of speed. Be careful.
	 */
	public final boolean diffInSample(int x, int y) {
		// relies on short-circuiting of the && and || operators
		// return ((samplingByPoint && pointSample[y]) || (samplingbyDiff &&
		// diffSample[x][y]));
		// An if-then-else construction seems to run faster than the big
		// expression on the previous line.
		if (samplingByPoint) {
			return pointSample[y];
		} else if (samplingByDiff) {
			return diffSample[x][y];
		} else {
			return false;
		}
	}
}

