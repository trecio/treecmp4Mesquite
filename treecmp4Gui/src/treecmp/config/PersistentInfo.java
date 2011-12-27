/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.config;

/**
 *
 * @author Damian
 */
public class PersistentInfo {

    
        
        public final String path= getClass().getProtectionDomain().getCodeSource().getLocation().toString();
    
    public final static String configPath="../config";
    public final static String configFile=configPath+"/config.xml";
   
}
