/**
 * This file is part of TreeCmp, a tool for comparing phylogenetic trees using
 * the Matching Split distance and other metrics. Copyright (C) 2011, Damian
 * Bogdanowicz
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package treecmp.command;

import pal.tree.Tree;
import treecmp.common.AlignWriter;
import treecmp.common.ProgressIndicator;
import treecmp.common.ReportUtils;
import treecmp.common.StatCalculator;
import treecmp.common.SummaryStatCalculator;
import treecmp.common.TreeCmpException;
import treecmp.io.ResultWriter;
import treecmp.io.TreeReader;
import treecmp.config.ActiveMetricsSet;
import treecmp.metric.Metric;

public class RunRCommand extends Command {

    private String refTreeFile;
    private TreeReader refTreeReader;

    public RunRCommand(int paramNumber, String name, String refTreeFile) {
        super(paramNumber, name);
        this.refTreeFile = refTreeFile;
    }

    @Override
    public void run() throws TreeCmpException {
        super.run();

        out.init();
        reader.open();
        refTreeReader = new TreeReader(refTreeFile);
        refTreeReader.open();
        refTreeReader.scan();
        refTreeReader.close();
        refTreeReader.setStep(1);
        refTreeReader.open();

        refTreeCompareExecute(reader, out);

        refTreeReader.close();
        reader.close();
        out.close();

    }

    public void refTreeCompareExecute(TreeReader reader, ResultWriter out) throws TreeCmpException {

        Metric[] metrics = ActiveMetricsSet.getInstance().getActiveMetricsTable();
        StatCalculator[] statsMetrics = new StatCalculator[metrics.length];

        for (int i = 0; i < metrics.length; i++) {
            statsMetrics[i] = new StatCalculator(metrics[i]);
        }

        refTreeCompareEx(reader, out, statsMetrics);

    }

    private void refTreeCompareEx(TreeReader reader, ResultWriter out, StatCalculator[] metrics) throws TreeCmpException {

        Tree tree;
        Tree refTree;
        double val;
        String row = "";
        long num = 1;

        int mSize = metrics.length;

        //initialize summary stat calculators
        SummaryStatCalculator[] sStatCalc = new SummaryStatCalculator[mSize];
        for (int i = 0; i < mSize; i++) {
            sStatCalc[i] = new SummaryStatCalculator(metrics[i]);
        }

        String head = ReportUtils.getHeaderRow(metrics, true);
        out.setText(head);
        out.write();

        AlignWriter aw = new AlignWriter();
        aw.initFiles(metrics);

        ProgressIndicator progress = new ProgressIndicator();
        int numbnerOfTrees = reader.getEffectiveNumberOfTrees();
        int numbnerOfRefTrees = refTreeReader.getEffectiveNumberOfTrees();
        long numberOfComparisons = (long)numbnerOfTrees * (long)numbnerOfRefTrees;
        progress.setMaxVal(numberOfComparisons);
        progress.setPrintInterval(600);
        progress.setPrintPercentInterval(5.0);
        progress.init();
        int i = 0;
        while ((refTree = refTreeReader.readNextTree()) != null) {
            int j = 0; 
            while ((tree = reader.readNextTree()) != null) {
                for (int k = 0; k < metrics.length; k++) {
                    val = metrics[k].getDistance(refTree, tree);
                    //summary
                    sStatCalc[k].insertValue(val);
                }

                row = ReportUtils.getResultRow((int) num, i + 1, j + 1, metrics);
                out.setText(row);
                out.write();

                aw.writeAlignments((int) num, i + 1, j + 1, metrics);

                progress.displayProgress(num);
                num++;
                j++;
            }
            reader.close();
            reader.open();
            i++;
        }
        aw.closeFiles(metrics);
        //print summary data to file
        SummaryStatCalculator.printSummary(out, sStatCalc);
    }
}
