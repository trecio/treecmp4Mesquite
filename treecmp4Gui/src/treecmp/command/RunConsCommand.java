/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.MinDistTreeHolder;
import treecmp.common.NewickTreePrinter;
import treecmp.common.ProgressIndicator;
import java.util.Vector;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class RunConsCommand extends Command {

    private String listOfTrees="";

      public RunConsCommand(int paramNumber, String name,String paramValue) {
        super(paramNumber, name);
        this.listOfTrees=paramValue;

    }
    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();

        consExecute(reader, listOfTrees, out);

        reader.close();
        out.close();



    }


     public void consExecute(TreeReader reader, String listOfTrees, ResultWriter out) {



        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();

/*
        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {
            statsMetrics[i]=new StatCalculator(metrics[i]);
        }
*/
        TreeReader readerListOfTree = new TreeReader(listOfTrees);
        readerListOfTree.open();

        consEx(reader, readerListOfTree, out, metrics);
        readerListOfTree.close();


    }

     private void consEx(TreeReader reader, TreeReader readerListOfTree, ResultWriter out, Metric[] metrics)
     {
        
          Vector<Tree> candidateTree_vec = new Vector<Tree>();
          Vector<Tree> givenTree_vec = new Vector<Tree>();
          
          Tree tree;
        while ((tree = reader.readNextTree()) != null) {
            candidateTree_vec.add(tree);
        }
          
          
        while ((tree = readerListOfTree.readNextTree()) != null) {
            givenTree_vec.add(tree);
        }
         
        int N_c = candidateTree_vec.size();
        int N_g = givenTree_vec.size();
        
        int counter=1;
        int maxIt=N_c*N_g;
        ProgressIndicator progress=new ProgressIndicator();
        progress.setMaxVal(maxIt);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);
        progress.init();
        MinDistTreeHolder treeHolder=new MinDistTreeHolder();

        for(int i=0;i<N_c;i++){

            double sumDist=0.0;
            tree=candidateTree_vec.get(i);

            for(int j=0;j<N_g;j++){
                sumDist+=metrics[0].getDistance(tree, givenTree_vec.get(j));

               progress.displayProgress(counter);
               counter++;
            }
            treeHolder.updateMinTrees(sumDist, tree);
        }
        out.setText("Metric: "+metrics[0].getName());
        out.write();

        out.setText("Number of trees: "+treeHolder.getNumOfTrees());
        out.write();
        out.setText("Sum of distances: "+treeHolder.getMin());
        out.write();

        out.setText("Consensus tress:");
        out.write();
        
        String treeString="";
        for(Tree t:treeHolder.getMinDistTrees()){
            treeString=NewickTreePrinter.printToString(t);
            out.setText(treeString);
            out.write_pure();


        }
    }
}
