/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.NewickTreePrinter;
import treecmp.common.StatCalculator;
import java.util.ArrayList;
import java.util.Locale;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.metric.Metric;
import treecmp.metric.NodalUnrootedMetric;
import treecmp.metric.QuartetMetric;
import treecmp.metric.RFMetric;
import treecmp.metric.bpmetric.BiparteSplitMetric;

/**
 *
 * @author Damian
 */
public class RunTCommand extends Command {

    public RunTCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();
        
        out.init();
        reader.open();
        
        tripleCompareExecute(reader, out, args[1]);
        
        reader.close();
        out.close();
        
        
    }

    public void tripleCompareExecute(TreeReader reader, ResultWriter out, String fileName ) {

      
        ArrayList<StatCalculator> metricList=new ArrayList<StatCalculator>();
        int size,i;
        String line="";
        StatCalculator statCalc;
        
        statCalc=new StatCalculator(new RFMetric());
                
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  NodalUnrootedMetric());
        statCalc.setFindMaxDistTrees(true);
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  QuartetMetric());
        
        metricList.add(statCalc);
        
        statCalc=new StatCalculator(new  BiparteSplitMetric());
        
        metricList.add(statCalc);
               
        
        size=metricList.size();
        StatCalculator[] metrics=new StatCalculator[size];
        
        for(i=0;i<size;i++)
            metrics[i]=metricList.get(i);
        
        //pairCompareEx(reader,out,metrics);
        tripleCompareEx(reader,out,metrics);
        
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
        
        ArrayList<Tree[]> trees=metrics[1].getMaxDistTrees();
        NewickTreePrinter.printArrayOfTreePairs(trees, out);
            

    }  
    private void tripleCompareEx(TreeReader reader, ResultWriter out, Metric[] metrics ) {

        pal.tree.Tree tree1 = reader.readNextTree();
        pal.tree.Tree tree2=  reader.readNextTree();
        pal.tree.Tree tree3;
        double a,b,c,cos;
        int i;      
        
        while ((tree3 = reader.readNextTree()) != null) {

            for(i=0;i<metrics.length;i++)
            {
                a=metrics[i].getDistance(tree1, tree2);
                b=metrics[i].getDistance(tree2, tree3);
                c=metrics[i].getDistance(tree1, tree3);
                
                if(a==0||b==0||c==0) cos=1;
                else
                {
                    cos=(a*a+b*b-c*c)/(2*a*b);
                }
                
                out.setText(cos+"\t");
                out.write_pure();
            
            }
                  out.setText("\n");
                  out.write_pure();
            
            
            
            tree1 = tree2;
            tree2 = tree3;
        }

    }
    
}
