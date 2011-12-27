/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.statistic;

/**
 *
 * @author Damian
 */
public class BaseStatistic {

    protected String name;
    protected String commandLineName;
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public BaseStatistic() {
    }

    public String getCommandLineName() {
        return commandLineName;
    }

    public void setCommandLineName(String commandLineName) {
        this.commandLineName = commandLineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
