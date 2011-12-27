/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import treecmp.ResultWriter;

/**
 *
 * @author Damian
 */
public class NewickTreePrinter {

    /**
     * 
     */
    public NewickTreePrinter() {
    }

    /**
     * Return String with tree t in Newick form.
     * @param t
     * @return
     */
    public static String printToString(Tree t)
    {
        return printToString(t,false,false);
    }
    
   /**
    * Return String with tree t in Newick form.
    * @param t
    * @param printLengths
    * @param printInternalLabels
    * @return
    */
   public static String printToString(Tree t,boolean printLengths, boolean printInternalLabels)
    {
         String tree=""; 
        
         StringWriter sWriter=new StringWriter();
         PrintWriter pWriter=new PrintWriter(sWriter);
        
         TreeUtils.printNH(t, pWriter,printLengths,printInternalLabels);
         tree=sWriter.toString();
         
         return tree;
    
    }
   
   
   public static void printArrayOfTreePairs(ArrayList<Tree[]> trees,ResultWriter out,boolean printLengths, boolean printInternalLabels)
   {
       int i;
       Tree t1,t2;
       String t1_str="";
       String t2_str="";
       int N=trees.size();
       
       out.setText("Number of pairs:"+N);
       out.write();
       
       
       for( i=0;i<N;i++)
        {
           t1= trees.get(i)[0];
           t2= trees.get(i)[1];
           
                   
            t1_str=NewickTreePrinter.printToString(t1,printLengths, printInternalLabels);
            t2_str=NewickTreePrinter.printToString(t2,printLengths,printInternalLabels);
        
            out.setText("\nPair:"+i);
            out.write();
            out.setText("t1: "+t1_str);
            out.write();
            out.setText("t2: "+t2_str);
            out.write();
       
       
       }       
   
   }
   
   public static void printArrayOfTreePairs(ArrayList<Tree[]> trees,ResultWriter out)
   {
       printArrayOfTreePairs(trees,out,false,false);
   }
}
