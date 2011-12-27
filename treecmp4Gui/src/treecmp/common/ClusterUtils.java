/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import java.util.HashMap;
import java.util.Map;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class ClusterUtils {


   public static boolean[][]  getClusters(IdGroup idGroup, Tree tree)
	{
		int clusterCount = tree.getInternalNodeCount()-1;
        int labelCount = idGroup.getIdCount();

		boolean[][] clusters = new boolean[clusterCount][labelCount];

        Map<Node,boolean[]> clusterMap=new HashMap<Node,boolean[]>();


        /*


		for (int i = 0; i < size; i++)
		{
			getSplit(idGroup, tree.getInternalNode(i), splits[i]);
		}
*/

		return clusters;
	}

    private boolean[] getNewFalseBooleanArray(int size) {
        boolean[] booleanArray = new boolean[size];

        for (int i = 0; i < booleanArray.length; i++) {
            booleanArray[i] = false;
        }

        return booleanArray;
    }
    
}
