/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.command;

import java.io.File;
import treecmp.config.ConfigSettings;

/**
 *
 * @author Damian
 */
public class RunDebugCommand extends Command {

    public RunDebugCommand(int paramNumber, String name) {
        super(paramNumber, name);
    }

    @Override
    public void run() {
        super.run();

    String _args[]=this.getArgs();

        ConfigSettings config=ConfigSettings.getConfig();

        File file=new File(_args[1]);
        config.readConfigFromFile(file);

    }

}
