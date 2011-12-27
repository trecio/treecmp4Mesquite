/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.config.IOSettings;
import treecmp.statistic.Statistic;
/**
 *
 * @author Damian
 */
public class StatCalculatorS extends StatCalculator implements Statistic {

    private Statistic stat;
    
    public StatCalculatorS(Statistic _stat) {
      
        //call non-parameter constructor
        super();      
        this.stat=_stat;       
    }


    public double getStatistic(Tree t){
        
        double dist=stat.getStatistic(t);

        sum+=dist;
        count++;
        sq_sum+=dist*dist;

        if(dist<min) min=dist;
        if(dist>max) max=dist;

        if(this.recordValues)
            addValue(dist);

        return dist;
    }

    @Override
    public String getName() {
        return this.stat.getName();
    }

    @Override
     public String getCommandLineName() {
        return this.stat.getCommandLineName();
    }

    @Override
    public void setCommandLineName(String commandLineName) {
        this.stat.setCommandLineName(commandLineName);
    }

    @Override
    public void setName(String name) {
        this.stat.setName(name);
    }

    @Override
    public String getDescription() {
        return this.stat.getDescription();
    }

    @Override
    public void setDescription(String description) {
        this.stat.setDescription(description);
    }

    public static void printSummary(ResultWriter out,StatCalculatorS[] sStatCalc)
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
