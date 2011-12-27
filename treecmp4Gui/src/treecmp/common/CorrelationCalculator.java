/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
import treecmp.ResultWriter;
import treecmp.config.IOSettings;

/**
 *
 * @author Damian
 */
public class CorrelationCalculator {


    private StatCalculator[] statsMetrics;
    public CorrelationCalculator(StatCalculator[] statsMetrics) {
        this.statsMetrics=statsMetrics;
    }


    public void calcAndPrint()
    {
           IOSettings IOset=IOSettings.getIOSettings();
           ResultWriter out = new ResultWriter(IOset.getInputFile()+".cor",true);
           out.init();


        if(statsMetrics.length>1)
        {

            int matLen=statsMetrics[0].getValuesHolder().size();
            int colNum=statsMetrics.length;
            double temp=0.0;

            double[][] distArray = new double[matLen][colNum];

            for (int i = 0; i < statsMetrics.length; i++) {
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
             for (int i = 0; i < statsMetrics.length; i++)
             {
                 row=row+"\t"+statsMetrics[i].getName();

             }
             out.setText(row);
             out.write();

            double [][] cMat=corMatrix.getData();

            for(int i=0;i<cMat.length;i++)
            {
                 row=statsMetrics[i].getName();
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
             for (int i = 0; i < statsMetrics.length; i++)
             {
                 row=row+"\t"+statsMetrics[i].getName();

             }
             out.setText(row);
             out.write();

            cMat=corMatrix2.getData();

            for(int i=0;i<cMat.length;i++)
            {
                 row=statsMetrics[i].getName();
                for(int j=0;j<cMat[i].length;j++)
                {
                    row=row+"\t"+cMat[i][j];

                }
                out.setText(row);
                out.write();
            }




          int a=3;

        }

        out.close();
    }



}
