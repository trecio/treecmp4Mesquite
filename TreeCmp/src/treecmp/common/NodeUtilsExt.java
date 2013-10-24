/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import java.io.PrintWriter;
import pal.io.FormattedOutput;
import pal.io.OutputTarget;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public class NodeUtilsExt extends NodeUtils {

    public static int printNH(PrintWriter out, Node node,
            boolean printLengths, boolean printInternalLabels, int column, int fractionDigits) {

        if (!node.isLeaf()) {
            out.print("(");
            column++;

            for (int i = 0; i < node.getChildCount(); i++) {
                if (i != 0) {
                    out.print(",");
                    column++;
                }

                column = printNH(out, node.getChild(i), printLengths, printInternalLabels, column, fractionDigits);
            }

            out.print(")");
            column++;
        }

        if (!node.isRoot()) {
            if (node.isLeaf() || printInternalLabels) {

                String id = node.getIdentifier().toString();
                out.print(id);
                column += id.length();
            }

            if (printLengths) {
                out.print(":");
                column++;

                column += FormattedOutput.getInstance().displayDecimal(out, node.getBranchLength(), fractionDigits);
            }
        }
        return column;
    }

    public static String treeToSimpleString(Tree tree,  boolean printLengths){
        OutputTarget out = OutputTarget.openString();
        NodeUtilsExt.printNH(out, tree.getRoot(), printLengths, false, 0, 6);
        String treeNewick = out.getString();
        out.close();
        return treeNewick;
    }
    
    public static void getSplitExternal(IdGroup idGroup, Node externalNode, boolean[] split) {
        // make sure split is reset
        for (int i = 0; i < split.length; i++) {
            split[i] = false;
        }

        if (externalNode.isLeaf()) {
            String name1 = externalNode.getIdentifier().getName();
            int index = idGroup.whichIdNumber(name1);

            if (index < 0) {
                throw new IllegalArgumentException("INCOMPATIBLE IDENTIFIER (" + name1 + ")");
            }
            split[index] = true;
        } else{
             throw new IllegalArgumentException("NOT EXTERNAL NODE CHOSEN: " + externalNode);
        }

        // standardize split (i.e. first index is alway true)
        if (split[0] == false) {
            for (int i = 0; i < split.length; i++) {
                if (split[i] == false) {
                    split[i] = true;
                } else {
                    split[i] = false;
                }
            }
        }
    }
}
