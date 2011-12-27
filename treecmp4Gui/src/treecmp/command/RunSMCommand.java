/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.CorrelationCalculator;
import treecmp.common.ProgressIndicator;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
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
public class RunSMCommand extends Command {

 public RunSMCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

     @Override
    public void run() {
        super.run();

        out.init();
        reader.open();

        singleMatrixCompareExecute(reader, out);

        reader.close();
        out.close();




    }

    private void singleMatrixCompareEx(TreeReader reader, ResultWriter out, Metric[] metrics) {

        pal.tree.Tree tree1,tree2 ;
        Vector<Tree> tree_vec = new Vector<Tree>();
        int k,counter,maxIt;
        double val;
        String row;


        int mSize=metrics.length;

        if(mSize!=1)
        {
            System.out.println("In sigle matrix mode only one metrix can be active!");
            return;

        }

        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc=new SummaryStatCalculator[mSize];
        for(int i=0;i<mSize;i++)
        {
            sStatCalc[i]=new SummaryStatCalculator(metrics[i]);
        }


         String separator=IOSettings.getIOSettings().getSSep();



        while ((tree1 = reader.readNextTree()) != null) {
            tree_vec.add(tree1);
        }

        int N = tree_vec.size();
        counter=1;
        maxIt=N*(N-1)/2;
        ProgressIndicator progress=new ProgressIndicator();


        progress.setMaxVal(maxIt);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);

        progress.init();

        double mat[][]=new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {

                tree1 = tree_vec.get(i);
                tree2 = tree_vec.get(j);


                val = metrics[0].getDistance(tree1, tree2);

                mat[i][j] = val;
                mat[j][i] = val;

                sStatCalc[0].insertValue(val);
                progress.displayProgress(counter);
                counter++;

            }
        }

        //print matrix to file
        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N - 1; j++) {

                out.setText(mat[i][j] + separator);
                out.write_pure();

            }
            out.setText(Double.toString(mat[i][N - 1]));
            out.write();

        }

       // SummaryStatCalculator.printSummary(out, sStatCalc);


    }

    public void singleMatrixCompareExecute(TreeReader reader, ResultWriter out ) {



        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();


        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {


            statsMetrics[i]=new StatCalculator(metrics[i]);


            if(IOSettings.getIOSettings().isCalcCorrelation())//temprary set statcalc to hold valuse
                statsMetrics[i].setRecordValues(true);
        }


        singleMatrixCompareEx(reader, out, statsMetrics);

        if (IOSettings.getIOSettings().isCalcCorrelation()) {

            CorrelationCalculator cc = new CorrelationCalculator(statsMetrics);
            cc.calcAndPrint();
        }

    }

}