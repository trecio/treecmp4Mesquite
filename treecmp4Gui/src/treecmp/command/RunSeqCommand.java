/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculatorS;
import treecmp.common.SummaryStatCalculator;
import treecmp.config.ActiveStatisticsSet;
import treecmp.config.IOSettings;
import treecmp.statistic.Statistic;

/**
 *
 * @author Damian
 */
public class RunSeqCommand extends Command{

     public RunSeqCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();
        
        seqCompareExecute(reader, out);
        
        reader.close();
        out.close();


    }

     public void seqCompareExecute(TreeReader reader, ResultWriter out ) {

        Statistic[] statistics=ActiveStatisticsSet.getActiveStatisticsSet().getActiveStatisticsTable();
        StatCalculatorS[] statsCalcSs=new StatCalculatorS[statistics.length];

        for(int i=0;i<statistics.length;i++){
            statsCalcSs[i]=new StatCalculatorS(statistics[i]);
        }

        seqCompareEx(reader, out, statsCalcSs);
    }

     private void seqCompareEx(TreeReader reader, ResultWriter out, StatCalculatorS[] statistics ) {

        pal.tree.Tree tree;
        int i;
        double val;
        String row="";
        int num=1;

        String separator=IOSettings.getIOSettings().getSSep();

        String head = this.createHeader(statistics);
        out.setText(head);
        out.write();

        ProgressIndicator progress=new ProgressIndicator();
        int numnerOfTrees=reader.getEffectiveNumberOfTrees();

        progress.setMaxVal(numnerOfTrees);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);

        progress.init();
        while ((tree = reader.readNextTree()) != null) {

            row=""+num+separator;

            for(i=0;i<statistics.length-1;i++){
                val=statistics[i].getStatistic(tree);
                row+=val+separator;
            }
            i=statistics.length-1;

            if(i>=0) {
                val=statistics[i].getStatistic(tree);
                row+=val;
            }

            out.setText(row);
            out.write();

            progress.displayProgress(num);

            num++;
        }

        StatCalculatorS.printSummary(out,statistics);
    }


 private String createHeader(Statistic[] statistics) {


         String header = "";
         String metricName="";
         String separator=IOSettings.getIOSettings().getSSep();
         int i;

         for (i = 0; i < statistics.length-1; i++) {
             metricName=statistics[i].getName();
             header+=metricName+separator;
         }
         i=statistics.length-1;

         if(i>=0){
            metricName=statistics[i].getName();
            header+=metricName;
         }

         header="state"+separator+header;

     return header;
    }
}
