package mesquite.treecomp.TreeSetVisualizationV2.mds;

/**
 * Mathematical points/vectors of arbitrary dimensionality.
 */
public class MDSPoint {

	/** Used in the default (no parameter) constructor */
	private final static int DEFAULT_DIMENSIONS = 2;

	/** Number of dimensions in the point (2D, 3D, etc.) */
	private int dimensionality;

	/** One value for each dimentions (e.g. x, y, and z components of a vector) */
	private float[] components;

	/**
	 * A universal loop index used to avoid declaring local variables. (Local
	 * variables can prevent the inlining of these fuctions, which are called
	 * from the inner loop of the MDS iteration method.)
	 */
	private int i;

	/**
	 * Constructor for the MDSPoint object using a component list.
	 * 
	 * @param values
	 *            Array of component values (one value for each dimension of the
	 *            point) The size of this array specifies the number of
	 *            dimensions
	 */
	public MDSPoint(float[] values) {
		dimensionality = values.length;
		components = new float[dimensionality];
		System.arraycopy(values, 0, components, 0, values.length);
	}

	/**
	 * Constructor for the MDSPoint object using a dimensionality specification.
	 * 
	 * @param ndims
	 *            Number of dimensions for the point. The point defaults to the
	 *            origin.
	 */
	public MDSPoint(int ndims) {
		if (ndims < 1) {
			/* Illegal number of dimensions. Use the default number instead. */
			ndims = DEFAULT_DIMENSIONS;
		}
		dimensionality = ndims;
		components = new float[ndims];
		for (i = 0; i < ndims; ++i) {
			components[i] = 0;
		}
	}

	/**
	 * Copy constructor for the MDSPoint object
	 * 
	 * @param p
	 *            Source point
	 */
	public MDSPoint(MDSPoint p) {
		this(p.components);
	}

	/**
	 * Default Constructor for the MDSPoint object. Makes a point at the origin
	 * with DEFAULT_DIMENSIONS dimensions.
	 */
	public MDSPoint() {
		this(DEFAULT_DIMENSIONS);
	}

	/**
	 * Sets one of the component values for a point
	 * 
	 * @param c
	 *            Which component you want to change?
	 * @param new_value
	 *            new value for that component. For efficiency, this
	 *            commonly-called method does not bounds-check c, so make sure
	 *            that c is non-negative and less than the number of dimensions.
	 */
	public void setComponent(int c, float new_value) {
		components[c] = new_value;
	}

	/**
	 * Gets a component of the point.
	 * 
	 * @param c
	 *            Which component do you want?
	 * @return the value of component c. For efficiency, this commonly-called
	 *         method does not bounds-check c, so make sure that c is
	 *         non-negative and less than the number of dimensions.
	 */
	public float getComponent(int c) {
		return components[c];
	}

	/**
	 * Gets the dimensionality of the point
	 * 
	 * @return The number of dimensions
	 */
	public int getDimensionality() {
		return dimensionality;
	}

	/** Zeros out a point. Coordinates become the origin. */
	public final void zero() {
		for (i = 0; i < dimensionality; ++i) {
			components[i] = 0;
		}
	}

	/**
	 * Returns a new vector that is the vector difference between two points
	 * 
	 * @param p
	 *            Vector to be subtracted from this
	 * @return this - p
	 */
	public MDSPoint difference(MDSPoint p) {
		MDSPoint d = new MDSPoint(dimensionality);
		for (i = 0; i < dimensionality; ++i) {
			d.components[i] = this.components[i] - p.components[i];
		}
		return d;
	}

	/**
	 * Slightly less useful than difference(), but much faster, because it
	 * doesn't need to make a new MDSPoint.
	 */
	public final void setToDifference(MDSPoint p1, MDSPoint p2) {
		for (i = 0; i < dimensionality; ++i) {
			components[i] = p1.components[i] - p2.components[i];
		}
	}

	/**
	 * Does vector addition. The vector sum (this + p) is assigned to this.
	 * 
	 * @param p
	 *            addend; added to this.
	 */
	public final void add(MDSPoint p) {
		for (i = 0; i < dimensionality; ++i) {
			components[i] += p.components[i];
		}
	}

	/**
	 * Does vector subtaction. Modifies this by assigning the vector difference
	 * (this-p) to this.
	 * 
	 * @param p
	 *            subtend; subtracted from this
	 */
	public final void subtract(MDSPoint p) {
		for (i = 0; i < dimensionality; ++i) {
			components[i] -= p.components[i];
		}
	}

	/**
	 * Treats the point as a vector and computes its magnitude
	 * 
	 * @return the scalar length of the vector specified by this Point
	 */
	public final float magnitude() {
		float accum = 0;
		for (i = 0; i < dimensionality; ++i) {
			accum += components[i] * components[i];
		}
		return (float) Math.sqrt(accum);
	}

	/**
	 * Scales this point by the factor s. For example, the point (1,-2) scaled
	 * by -3 becomes (-3,6).
	 * 
	 * @param s
	 *            the scaling factor
	 */
	public final void scale(float s) {
		for (i = 0; i < dimensionality; ++i) {
			components[i] *= s;
		}
	}

	/**
	 * Treats this point as a vector and normalizes it to length 1 by dividing
	 * it by its length. The zero vector is left unchanged.
	 */
	public void normalize() {
		float length = this.magnitude();
		if (length > 0) { // You can't normalize the zero vector.
			for (i = 0; i < dimensionality; ++i) {
				components[i] /= length;
			}
		}
	}
}
