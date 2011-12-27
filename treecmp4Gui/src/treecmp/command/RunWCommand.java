/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import java.util.Locale;
import java.util.Vector;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.config.IOSettings;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class RunWCommand extends Command {

    private final static int ROW_PRECISION=4;

    public RunWCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    public RunWCommand(int paramNumber, String name,int paramValue) {
        super(paramNumber, name);
        this.param=paramValue;
        
    }

    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();

        windowCompareExecute(reader, this.getParam(), out);

        reader.close();
        out.close();



    }

    private void windowCompareEx(TreeReader reader, int winSize, ResultWriter out, Metric[] metrics) {


        pal.tree.Tree tree1, tree2;
        Vector<Tree> tree_vec = new Vector<Tree>();

        int num, k;
        int count = 0;
        int n = 0;

        String head = "state\tRF_avg\tRF_std\tNodal_avg\tNodal_std";
        out.setText(head);
        out.write();

        //System.out.println(head);

        do {
            n = 0;
            tree_vec.clear();
            do {

                tree1 = reader.readNextTree();
                if (tree1 != null) {
                    tree_vec.add(tree1);
                    n++;
                }

            } while (tree1 != null && n < winSize);


            //comparing all pairs in vector
            int N = tree_vec.size();
            num = 0;
            if (N > 0) {
                int size;
                if (N > 1) {
                    size = (N * (N - 1)) / 2;
                } else {
                    size = 1;
                }

                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        tree1 = tree_vec.get(i);
                        tree2 = tree_vec.get(j);

                        for (k = 0; k < metrics.length; k++) {
                            metrics[k].getDistance(tree1, tree2);
                        }

                    }

                }

                if (num > 0) {

                    String result = "mama";
                    out.setText(result);
                    out.write();
                    if (out.writeToFile == true) {
                        System.out.print(".");
                    }

                }

            }
            count++;
        } while (tree1 != null);


    }

    private void windowCompareEx(TreeReader reader, int winSize, ResultWriter out, StatCalculator[] metrics) {


        pal.tree.Tree tree1, tree2;
        Vector<Tree> tree_vec = new Vector<Tree>();
        String row="";
        int num, k;
        int n = 0;
        double val=0;

        String rowDataFormat="%1$."+ROW_PRECISION+"f";


        int mSize=metrics.length;

        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc=new SummaryStatCalculator[mSize];
        for(int i=0;i<mSize;i++)
        {
            sStatCalc[i]=new SummaryStatCalculator(metrics[i]);
        }



        String separator=IOSettings.getIOSettings().getSSep();
        
        String head = this.createHeader(metrics);
        out.setText(head);
        out.write();

        int numberOfTreas=reader.getEffectiveNumberOfTrees();

        int nWin=numberOfTreas/winSize;
        int lastWinNum=numberOfTreas%winSize;
        int maxIt=(winSize*(winSize-1)/2)*nWin+(lastWinNum*(lastWinNum-1)/2);

        int counter=1;

        ProgressIndicator progress=new ProgressIndicator();


        progress.setMaxVal(maxIt);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);

        progress.init();


        //System.out.println(head);
        num = 1;

        do {
            n = 0;
            tree_vec.clear();
            do {

                tree1 = reader.readNextTree();
                if (tree1 != null) {
                    tree_vec.add(tree1);
                    n++;
                }

            } while (tree1 != null && n < winSize);


            //comparing all pairs in vector
            int N = tree_vec.size();
            
            if (N > 1) {
             
                 for (k = 0; k < metrics.length; k++) {
                            metrics[k].clear();
                        }

                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        tree1 = tree_vec.get(i);
                        tree2 = tree_vec.get(j);

                        for (k = 0; k < metrics.length; k++) {
                            metrics[k].getDistance(tree1, tree2);
                        }

                         progress.displayProgress(counter);
                         counter++;

                    }

                }
            //print row statistic
            row=""+num+separator;
            for (k = 0; k < metrics.length-1; k++) {


                val=metrics[k].getAvg();
                row+=String.format(Locale.US,rowDataFormat,val)+separator;
                row+=String.format(Locale.US,rowDataFormat,metrics[k].getStd())+separator;

                //summary
                sStatCalc[k].insertValue(val);
            }

            k=metrics.length-1;

            if(k>=0)
            {
                val=metrics[k].getAvg();
                
                row+=String.format(Locale.US,rowDataFormat,val)+separator;
                row+=String.format(Locale.US,rowDataFormat,metrics[k].getStd());

                //summary
                sStatCalc[k].insertValue(val);
            }

            out.setText(row);
            out.write();
            num++;
            }
            

        } while (tree1 != null);

        //print summary data to file
        SummaryStatCalculator.printSummary(out, sStatCalc);

    }


    public void windowCompareExecute(TreeReader reader, int winSize, ResultWriter out) {



        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();
        

        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {
            statsMetrics[i]=new StatCalculator(metrics[i]);
        }


        windowCompareEx(reader, winSize, out, statsMetrics);

    }


     private String createHeader(Metric[] metrics) {


         String header = "";
         String metricName="";
         String separator=IOSettings.getIOSettings().getSSep();
         int i;

         for (i = 0; i < metrics.length-1; i++) {
             metricName=metrics[i].getName();

             header+=metricName+" (avg)"+separator;
             header+=metricName+" (stddev)"+separator;

         }

         i=metrics.length-1;

         if(i>=0)
         {
            metricName=metrics[i].getName();
            header+=metricName+" (avg)"+separator;
            header+=metricName+" (stddev)";
         }

         header="state"+separator+header;

     return header;
    }


}
