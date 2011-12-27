/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.config.IOSettings;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class RunSCommand extends Command {

    public RunSCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();
        
        pairCompareExecute(reader, out);
        
        reader.close();
        out.close();


    }
/*
public void pairCompareExecute(TreeReader reader, ResultWriter out, String fileName ) {

      
        ArrayList<StatCalculator> metricList=new ArrayList<StatCalculator>();
        int size,i;
        String line="";
        StatCalculator statCalc;
        
        statCalc=new StatCalculator(new RFMetric());
                
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  NodalUnrootedMetric());
       // statCalc.setFindMaxDistTrees(true);
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  QuartetMetric());
        
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  BiparteSplitMetric());
        
        metricList.add(statCalc);
               
        
        size=metricList.size();
        StatCalculator[] metrics=new StatCalculator[size];
        
        for(i=0;i<size;i++)
            metrics[i]=metricList.get(i);
        
        pairCompareEx(reader,out,metrics);
        //tripleCompareEx(reader,out,metrics);
        
        String head="NAME\tAVG\tSTD\tMIN\tMAX\tCOUNT";
        
        //out.setText(head);
       // out.write();
        
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
        
        //ArrayList<Tree[]> trees=metrics[1].getMaxDistTrees();
        //NewickTreePrinter.printArrayOfTreePairs(trees, out);
            

    }
*/

    public void pairCompareExecute(TreeReader reader, ResultWriter out ) {



        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();


        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {
            statsMetrics[i]=new StatCalculator(metrics[i]);
        }


        pairCompareEx(reader, out, statsMetrics);




    }

private void pairCompareEx(TreeReader reader, ResultWriter out, StatCalculator[] metrics ) {

        pal.tree.Tree tree1 = reader.readNextTree();
        pal.tree.Tree tree2;
        int i;
        double val;
        String row="";
        int num=1;

        int mSize=metrics.length;

        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc=new SummaryStatCalculator[mSize];
        for(i=0;i<mSize;i++)
        {
            sStatCalc[i]=new SummaryStatCalculator(metrics[i]);
        }



        String separator=IOSettings.getIOSettings().getSSep();

        String head = this.createHeader(metrics);
        out.setText(head);
        out.write();


        ProgressIndicator progress=new ProgressIndicator();
        int numnerOfTrees=reader.getEffectiveNumberOfTrees();

        progress.setMaxVal(numnerOfTrees-1);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);
        
        progress.init();
        while ((tree2 = reader.readNextTree()) != null) {

            
            row=""+num+separator;
            
            for(i=0;i<metrics.length-1;i++)
            {
                val=metrics[i].getDistance(tree1, tree2);
                row+=val+separator;

                 //summary
                sStatCalc[i].insertValue(val);
            }
            
            i=metrics.length-1;

            if(i>=0)
            {

                val=metrics[i].getDistance(tree1, tree2);
                row+=val;

                //summary
                sStatCalc[i].insertValue(val);
            }
     
            out.setText(row);
            out.write();

            progress.displayProgress(num);

            num++;

            tree1 = tree2;
        }


        //print summary data to file
        SummaryStatCalculator.printSummary(out, sStatCalc);



    }


 private String createHeader(Metric[] metrics) {


         String header = "";
         String metricName="";
         String separator=IOSettings.getIOSettings().getSSep();
         int i;

         for (i = 0; i < metrics.length-1; i++) {
             metricName=metrics[i].getName();

             header+=metricName+separator;

         }

         i=metrics.length-1;

         if(i>=0)
         {
            metricName=metrics[i].getName();
            header+=metricName;
         }

         header="state"+separator+header;

     return header;
    }
}
