/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.statistic;

import pal.tree.Tree;

/**
 *
 * @author Damian
 */
public interface Statistic {
    
    public double getStatistic(Tree t);
    
    public String getName();

    public String getCommandLineName();

    public void setCommandLineName(String commandLineName);

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

}
