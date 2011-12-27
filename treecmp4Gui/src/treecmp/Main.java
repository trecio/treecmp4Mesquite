/*
 * Main.java
 *
 * Created on 8 marzec 2007, 19:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package treecmp;


import treecmp.common.TimeDate;
import java.io.File;
import java.util.Hashtable;
import treecmp.command.*;
import treecmp.commandline.CommandLineParser;
import treecmp.config.*;



/**
 *
 * @author VOX
 */
public class Main {

    /** Creates a new instance of Main */


    public Main() {
    }

    /**
     * @param args the command line arguments
     */
  
    //public final String runtimePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

    public static void main(String[] args) {

        //networkx test
        
   //     NetworkxTest.test();
        //

       String runtimePath =Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
       String conf="";

       String version=Main.class.getPackage().getImplementationVersion();

       if(version==null){
           conf=runtimePath+"../"+PersistentInfo.configFile;
           
       }else{
           String tempPath=runtimePath.substring(0,runtimePath.lastIndexOf("/")+1);
           conf=tempPath+PersistentInfo.configFile;
       } 
       //System.out.println(conf);
       File confXmlFile=new File(conf);
        
        ConfigSettings config=ConfigSettings.getConfig();
                      
        config.readConfigFromFile(confXmlFile);

        Command cmd=CommandLineParser.run(args);

        if(cmd!=null)
        {

            TreeReader reader = new TreeReader(IOSettings.getIOSettings().getInputFile());
            //scanning all content of the input file
            reader.open();

            System.out.println(TimeDate.now()+": Start of scanning input file: "+IOSettings.getIOSettings().getInputFile());
            int numberOfTrees=reader.scan();
            reader.close();
            System.out.println(TimeDate.now()+": End of scanning input file: "+IOSettings.getIOSettings().getInputFile());
            System.out.println(TimeDate.now()+": "+numberOfTrees+" valid trees found in file: "+IOSettings.getIOSettings().getInputFile());


            reader.setStep(IOSettings.getIOSettings().getIStep());
            cmd.setReader(reader);
            
            ResultWriter out = new ResultWriter();
            out.isWriteToFile(true);
            out.setFileName(IOSettings.getIOSettings().getOutputFile());
            cmd.setOut(out);

            cmd.run();
            

        }

        //parseAndRunCommand(args);
        
   
    }


private static void parseAndRunCommand(String[] args)
{
    Hashtable<String,Command> commands=new Hashtable<String,Command>(10);
    
    commands.put("-s", new RunSCommand(0,"-s"));
    commands.put("-m", new RunMCommand(0,"-m"));
    commands.put("-t", new RunTCommand(0,"-t"));
    commands.put("-w", new RunWCommand(1,"-w"));
    commands.put("-stat", new RunStatCommand(0,"-stat"));
    commands.put("-sum", new RunSumCommand(0,"-sum"));
    commands.put("-debug", new RunDebugCommand(0,"-debug"));
    commands.put("-h", new RunHistCommand(0,"-h"));
    commands.put("-fm", new RunFMCommand(0,"-fm"));
    commands.put("-seq", new RunSeqCommand(0,"-seq"));

    String commanLineOption=args[0];
    Command commandToRun;
    
    commandToRun =commands.get(commanLineOption);
            
    if(commandToRun!=null)
    {
        //running command
        
        if(commandToRun.init(args))
            commandToRun.run();
        else
        {
            
           System.out.println("Error during intialization command arguments!!!"); 
        }
        
    }else
    {
        System.out.println("Syntax 1: start.bat -w windowSize inputFile [outputFile]");
        System.out.println("Syntax 2: start.bat [-s|-m|-fm|-stst|-t] inputFile [outputFile]");

    }
    

}
    
    
    

}
