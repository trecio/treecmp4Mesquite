/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import treecmp.ResultWriter;
import treecmp.config.IOSettings;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class SummaryStatCalculator {


    private Metric met;
    private int count;
    private double sum;
    private double sq_sum;
    private double min;
    private double max;



    public SummaryStatCalculator() {

        this.count=0;
        this.sum=0.0;
        this.sq_sum=0.0;

        this.min=Double.MAX_VALUE;
        this.max=-Double.MIN_VALUE;

    }


    /**
     *
     * @param _met
     */
    public SummaryStatCalculator(Metric _met) {

        //call non-parameter constructor
        this();
        this.met=_met;
    }

    public void addMetric(Metric _met)
    {
        this.met=_met;
        this.clear();
    }


   public void clear()
  {
        this.count=0;
        this.sum=0.0;
        this.sq_sum=0.0;

        this.min=Double.MAX_VALUE;
        this.max=-Double.MAX_VALUE;

    }


  public double getMax()
  {
   return this.max;
  }

  public double getMin()
  {
    return this.min;
  }

  public double getAvg()
  {
      double avg=Double.POSITIVE_INFINITY;
      if (count>0)
          avg=this.sum/(double)count;

      return avg;
  }

  public double getVariance()
  {
      double var=Double.POSITIVE_INFINITY;
      double avg;
      if (count>0)
      {
          avg=this.getAvg();
          var=this.sq_sum/(double)count-avg*avg;
      }
      return var;
  }

  public double getStd()
  {
        double std=Double.POSITIVE_INFINITY;
        double var;

        if(count>0)
        {
            var=this.getVariance();
            std=Math.sqrt(var);
        }
        return std;
  }


  public int getCount()
  {
      return this.count;
  }

  public void insertValue(double dist)
  {
        sum+=dist;
        count++;
        sq_sum+=dist*dist;

        if(dist<min) min=dist;
        if(dist>max) max=dist;

  }

    public String getName() {
        return this.met.getName();
    }

     public String getCommandLineName() {
        return this.met.getCommandLineName();
    }


     public static void printSummary(ResultWriter out,SummaryStatCalculator[] sStatCalc)
     {
         int size=sStatCalc.length;
         String separator=IOSettings.getIOSettings().getSSep();
         String line="";

         line="---------";
         out.setText(line);
         out.write();
         line="Summary:";

         out.setText(line);
         out.write();

         //name-avg-std-min-max-count
         line="Name"+separator;
         line+="Avg"+separator;
         line+="Std"+separator;
         line+="Min"+separator;
         line+="Max"+separator;
         line+="Count";

         out.setText(line);
         out.write();

         for(int i=0;i<size;i++)
         {
            line=sStatCalc[i].getName()+separator;
            line+=sStatCalc[i].getAvg()+separator;
            line+=sStatCalc[i].getStd()+separator;
            line+=sStatCalc[i].getMin()+separator;
            line+=sStatCalc[i].getMax()+separator;
            line+=sStatCalc[i].getCount();
            out.setText(line);
            out.write();
         }


     }
  

}
