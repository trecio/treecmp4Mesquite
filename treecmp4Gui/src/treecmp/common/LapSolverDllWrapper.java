/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

/**
 *
 * @author Damian
 */
public class LapSolverDllWrapper {

    public static native int lap(int dim,
        int assigncost[][],
        int rowsol[],
        int colsol[],
        int u[],
        int v[]);
  //Load the library
  static {
    System.loadLibrary("LAJVC_dll");
  }
}
