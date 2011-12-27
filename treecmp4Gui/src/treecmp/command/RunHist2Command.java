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
public class RunHist2Command extends Command {

    @Override
    public void run() {
        super.run();

        out.init();
        reader.open();

        hist2CalcExecute(reader, out, "");

        reader.close();
        out.close();
    }

    public RunHist2Command(int paramNumber, String name) {
        super(paramNumber, name);
    }

    public void hist2CalcExecute(TreeReader reader, ResultWriter out, String fileName) {


        //Metric[] metrics=new Metric[1];
        Metric[] metrics = ActiveMetricsSet.getActiveMetricsSet().getActiveMetricsTable();
        // metrics[0]=new BiparteSplitMetric();


        h2CalcEx(reader, out, metrics);



    }

    private void h2CalcEx(TreeReader reader, ResultWriter out, Metric[] metrics) {

        HashMap<Integer, Integer> distributon = new HashMap<Integer, Integer>();
        Integer dist, help;

        pal.tree.Tree tree1, tree2;
        int treeNum=reader.getNumberOfTrees();
        int i=0;
        int maxStep=treeNum/2;

        out.setText("Metric: " + metrics[0].getName());
        out.write();

        while ((tree1 = reader.readNextTree()) != null) {
            tree2=reader.readNextTree();
            if(tree2!=null)
            {
                System.out.println("Step:" + i + "/" + maxStep);

                dist = Integer.valueOf((int) metrics[0].getDistance(tree1, tree2));
                if (distributon.containsKey(dist)) {
                    help = distributon.get(dist);
                    help++;
                    distributon.put(dist, help);

                } else {
                    distributon.put(new Integer(dist.intValue()), new Integer(1));
                }


            }
            i++;
        }


        Set<Integer> key_set = distributon.keySet();
        String line = "";
        Iterator<Integer> itr = key_set.iterator();
        Integer key;
        while (itr.hasNext()) {
            key = itr.next();

            line = key.toString() + "\t" + distributon.get(key).toString();
            out.setText(line);
            out.write();

        }

    }



}
