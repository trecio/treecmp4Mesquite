/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import pal.tree.Tree;
import treecmp.ResultWriter;
import treecmp.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.metric.Metric;

/**
 *
 * @author Damian
 */
public class RunHistCommand extends Command {

    @Override
    public void run() {
        super.run();

         out.init();
        reader.open();

        histCalcExecute(reader, out, "");

        reader.close();
        out.close();
    }

    public RunHistCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }




public void histCalcExecute(TreeReader reader, ResultWriter out, String fileName ) {


        //Metric[] metrics=new Metric[1];
        Metric[] metrics=ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();
       // metrics[0]=new BiparteSplitMetric();


        hCalcEx(reader,out,metrics);



    }



 private void hCalcEx(TreeReader reader, ResultWriter out, Metric[] metrics ) {

         Vector<Tree> tree_vec = new Vector<Tree>();
         HashMap<Integer,Integer> distributon=new HashMap<Integer,Integer>();
         Integer dist,help;

         pal.tree.Tree tree1,tree2 ;

        while ((tree1 = reader.readNextTree()) != null) {
            tree_vec.add(tree1);
        }

        int N = tree_vec.size();

        out.setText("Metric: "+metrics[0].getName());
        out.write();
        
        for (int i = 0; i < N; i++) {
            System.out.println("Step:"+i+"/"+N);

            for (int j=i+1; j<N;j++)
            {

                tree1 = tree_vec.get(i);
                tree2 = tree_vec.get(j);
                dist=Integer.valueOf((int)metrics[0].getDistance(tree1, tree2));

                if (distributon.containsKey(dist))
                {
                    help=distributon.get(dist);
                    help++;
                    distributon.put(dist, help);

                }else
                {
                    distributon.put(new Integer(dist.intValue()), new Integer(1));
                }

            }

      }



        Set<Integer> key_set = distributon.keySet();
        String line="";
        Iterator<Integer> itr = key_set.iterator();
        Integer key;
        while (itr.hasNext()) {
        key = itr.next();

        line=key.toString() + "\t" + distributon.get(key).toString();
        out.setText(line);
            out.write();
        
    }






 }

}
