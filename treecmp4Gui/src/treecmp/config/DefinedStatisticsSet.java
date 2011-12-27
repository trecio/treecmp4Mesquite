/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.config;

import java.util.Vector;
import treecmp.statistic.Statistic;

/**
 *
 * @author Damian
 */
public class DefinedStatisticsSet {

    private static DefinedStatisticsSet DSset;
    private Vector<Statistic> statisticList;

    protected DefinedStatisticsSet()
    {
        DSset=null;
        statisticList=new Vector<Statistic>();
        statisticList.clear();

    }

    public static DefinedStatisticsSet getDefinedStatisticsSet()
    {
        if(DSset==null)
        {
            DSset=new DefinedStatisticsSet();
        }
        return DSset;
    }

    public void addStatistic(Statistic s)
    {

        /**
         *
         * Here can be added a protection against adding the same metric more than onec
         */

        this.statisticList.add(s);

    }
    public Vector<Statistic> getDefinedStatistics()
    {

        return this.statisticList;
    }
}
