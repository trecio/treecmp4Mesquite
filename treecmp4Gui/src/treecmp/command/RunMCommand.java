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
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
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
public class RunMCommand extends Command {

    public RunMCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();
        
        out.init();
        reader.open();
        
        matrixCompareExecute(reader, out);
        
        reader.close();
        out.close();
        
        
        
        
    }

    private void matrixCompareEx(TreeReader reader, ResultWriter out, Metric[] metrics) {

        pal.tree.Tree tree1,tree2 ;
        Vector<Tree> tree_vec = new Vector<Tree>();
        int k,counter,maxIt;
        double val;
        String row;


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


        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {

                tree1 = tree_vec.get(i);
                tree2 = tree_vec.get(j);                
                row=""+counter+separator+i+separator+j+separator;
                for(k=0;k<metrics.length-1;k++){
                    
                    val=metrics[k].getDistance(tree1, tree2);

                    row+=val+separator;
                    sStatCalc[k].insertValue(val);
                }   

                k=metrics.length-1;

                if(k>=0)
                {
                    val=metrics[k].getDistance(tree1, tree2);
                    row+=val;

                    //summary
                    sStatCalc[k].insertValue(val);
                }

                out.setText(row);
                out.write();


                progress.displayProgress(counter);
                counter++;

            }
        }

        SummaryStatCalculator.printSummary(out, sStatCalc);


    }  
    
    public void matrixCompareExecute(TreeReader reader, ResultWriter out ) {

      

        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();


        StatCalculator[] statsMetrics=new StatCalculator[metrics.length];

        for(int i=0;i<metrics.length;i++)
        {
           
            
            statsMetrics[i]=new StatCalculator(metrics[i]);


            if(IOSettings.getIOSettings().isCalcCorrelation())//temprary set statcalc to hold valuse
                statsMetrics[i].setRecordValues(true);
        }


        matrixCompareEx(reader, out, statsMetrics);

        if (IOSettings.getIOSettings().isCalcCorrelation()) {
            
            CorrelationCalculator cc = new CorrelationCalculator(statsMetrics);
            cc.calcAndPrint();
        }
/*
        //if calculate correlation matrix
        if(statsMetrics.length>1)
        {

            int matLen=statsMetrics[0].getValuesHolder().size();
            int colNum=metrics.length;
            double temp=0.0;

            double[][] distArray = new double[matLen][colNum];

            for (int i = 0; i < metrics.length; i++) {
                for (int j = 0; j < matLen; j++) {
                    temp = statsMetrics[i].getValuesHolder().get(j);
                    distArray[j][i] = temp;

                }
            }
            PearsonsCorrelation PCor=new PearsonsCorrelation(distArray);

            RealMatrix corMatrix=PCor.getCorrelationMatrix();

            String row="---------";
             out.setText(row);
             out.write();
            row="Pearsons Correlation Matrix:";
             out.setText(row);
             out.write();

             row="";
             for (int i = 0; i < metrics.length; i++)
             {
                 row=row+"\t"+metrics[i].getName();

             }
             out.setText(row);
             out.write();

            double [][] cMat=corMatrix.getData();

            for(int i=0;i<cMat.length;i++)
            {
                 row=metrics[i].getName();
                for(int j=0;j<cMat[i].length;j++)
                {
                    row=row+"\t"+cMat[i][j];

                }
                out.setText(row);
                out.write();
            }
            //run garbage collector
            System.gc(); 

             SpearmansCorrelation SCor=new SpearmansCorrelation();

            RealMatrix corMatrix2=SCor.computeCorrelationMatrix(distArray);

            row="---------";
             out.setText(row);
             out.write();
            row="Spearmans Correlation Matrix:";
             out.setText(row);
             out.write();

             row="";
             for (int i = 0; i < metrics.length; i++)
             {
                 row=row+"\t"+metrics[i].getName();

             }
             out.setText(row);
             out.write();

            cMat=corMatrix2.getData();

            for(int i=0;i<cMat.length;i++)
            {
                 row=metrics[i].getName();
                for(int j=0;j<cMat[i].length;j++)
                {
                    row=row+"\t"+cMat[i][j];

                }
                out.setText(row);
                out.write();
            }




          int a=3;

        }
    */
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

         header="state"+separator+"tree1"+separator+"tree2"+separator+header;

     return header;
    }
}
