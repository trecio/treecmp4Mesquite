/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

/**
 *
 * @author Damian based on
 */
/************************************************************************
 * 
 * lap.cpp version 1.0 - 4 September 1996 author: Roy Jonker @ MagicLogic
 * Optimization Inc. e-mail: roy_jonker@magiclogic.com
 * 
 * Code for Linear Assignment Problem, according to
 * 
 * "A Shortest Augmenting Path Algorithm for Dense and Sparse Linear Assignment
 * Problems," Computing 38, 325-340, 1987
 * 
 * by
 * 
 * R. Jonker and A. Volgenant, University of Amsterdam.
 * 
 *************************************************************************/
public class LapSolver {

	static final int BIG = 100000000;

	public static int lap(int dim, int assigncost[][], int rowsol[],
			int colsol[], int u[], int v[])

	// input:
	// dim - problem size
	// assigncost - cost matrix

	// output:
	// rowsol - column assigned to row in solution
	// colsol - row assigned to column in solution
	// u - dual variables, row reduction numbers
	// v - dual variables, column reduction numbers

	{
		boolean unassignedfound;
		int i, imin, numfree = 0, prvnumfree, f, i0, k, freerow;
		int j, j1, j2 = 0, endofpath = 0, last = 0, low, up;
		int min = 0, h, umin, usubmin, v2;

		int[] free = new int[dim]; // list of unassigned rows.
		int[] collist = new int[dim]; // list of columns to be scanned in
										// various ways.
		int[] matches = new int[dim]; // counts how many times a row could be
										// assigned.
		int[] d = new int[dim]; // 'cost-distance' in augmenting path
								// calculation.
		int[] pred = new int[dim]; // row-predecessor of column in
									// augmenting/alternating path.

		// init how many times a row will be assigned in the column reduction.
		for (i = 0; i < dim; i++)
			matches[i] = 0;

		// COLUMN REDUCTION
		for (j = dim - 1; j >= 0; j--) // reverse order gives better results.
		{
			// find minimum cost over rows.
			min = assigncost[0][j];
			imin = 0;
			for (i = 1; i < dim; i++)
				if (assigncost[i][j] < min) {
					min = assigncost[i][j];
					imin = i;
				}
			v[j] = min;

			if (++matches[imin] == 1) {
				// init assignment if minimum row assigned for first time.
				rowsol[imin] = j;
				colsol[j] = imin;
			} else
				colsol[j] = -1; // row already assigned, column not assigned.
		}

		// REDUCTION TRANSFER
		for (i = 0; i < dim; i++)
			if (matches[i] == 0) // fill list of unassigned 'free' rows.
				free[numfree++] = i;
			else if (matches[i] == 1) // transfer reduction from rows that are
										// assigned once.
			{
				j1 = rowsol[i];
				min = BIG;
				for (j = 0; j < dim; j++)
					if (j != j1)
						if (assigncost[i][j] - v[j] < min)
							min = assigncost[i][j] - v[j];
				v[j1] = v[j1] - min;
			}

		// AUGMENTING ROW REDUCTION
		int loopcnt = 0; // do-loop to be done twice.
		do {
			loopcnt++;

			// scan all free rows.
			// in some cases, a free row may be replaced with another one to be
			// scanned next.
			k = 0;
			prvnumfree = numfree;
			numfree = 0; // start list of rows still free after augmenting row
							// reduction.
			while (k < prvnumfree) {
				i = free[k];
				k++;

				// find minimum and second minimum reduced cost over columns.
				umin = assigncost[i][0] - v[0];
				j1 = 0;
				usubmin = BIG;
				for (j = 1; j < dim; j++) {
					h = assigncost[i][j] - v[j];
					if (h < usubmin) {
						if (h >= umin) {
							usubmin = h;
							j2 = j;
						} else {
							usubmin = umin;
							umin = h;
							j2 = j1;
							j1 = j;
						}
					}
				}

				i0 = colsol[j1];
				if (umin < usubmin)
					// change the reduction of the minimum column to increase
					// the minimum
					// reduced cost in the row to the subminimum.
					v[j1] = v[j1] - (usubmin - umin);
				else // minimum and subminimum equal.
				if (i0 >= 0) // minimum column j1 is assigned.
				{
					// swap columns j1 and j2, as j2 may be unassigned.
					j1 = j2;
					i0 = colsol[j2];
				}

				// (re-)assign i to j1, possibly de-assigning an i0.
				rowsol[i] = j1;
				colsol[j1] = i;

				if (i0 >= 0) // minimum column j1 assigned earlier.
					if (umin < usubmin)
						// put in current k, and go back to that k.
						// continue augmenting path i - j1 with i0.
						free[--k] = i0;
					else
						// no further augmenting reduction possible.
						// store i0 in list of free rows for next phase.
						free[numfree++] = i0;
			}
		} while (loopcnt < 2); // repeat once.

		// AUGMENT SOLUTION for each free row.
		for (f = 0; f < numfree; f++) {
			freerow = free[f]; // start row of augmenting path.

			// Dijkstra shortest path algorithm.
			// runs until unassigned column added to shortest path tree.
			for (j = 0; j < dim; j++) {
				d[j] = assigncost[freerow][j] - v[j];
				pred[j] = freerow;
				collist[j] = j; // init column list.
			}

			low = 0; // columns in 0..low-1 are ready, now none.
			up = 0; // columns in low..up-1 are to be scanned for current
					// minimum, now none.
			// columns in up..dim-1 are to be considered later to find new
			// minimum,
			// at this stage the list simply contains all columns
			unassignedfound = false;
			do {
				if (up == low) // no more columns to be scanned for current
								// minimum.
				{
					last = low - 1;

					// scan columns for up..dim-1 to find all indices for which
					// new minimum occurs.
					// store these indices between low..up-1 (increasing up).
					min = d[collist[up++]];
					for (k = up; k < dim; k++) {
						j = collist[k];
						h = d[j];
						if (h <= min) {
							if (h < min) // new minimum.
							{
								up = low; // restart list at index low.
								min = h;
							}
							// new index with same minimum, put on undex up, and
							// extend list.
							collist[k] = collist[up];
							collist[up++] = j;
						}
					}

					// check if any of the minimum columns happens to be
					// unassigned.
					// if so, we have an augmenting path right away.
					for (k = low; k < up; k++)
						if (colsol[collist[k]] < 0) {
							endofpath = collist[k];
							unassignedfound = true;
							break;
						}
				}

				if (!unassignedfound) {
					// update 'distances' between freerow and all unscanned
					// columns, via next scanned column.
					j1 = collist[low];
					low++;
					i = colsol[j1];
					h = assigncost[i][j1] - v[j1] - min;

					for (k = up; k < dim; k++) {
						j = collist[k];
						v2 = assigncost[i][j] - v[j] - h;
						if (v2 < d[j]) {
							pred[j] = i;
							if (v2 == min) // new column found at same minimum
											// value
								if (colsol[j] < 0) {
									// if unassigned, shortest augmenting path
									// is complete.
									endofpath = j;
									unassignedfound = true;
									break;
								}
								// else add to list to be scanned right away.
								else {
									collist[k] = collist[up];
									collist[up++] = j;
								}
							d[j] = v2;
						}
					}
				}
			} while (!unassignedfound);

			// update column prices.
			for (k = 0; k <= last; k++) {
				j1 = collist[k];
				v[j1] = v[j1] + d[j1] - min;
			}

			// reset row and column assignments along the alternating path.
			do {
				i = pred[endofpath];
				colsol[endofpath] = i;
				j1 = endofpath;
				endofpath = rowsol[i];
				rowsol[i] = j1;
			} while (i != freerow);
		}

		// calculate optimal cost.
		int lapcost = 0;
		for (i = 0; i < dim; i++) {
			j = rowsol[i];
			u[i] = assigncost[i][j] - v[j];
			lapcost = lapcost + assigncost[i][j];
		}

		return lapcost;
	}

	public static double lap(int dim, double assigncost[][], int rowsol[],
			int colsol[], double u[], double v[])

	// input:
	// dim - problem size
	// assigncost - cost matrix

	// output:
	// rowsol - column assigned to row in solution
	// colsol - row assigned to column in solution
	// u - dual variables, row reduction numbers
	// v - dual variables, column reduction numbers

	{
		boolean unassignedfound;
		int i, imin, numfree = 0, prvnumfree, f, i0, k, freerow;
		int j, j1, j2 = 0, endofpath = 0, last = 0, low, up;
		double min = 0, h, umin, usubmin, v2;

		int[] free = new int[dim]; // list of unassigned rows.
		int[] collist = new int[dim]; // list of columns to be scanned in
		// various ways.
		int[] matches = new int[dim]; // counts how many times a row could be
		// assigned.
		double[] d = new double[dim]; // 'cost-distance' in augmenting path
		// calculation.
		int[] pred = new int[dim]; // row-predecessor of column in
		// augmenting/alternating path.

		// init how many times a row will be assigned in the column reduction.
		for (i = 0; i < dim; i++)
			matches[i] = 0;

		// COLUMN REDUCTION
		for (j = dim - 1; j >= 0; j--) // reverse order gives better results.
		{
			// find minimum cost over rows.
			min = assigncost[0][j];
			imin = 0;
			for (i = 1; i < dim; i++)
				if (assigncost[i][j] < min) {
					min = assigncost[i][j];
					imin = i;
				}
			v[j] = min;

			if (++matches[imin] == 1) {
				// init assignment if minimum row assigned for first time.
				rowsol[imin] = j;
				colsol[j] = imin;
			} else
				colsol[j] = -1; // row already assigned, column not assigned.
		}

		// REDUCTION TRANSFER
		for (i = 0; i < dim; i++)
			if (matches[i] == 0) // fill list of unassigned 'free' rows.
				free[numfree++] = i;
			else if (matches[i] == 1) // transfer reduction from rows that are
			// assigned once.
			{
				j1 = rowsol[i];
				min = BIG;
				for (j = 0; j < dim; j++)
					if (j != j1)
						if (assigncost[i][j] - v[j] < min)
							min = assigncost[i][j] - v[j];
				v[j1] = v[j1] - min;
			}

		// AUGMENTING ROW REDUCTION
		int loopcnt = 0; // do-loop to be done twice.
		do {
			loopcnt++;

			// scan all free rows.
			// in some cases, a free row may be replaced with another one to be
			// scanned next.
			k = 0;
			prvnumfree = numfree;
			numfree = 0; // start list of rows still free after augmenting row
			// reduction.
			while (k < prvnumfree) {
				i = free[k];
				k++;

				// find minimum and second minimum reduced cost over columns.
				umin = assigncost[i][0] - v[0];
				j1 = 0;
				usubmin = BIG;
				for (j = 1; j < dim; j++) {
					h = assigncost[i][j] - v[j];
					if (h < usubmin) {
						if (h >= umin) {
							usubmin = h;
							j2 = j;
						} else {
							usubmin = umin;
							umin = h;
							j2 = j1;
							j1 = j;
						}
					}
				}

				i0 = colsol[j1];
				if (umin < usubmin)
					// change the reduction of the minimum column to increase
					// the minimum
					// reduced cost in the row to the subminimum.
					v[j1] = v[j1] - (usubmin - umin);
				else // minimum and subminimum equal.
				if (i0 >= 0) // minimum column j1 is assigned.
				{
					// swap columns j1 and j2, as j2 may be unassigned.
					j1 = j2;
					i0 = colsol[j2];
				}

				// (re-)assign i to j1, possibly de-assigning an i0.
				rowsol[i] = j1;
				colsol[j1] = i;

				if (i0 >= 0) // minimum column j1 assigned earlier.
					if (umin < usubmin)
						// put in current k, and go back to that k.
						// continue augmenting path i - j1 with i0.
						free[--k] = i0;
					else
						// no further augmenting reduction possible.
						// store i0 in list of free rows for next phase.
						free[numfree++] = i0;
			}
		} while (loopcnt < 2); // repeat once.

		// AUGMENT SOLUTION for each free row.
		for (f = 0; f < numfree; f++) {
			freerow = free[f]; // start row of augmenting path.

			// Dijkstra shortest path algorithm.
			// runs until unassigned column added to shortest path tree.
			for (j = 0; j < dim; j++) {
				d[j] = assigncost[freerow][j] - v[j];
				pred[j] = freerow;
				collist[j] = j; // init column list.
			}

			low = 0; // columns in 0..low-1 are ready, now none.
			up = 0; // columns in low..up-1 are to be scanned for current
			// minimum, now none.
			// columns in up..dim-1 are to be considered later to find new
			// minimum,
			// at this stage the list simply contains all columns
			unassignedfound = false;
			do {
				if (up == low) // no more columns to be scanned for current
				// minimum.
				{
					last = low - 1;

					// scan columns for up..dim-1 to find all indices for which
					// new minimum occurs.
					// store these indices between low..up-1 (increasing up).
					min = d[collist[up++]];
					for (k = up; k < dim; k++) {
						j = collist[k];
						h = d[j];
						if (h <= min) {
							if (h < min) // new minimum.
							{
								up = low; // restart list at index low.
								min = h;
							}
							// new index with same minimum, put on undex up, and
							// extend list.
							collist[k] = collist[up];
							collist[up++] = j;
						}
					}

					// check if any of the minimum columns happens to be
					// unassigned.
					// if so, we have an augmenting path right away.
					for (k = low; k < up; k++)
						if (colsol[collist[k]] < 0) {
							endofpath = collist[k];
							unassignedfound = true;
							break;
						}
				}

				if (!unassignedfound) {
					// update 'distances' between freerow and all unscanned
					// columns, via next scanned column.
					j1 = collist[low];
					low++;
					i = colsol[j1];
					h = assigncost[i][j1] - v[j1] - min;

					for (k = up; k < dim; k++) {
						j = collist[k];
						v2 = assigncost[i][j] - v[j] - h;
						if (v2 < d[j]) {
							pred[j] = i;
							if (v2 == min) // new column found at same minimum
								// value
								if (colsol[j] < 0) {
									// if unassigned, shortest augmenting path
									// is complete.
									endofpath = j;
									unassignedfound = true;
									break;
								}
								// else add to list to be scanned right away.
								else {
									collist[k] = collist[up];
									collist[up++] = j;
								}
							d[j] = v2;
						}
					}
				}
			} while (!unassignedfound);

			// update column prices.
			for (k = 0; k <= last; k++) {
				j1 = collist[k];
				v[j1] = v[j1] + d[j1] - min;
			}

			// reset row and column assignments along the alternating path.
			do {
				i = pred[endofpath];
				colsol[endofpath] = i;
				j1 = endofpath;
				endofpath = rowsol[i];
				rowsol[i] = j1;
			} while (i != freerow);
		}

		// calculate optimal cost.
		double lapcost = 0;
		for (i = 0; i < dim; i++) {
			j = rowsol[i];
			u[i] = assigncost[i][j] - v[j];
			lapcost = lapcost + assigncost[i][j];
		}

		return lapcost;
	}

	public static int lapShort(int dim, short assigncost[][], int rowsol[],
			int colsol[], int u[], int v[])

	// input:
	// dim - problem size
	// assigncost - cost matrix

	// output:
	// rowsol - column assigned to row in solution
	// colsol - row assigned to column in solution
	// u - dual variables, row reduction numbers
	// v - dual variables, column reduction numbers

	{
		boolean unassignedfound;
		int i, imin, numfree = 0, prvnumfree, f, i0, k, freerow;
		int j, j1, j2 = 0, endofpath = 0, last = 0, low, up;
		int min = 0, h, umin, usubmin, v2;

		int[] free = new int[dim]; // list of unassigned rows.
		int[] collist = new int[dim]; // list of columns to be scanned in
										// various ways.
		int[] matches = new int[dim]; // counts how many times a row could be
										// assigned.
		int[] d = new int[dim]; // 'cost-distance' in augmenting path
								// calculation.
		int[] pred = new int[dim]; // row-predecessor of column in
									// augmenting/alternating path.

		// init how many times a row will be assigned in the column reduction.
		for (i = 0; i < dim; i++)
			matches[i] = 0;

		// COLUMN REDUCTION
		for (j = dim - 1; j >= 0; j--) // reverse order gives better results.
		{
			// find minimum cost over rows.
			min = assigncost[0][j];
			imin = 0;
			for (i = 1; i < dim; i++)
				if (assigncost[i][j] < min) {
					min = assigncost[i][j];
					imin = i;
				}
			v[j] = min;

			if (++matches[imin] == 1) {
				// init assignment if minimum row assigned for first time.
				rowsol[imin] = j;
				colsol[j] = imin;
			} else
				colsol[j] = -1; // row already assigned, column not assigned.
		}

		// REDUCTION TRANSFER
		for (i = 0; i < dim; i++)
			if (matches[i] == 0) // fill list of unassigned 'free' rows.
				free[numfree++] = i;
			else if (matches[i] == 1) // transfer reduction from rows that are
										// assigned once.
			{
				j1 = rowsol[i];
				min = BIG;
				for (j = 0; j < dim; j++)
					if (j != j1)
						if (assigncost[i][j] - v[j] < min)
							min = assigncost[i][j] - v[j];
				v[j1] = v[j1] - min;
			}

		// AUGMENTING ROW REDUCTION
		int loopcnt = 0; // do-loop to be done twice.
		do {
			loopcnt++;

			// scan all free rows.
			// in some cases, a free row may be replaced with another one to be
			// scanned next.
			k = 0;
			prvnumfree = numfree;
			numfree = 0; // start list of rows still free after augmenting row
							// reduction.
			while (k < prvnumfree) {
				i = free[k];
				k++;

				// find minimum and second minimum reduced cost over columns.
				umin = assigncost[i][0] - v[0];
				j1 = 0;
				usubmin = BIG;
				for (j = 1; j < dim; j++) {
					h = assigncost[i][j] - v[j];
					if (h < usubmin) {
						if (h >= umin) {
							usubmin = h;
							j2 = j;
						} else {
							usubmin = umin;
							umin = h;
							j2 = j1;
							j1 = j;
						}
					}
				}

				i0 = colsol[j1];
				if (umin < usubmin)
					// change the reduction of the minimum column to increase
					// the minimum
					// reduced cost in the row to the subminimum.
					v[j1] = v[j1] - (usubmin - umin);
				else // minimum and subminimum equal.
				if (i0 >= 0) // minimum column j1 is assigned.
				{
					// swap columns j1 and j2, as j2 may be unassigned.
					j1 = j2;
					i0 = colsol[j2];
				}

				// (re-)assign i to j1, possibly de-assigning an i0.
				rowsol[i] = j1;
				colsol[j1] = i;

				if (i0 >= 0) // minimum column j1 assigned earlier.
					if (umin < usubmin)
						// put in current k, and go back to that k.
						// continue augmenting path i - j1 with i0.
						free[--k] = i0;
					else
						// no further augmenting reduction possible.
						// store i0 in list of free rows for next phase.
						free[numfree++] = i0;
			}
		} while (loopcnt < 2); // repeat once.

		// AUGMENT SOLUTION for each free row.
		for (f = 0; f < numfree; f++) {
			freerow = free[f]; // start row of augmenting path.

			// Dijkstra shortest path algorithm.
			// runs until unassigned column added to shortest path tree.
			for (j = 0; j < dim; j++) {
				d[j] = assigncost[freerow][j] - v[j];
				pred[j] = freerow;
				collist[j] = j; // init column list.
			}

			low = 0; // columns in 0..low-1 are ready, now none.
			up = 0; // columns in low..up-1 are to be scanned for current
					// minimum, now none.
			// columns in up..dim-1 are to be considered later to find new
			// minimum,
			// at this stage the list simply contains all columns
			unassignedfound = false;
			do {
				if (up == low) // no more columns to be scanned for current
								// minimum.
				{
					last = low - 1;

					// scan columns for up..dim-1 to find all indices for which
					// new minimum occurs.
					// store these indices between low..up-1 (increasing up).
					min = d[collist[up++]];
					for (k = up; k < dim; k++) {
						j = collist[k];
						h = d[j];
						if (h <= min) {
							if (h < min) // new minimum.
							{
								up = low; // restart list at index low.
								min = h;
							}
							// new index with same minimum, put on undex up, and
							// extend list.
							collist[k] = collist[up];
							collist[up++] = j;
						}
					}

					// check if any of the minimum columns happens to be
					// unassigned.
					// if so, we have an augmenting path right away.
					for (k = low; k < up; k++)
						if (colsol[collist[k]] < 0) {
							endofpath = collist[k];
							unassignedfound = true;
							break;
						}
				}

				if (!unassignedfound) {
					// update 'distances' between freerow and all unscanned
					// columns, via next scanned column.
					j1 = collist[low];
					low++;
					i = colsol[j1];
					h = assigncost[i][j1] - v[j1] - min;

					for (k = up; k < dim; k++) {
						j = collist[k];
						v2 = assigncost[i][j] - v[j] - h;
						if (v2 < d[j]) {
							pred[j] = i;
							if (v2 == min) // new column found at same minimum
											// value
								if (colsol[j] < 0) {
									// if unassigned, shortest augmenting path
									// is complete.
									endofpath = j;
									unassignedfound = true;
									break;
								}
								// else add to list to be scanned right away.
								else {
									collist[k] = collist[up];
									collist[up++] = j;
								}
							d[j] = v2;
						}
					}
				}
			} while (!unassignedfound);

			// update column prices.
			for (k = 0; k <= last; k++) {
				j1 = collist[k];
				v[j1] = v[j1] + d[j1] - min;
			}

			// reset row and column assignments along the alternating path.
			do {
				i = pred[endofpath];
				colsol[endofpath] = i;
				j1 = endofpath;
				endofpath = rowsol[i];
				rowsol[i] = j1;
			} while (i != freerow);
		}

		// calculate optimal cost.
		int lapcost = 0;
		for (i = 0; i < dim; i++) {
			j = rowsol[i];
			u[i] = assigncost[i][j] - v[j];
			lapcost = lapcost + assigncost[i][j];
		}

		return lapcost;
	}

	// public static int lapPascal(int n,
	// int c[][],
	// int x[],
	// int y[],
	// int u[],
	// int v[])
	//
	// {
	// /**
	// as published in
	// R. Jonker and A. Volgenant, University of Amsterdam,
	// A Shortest Augmenting Path Algorithm
	// for Dense and Sparse Linear Assignment Problems,
	// Computing 38, 325-340 (1987).
	// n: problem size;
	// c: costs matrix;
	// x: columns assigned to rows ;
	// y: rows assigned to columns ;
	// u: dual row variables ;
	// v: dual column variables
	// */
	//
	// //label augment;
	// int inf=1000000; // inf is a suitably large number
	//
	//
	// int f,h,i,j,k,f0,i1,j1,j2,u1,u2,min,last,low,up;
	//
	// int [] col,d,free,pred;
	// col =new int[n];
	// d =new int[n];
	// free =new int[n];
	// pred =new int[n];
	//
	// /*
	// { col : array of columns, scanned (k=1..low-1),
	// labeled and unscanned (k=low..up-1),
	// unlabeled (k=up..n);
	// d : shortest path lengths;
	// free : unassigned rows (number f0, index f);
	// pred : predecessor-array for shortest path tree;
	// i,i1 : row indices; j,j1,j2: column indices;
	// last : last column in col-array with d[j]<min. }
	// */
	// begin
	// for i:=1 to n do x[i]:=0;
	//
	// for j:=n downto 1 do { #### COLUMN REDUCTION }
	// begin
	// col[j]:=j; h:=c[1,j]; i1:=1;
	// for i:=2 to n do if c[i,j]<h then begin h:=c[i,j]; i1:=i end;
	// v[j]:=h;
	// if x[i1]=0 then begin x[i1]:=j; y[j]:=i1 end
	// else begin x[i1]:=-abs(x[i1]); y[j]:=0 end
	// end;
	//
	// f:=0; //{ #### REDUCTION TRANSFER }
	// for i:=1 to n do
	// if x[i]=0 then // ## unassigned row in free-array
	// begin f:=f+1; free[f]:=i end else
	// if x[i]<0 then //{ ## no reduction transfer possible }
	// x[i]:=-x[i] else //{ ## reduction transfer from assigned row }
	// begin
	// j1:=x[i]; min:=inf;
	// for j:=1 to n do if j<>j1 then
	// if c[i,j]-v[j]<min then min:=c[i,j]-v[j];
	// v[j1]:=v[j1]-min
	// end;
	//
	// cnt:=0;//{ #### AUGMENTING ROW REDUCTION }
	// repeat
	// k:=1; f0:=f; f:=0;
	// while k<=f0 do
	// begin
	// i:=free[k]; k:=k+1; u1:=c[i,1]-v[1]; j1:=1; u2:=inf;
	// for j:=2 to n do
	// begin
	// h:=c[i,j]-v[j];
	// if h<u2 then
	// if h>=u1 then begin u2:=h; j2:=j end
	// else begin u2:=u1; u1:=h; j2:=j1; j1:=j end
	// end;
	// i1:=y[j1];
	// if u1<u2 then v[j1]:=v[j1]-u2+u1
	// else if i1>0 then begin j1:=j2; i1:=y[j1] end;
	// if i1>0 then
	// if u1<u2 then begin k:=k-1; free[k]:=i1 end
	// else begin f:=f+1; free[f]:=i1 end;
	// x[i]:=j1; y[j1]:=i
	// end;
	// cnt:=cnt+1
	// until cnt=2; //{ ## routine applied twice }
	//
	// f0:=f; //{ #### AUGMENTATION }
	// for f:=1 to f0 do
	// begin
	// i1:=free[f]; low:=1; up:=1;//{ ## initialize d- and pred-array }
	// for j:=1 to n do begin d[j]:=c[i1,j]-v[j]; pred[j]:=i1 end;
	// repeat
	// if up=low then //{ ## find columns with new value for minimum d }
	// begin
	// last:=low-1; min:=d[col[up]]; up:=up+1;
	// for k:=up to n do
	// begin
	// j:=col[k]; h:=d[j];
	// if h<=min then
	// begin
	// if h<min then begin up:=low; min:=h end;
	// col[k]:=col[up]; col[up]:=j; up:=up+1
	// end
	// end;
	// for h:=low to up-1 do
	// begin j:=col[h]; if y[j]=0 then goto augment end
	// end; { up=low }
	//
	// j1:=col[low]; low:=low+1; i:=y[j1]; //{ ## scan a row }
	// u1:=c[i,j1]-v[j1]-min;
	// for k:=up to n do
	// begin
	// j:=col[k]; h:=c[i,j]-v[j]-u1;
	// if h<d[j] then
	// begin
	// d[j]:=h; pred[j]:=i;
	// if h=min then
	// if y[j]=0 then goto augment
	// else begin col[k]:=col[up]; col[up]:=j; up:=up+1 end
	// end
	// end
	// until false; //{ repeat ends with goto augment }
	//
	// augment:
	// for k:=1 to last do //{ ## updating of column prices }
	// begin j1:=col[k]; v[j1]:=v[j1]+d[j1]-min end;
	// repeat{ ## augmentation }
	// i:=pred[j]; y[j]:=i; k:=j; j:=x[i]; x[i]:=k
	// until i=i1
	// end; //{ of augmentation }
	//
	// h:=0; //{ #### DETERMINE ROW PRICES AND OPTIMAL VALUE }
	// for i:=1 to n do begin j:=x[i]; u[i]:=c[i,j]-v[j]; h:=h+u[i]+v[j] end;
	// lapjv:=h
	// return h;
	//
	// }

}
