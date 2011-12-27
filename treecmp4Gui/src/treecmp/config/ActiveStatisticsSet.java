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
public class ActiveStatisticsSet {

    private static ActiveStatisticsSet ASset;
    private Vector<Statistic> statisticList;

    protected ActiveStatisticsSet()
    {
        ASset=null;
        statisticList=new Vector<Statistic>();
        statisticList.clear();

    }

    public static ActiveStatisticsSet getActiveStatisticsSet()
    {
        if(ASset==null) {
            ASset=new ActiveStatisticsSet();
        }
        return ASset;
    }

    public void addStatistic(Statistic s)
    {

        /**
         *
         * Here can be added a protection against adding the same metric more than onec
         */

        this.statisticList.add(s);

    }
    public Vector<Statistic> getActiveStatistics()
    {
        return this.statisticList;
    }

    public Statistic[] getActiveStatisticsTable()
    {
        int size=this.statisticList.size();
        Statistic[] sTable=new Statistic[size];

        for(int i=0;i<size;i++) {
            sTable[i]=this.statisticList.get(i);

        }
        return sTable;
    }

}
