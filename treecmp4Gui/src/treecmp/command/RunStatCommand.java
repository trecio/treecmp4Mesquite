/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import treecmp.common.TreeCountPair;
import java.util.Vector;
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
public class RunStatCommand extends Command {

    public RunStatCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();
        
         out.init();
        reader.open();
        
        statCalcExecute(reader, out, args[1]);
        
        reader.close();
        out.close();

    }
  public void statCalcExecute(TreeReader reader, ResultWriter out, String fileName ) {

      
        Metric[] metrics=new Metric[4];
        metrics[0]=new RFMetric();
        metrics[1]=new  NodalUnrootedMetric();
        metrics[2]=new  QuartetMetric();
        metrics[3]=new  BiparteSplitMetric();
        
        statCalcEx(reader,out,metrics);
        
       

    }
    private void statCalcEx(TreeReader reader, ResultWriter out, Metric[] metrics ) {

         Vector<Tree> tree_vec = new Vector<Tree>();
         Vector<TreeCountPair> treeCount = new Vector<TreeCountPair>();
         TreeCountPair treePair;
         pal.tree.Tree tree1,tree2 ;
         double dist[]=new double[metrics.length];
         int N_tc,count;
         boolean found=false;
         
        while ((tree1 = reader.readNextTree()) != null) {
            tree_vec.add(tree1);
        }
           
        int N = tree_vec.size();
        
        for (int i = 0; i < N; i++) {    
            tree1 = tree_vec.get(i);
            N_tc=treeCount.size();
            found=false;
            for(int j=0;j<N_tc;j++)
            {
                treePair=treeCount.get(j);
                tree2=treePair.tree;
                if(metrics[0].getDistance(tree1, tree2)==0)
                {
                    treePair.count++;
                    found=true;
                    break;
                }                    
                
            }
            if(!found)
                treeCount.add(new TreeCountPair(tree1));            
            
        }
    
      N_tc=treeCount.size();
      
      out.setText("tree\tcount");
      out.write();
      for(int i=0;i<N_tc;i++)
      {
          treePair=treeCount.get(i);
          count=treePair.count;
          out.setText("tree"+i+"\t"+count);
          out.write();
      }
      out.setText("dist matrix:\tRF\tNodal\tQuartet\tBS");
      out.write();
      for (int i = 0; i < N_tc; i++) {
            for (int j = i + 1; j < N_tc; j++) {
                tree1 = treeCount.get(i).tree;
                tree2 = treeCount.get(j).tree;
                
                out.setText("(tree"+i+",tree"+j+"):");
                out.write_pure();
                for(int k=0;k<metrics.length;k++)
                {
                    dist[k]=metrics[k].getDistance(tree1, tree2);
                    out.setText("\t"+dist[k]);
                    out.write_pure();
                }
                out.setText("\n");
                out.write_pure();
            
            }
      }
      
      
      
      
      
      }
}
