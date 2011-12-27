/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.NewickTreePrinter;
import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.metric.Metric;
import treecmp.metric.bpmetric.BipartePairMetric;

/**
 *
 * @author Damian
 */
public class RunSumCommand extends Command {

    public RunSumCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();
    
    
       out.init();
       reader.open();
             
       //sumCompareExecute(reader,out,args[1]);
       sumCompareExecute(reader,out,"");
       reader.close();
       out.close();
    
    
    
    }

   
   private void sumCompareEx(TreeReader reader, ResultWriter out, Metric[] metrics ) {

       // pal.tree.Tree tree1 = reader.readNextTree();
       pal.tree.Tree tree1;
       pal.tree.Tree tree2;
        int i,j,k;
        
       /* while ((tree2 = reader.readNextTree()) != null) {

            for(i=0;i<metrics.length;i++)
            {
                metrics[i].getDistance(tree1, tree2);
            }
                       
            tree1 = tree2;
        }
*/

        Vector<Tree> tree_vec = new Vector<Tree>();

     

        while ((tree1 = reader.readNextTree()) != null) {
            tree_vec.add(tree1);
        }
           int N = tree_vec.size();

        int counter = 1;
        int maxIt = N * (N - 1) / 2;
        ProgressIndicator progress = new ProgressIndicator();


        progress.setMaxVal(maxIt);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);

        progress.init();




        for (i = 0; i < N; i++) {
            for (j = i + 1; j < N; j++) {

                tree1 = tree_vec.get(i);
                tree2 = tree_vec.get(j);

                for (k = 0; k < metrics.length; k++) {
                    metrics[k].getDistance(tree1, tree2);
                }
                progress.displayProgress(counter);
                counter++;
            }
        }


    }

    
  
    
    
        
    public void sumCompareExecute(TreeReader reader, ResultWriter out, String fileName ) {

      
        ArrayList<StatCalculator> metricList=new ArrayList<StatCalculator>();
        int size,i;
        String line="";
        StatCalculator statCalc;
       
        Metric[] activeMetrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();
  
        for(i=0;i<activeMetrics.length;i++){
            statCalc=new StatCalculator(activeMetrics[i]);
            statCalc.setFindMaxDistTrees(true);
            statCalc.setMaxTreesListSize(100);
            metricList.add(statCalc);    
        }
        /*
        statCalc=new StatCalculator(new RFMetric());
                
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  NodalUnrootedMetric());
       // statCalc.setFindMaxDistTrees(true);
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  QuartetMetric());
        
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  BiparteSplitMetric());
        
        metricList.add(statCalc);
        */
        //statCalc=new StatCalculator(new BipartePairMetric());
       // statCalc.setFindMaxDistTrees(true);
       // metricList.add(statCalc);

        size=metricList.size();
        StatCalculator[] metrics=new StatCalculator[size];
        
        for(i=0;i<size;i++)
            metrics[i]=metricList.get(i);
        
        sumCompareEx(reader,out,metrics);
        //tripleCompareEx(reader,out,metrics);
        
        String head="FILE\tNAME\tAVG\tSTD\tMIN\tMAX\tCOUNT";
        
        out.setText(head);
        out.write();
        
        for(i=0;i<size;i++)
        {
            line=fileName+"\t"+metrics[i].getName()+"\t"+
                      String.format(Locale.US, "%1$.6f", metrics[i].getAvg())+"\t"+
                      String.format(Locale.US, "%1$.6f", metrics[i].getStd())+"\t"+
                      String.format(Locale.US, "%1$.6f", metrics[i].getMin())+"\t"+
                      String.format(Locale.US, "%1$.6f", metrics[i].getMax())+"\t"+
                      metrics[i].getCount();
            
            out.setText(line);
            out.write();
        }

        for (i=0;i<metrics.length;i++){
            out.setText(metrics[i].getName()+":");
            out.write();
            ArrayList<Tree[]> trees=metrics[i].getMaxDistTrees();
            NewickTreePrinter.printArrayOfTreePairs(trees, out);
        }

    }
      
}
