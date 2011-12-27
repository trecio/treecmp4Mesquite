/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import treecmp.config.IOSettings;
import treecmp.metric.Metric;
import treecmp.metric.bpmetric.MatchingClusterOptMetric;
import treecmp.metric.bpmetric.MatchingSplitOptMetric;

/**
 *
 * @author Damian
 */
public class RunA1Command extends Command {

    public RunA1Command(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();

        a1CompareExecute(reader, out);

        reader.close();
        out.close();


    }


    public void a1CompareExecute(TreeReader reader, ResultWriter out ) {



        Metric[] metrics=new Metric[1];
        metrics[0]=new MatchingSplitOptMetric();




        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {
            statsMetrics[i]=new StatCalculator(metrics[i]);
        }


        a1CompareEx(reader, out, statsMetrics);




    }

private void a1CompareEx(TreeReader reader, ResultWriter out, StatCalculator[] metrics ) {

        pal.tree.Tree tree1 = reader.readNextTree();
        pal.tree.Tree tree2 ;
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
