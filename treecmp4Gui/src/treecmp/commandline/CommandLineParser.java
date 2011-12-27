/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.commandline;

import java.util.ListIterator;
import java.util.Vector;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.FileValidator;
import treecmp.config.*;
import treecmp.metric.Metric;
import treecmp.validators.FileValidatorEx;
import treecmp.validators.NumberValidatorEx;
import treecmp.command.*;
import treecmp.statistic.Statistic;

/**
 *
 * @author Damian
 */
public class CommandLineParser {
public static Command run (String args[])
{
    GroupBuilder gBuilder = new GroupBuilder();
    GroupBuilder gBuilder1 = new GroupBuilder();
    GroupBuilder gBuilder2 = new GroupBuilder();
    ArgumentBuilder aBuilder = new ArgumentBuilder();
    DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();


    NumberValidatorEx nvSize=NumberValidatorEx.getIntegerInstance();
    nvSize.setMinimum(2);

    FileValidator ivFile=FileValidatorEx.getExistingFileInstance();
    ivFile.setReadable(true);

  /* Option oW = oBuilder
           .withArgument(aSize)
           .withShortName("W").create();
           //withChildren(aSize).withShortName("debug").create();
*/
  /* DefaultOption oW=
           new DefaultOption(
            "",
            "",
            false,
            "W",
            "Print standard greeting",
            null,
            null,
            true,
            aSize,
            null,
            'W');
*/

    Option oS = oBuilder
        .withShortName("s")
        .withDescription("-Pair comparison mode.  Each two neighbouring trees are compared.")
        .create();
    
    Option oSeq = oBuilder
        .withShortName("seq")
        .withDescription("-Sequential comparison mode.")
        .create();

    Argument aWindowSize = aBuilder
        .withName("size")
        .withMinimum(1)
        .withMaximum(1)
        .withValidator(nvSize)
        .create();


    Option oW = oBuilder
        .withShortName("w")
        .withArgument(aWindowSize)
        .withDescription("-Window comparison mode.")
        .create();

  /*  Option oT = oBuilder
        .withShortName("t")
        .create();
*/
/*    Option oSUM = oBuilder
        .withShortName("sum")
        .create();
*/
  /*  Option oSTAT = oBuilder
        .withShortName("stat")
        .create();
*/
    Option oM = oBuilder
        .withShortName("m")
        .withDescription("-Matrix comparison mode. Each two trees are compared.")
        .create();

    Option oSum = oBuilder
        .withShortName("sum")
        .withDescription("-sum.")
        .create();

    Option oSM = oBuilder
        .withShortName("sm")
        .withDescription("-single matrix.")
        .create();

    Option oHist = oBuilder
        .withShortName("hist")
        .withDescription("-hostogram for single metric in matrix mode.")
        .create();

    Option oHist2 = oBuilder
        .withShortName("hist2")
        .withDescription("-hostogram for single metric in window 2 mode.")
        .create();

     Option oFM = oBuilder
        .withShortName("fm")
        .withDescription("-full matrix comparison.")
        .create();
    
   Argument aTreeListFile = aBuilder
        .withName("listOfTrees")
        .withMinimum(1)
        .withMaximum(1)
       .withValidator(ivFile)
        .create();

   Option oCons = oBuilder
        .withShortName("cons")
        .withArgument(aTreeListFile)
        .withDescription("-Finds consensus trees for single metric.")
        .create();



    Option oDEBUG = oBuilder
        .withShortName("debug")
        .create();


    Group gType1 = gBuilder
        .withOption(oW)
        .withOption(oS)
      //  .withOption(oT)
  //      .withOption(oSUM)
    //    .withOption(oSTAT)
        .withOption(oM)
       // .withOption(oDEBUG)
        .withOption(oSum)
        .withOption(oSM)
        .withOption(oFM)
        .withOption(oHist)
        .withOption(oHist2)
        .withOption(oCons)
        .withMaximum(1)
        .withMinimum(1)
        .withDescription("-Commands that can be run in pair mode. Only one command can be active.")
        .create();

    Group gType2 = gBuilder
        .withOption(oSeq)
        .withMaximum(1)
        .withMinimum(1)
        .withDescription("-Commands that can be run in sequence mode. Only one command can be active.")
        .create();


    //
    Argument aInputFile = aBuilder
        .withName("inputfile")
        .withMinimum(1)
        .withMaximum(1)
       .withValidator(ivFile)
        .create();

    Option oI = oBuilder
        .withShortName("i")
       // .withChildren(gType)
        .withArgument(aInputFile)
        .withDescription("-Defines an input file.")
        .withRequired(true)
        .create();

    Argument aStepSize = aBuilder
        .withName("stepsize")
        .withMinimum(1)
        .withMaximum(1)
       .withValidator(nvSize)
        .create();

    Option oStep = oBuilder
        .withShortName("j")
        .withArgument(aStepSize)
        .withDescription("-Defines size of step (jump) in reading input data. Min. value:2.")
        .create();

    Option oCorrelation = oBuilder
        .withShortName("c")
        .withDescription("-Calculate corrleation.")
        .create();



    Argument aOutputFile = aBuilder
        .withName("outputfile")
        .withMinimum(1)
        .withMaximum(1)
        .create();


    Option oO = oBuilder
        .withShortName("o")
        .withArgument(aOutputFile)
        .withDescription("-Defines an output file.")
        .withRequired(true)
        .create();


    Option oHelp = oBuilder
        .withShortName("h")
        .withShortName("?")
        .withLongName("help")
        .withDescription("-Prints help.")
        .create();

DefinedMetricsSet DMSet=DefinedMetricsSet.getDefinedMetricsSet();

Vector<Metric> DMetrics=DMSet.getDefinedMetrics();

ListIterator<Metric> itDM=DMetrics.listIterator();

GroupBuilder gMBuilder = new GroupBuilder();
gMBuilder.reset();
while(itDM.hasNext())
{
    Metric m=itDM.next();
    Option opt=oBuilder
        .withLongName(m.getCommandLineName())
        .withDescription(m.getDescription())
        .create();

    gMBuilder.withOption(opt);

}
Group gMetric = gMBuilder
        .withMinimum(1)
        .create();
//statistics group
DefinedStatisticsSet DSSet=DefinedStatisticsSet.getDefinedStatisticsSet();

Vector<Statistic> DStatistics=DSSet.getDefinedStatistics();

ListIterator<Statistic> itSM=DStatistics.listIterator();

GroupBuilder gSBuilder = new GroupBuilder();
gSBuilder.reset();
while(itSM.hasNext())
{
    Statistic s=itSM.next();
    Option opt=oBuilder
        .withLongName(s.getCommandLineName())
        .withDescription(s.getDescription())
        .create();

    gSBuilder.withOption(opt);

}
Group gStatistic = gSBuilder
        .withMinimum(1)
        .create();



   /* HelpFormatter formatter2=new HelpFormatter();

   formatter2.setGroup(gMetric);
   formatter2.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_OUTER);
   formatter2.getFullUsageSettings().add(DisplaySetting.DISPLAY_OPTIONAL);

   formatter2.printUsage();
*/
/*
Option oBS=oBuilder
        .withLongName("bs")
        .create();

Option oNodal=oBuilder
        .withLongName("nodal")
        .create();

Option oRF=oBuilder
        .withLongName("rf")
        .create();

Option oQuartet=oBuilder
        .withLongName("qt")
        .create();

Group gMetric = gBuilder
        .withOption(oBS)
        .withOption(oNodal)
        .withOption(oRF)
        .withOption(oQuartet)
        .withMinimum(1)
        .create();
*/

Option oMetric = oBuilder
        .withShortName("d")
        .withChildren(gMetric)
        .withDescription("-Defines a set of metrics which will be used in the analysis.")
        .withRequired(true)
        .create();

Option oStatistic = oBuilder
        .withShortName("st")
        .withChildren(gStatistic)
        .withDescription("-Defines a set of statitics which will be used in the analysis.")
        .withRequired(true)
        .create();

 

       Group gOptions1=gBuilder1
           .withOption(gType1)
            .withOption(oI)
            .withOption(oStep)
            .withOption(oCorrelation)
            .withOption(oMetric)
            .withOption(oO)
            .withOption(oHelp)
            .create();

       Group gOptions2=gBuilder2
           .withOption(gType2)
            .withOption(oI)
            .withOption(oStep)
            .withOption(oCorrelation)
            .withOption(oStatistic)
            .withOption(oO)
            .withOption(oHelp)
            .create();

       Group gOptions=gBuilder
           .withOption(gOptions1)
         /*  .withOption(gOptions2)*/
           .withMinimum(1)
           .withMaximum(1)
           .create();


/*
    Option oCmd = oBuilder
        .withChildren(gType)
        .withShortName("debug")
        .create();
*/

  /*  Group gOptions = gBuilder
        .withOption(oCmd)
        .create();
*/

    /*
      commands.put("-s", new RunSCommand(0,"-s"));
    commands.put("-m", new RunMCommand(0,"-m"));
    commands.put("-t", new RunTCommand(0,"-t"));
    commands.put("-w", new RunWCommand(1,"-w"));
    commands.put("-stat", new RunStatCommand(0,"-stat"));
    commands.put("-sum", new RunStatCommand(0,"-sum"));
    commands.put("-debug", new RunDebugCommand(0,"-debug"));
    commands.put("-h", new RunHCommand(0,"-h")); 
      
         
     
     */
/*
    Group gCmd=gBuilder
            .withOption(gType)
            .withOption(oI)
            .withOption(oO)
            .create();
  */

    //getting version from manifest file
    String version=CommandLineParser.class.getPackage().getImplementationVersion();
    if(version==null) version="";

    HelpFormatter formatter=new HelpFormatter("| ","   ", " |",78);
    formatter.setShellCommand("java -jar treecmp.jar");
    formatter.setHeader("TreeCmp "+version+" Application Help");
    formatter.setFooter("Copyright @Damian Bogdanowicz");
    formatter.setDivider("|----------------------------------------------------------------------------|");
   formatter.setGroup(gOptions);
   // formatter.printHelp( "ant", gOptions );
    //formatter.
/*
   formatter.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_NAME);
formatter.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
//formatter.getFullUsageSettings().remove(DisplaySetting.DISPLAY_GROUP_EXPANDED);
formatter.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_OUTER);
//formatter.getFullUsageSettings().remove(DisplaySetting.DISPLAY_GROUP_OUTER);
formatter.getFullUsageSettings().add(DisplaySetting.DISPLAY_OPTIONAL);
formatter.getLineUsageSettings().add(DisplaySetting.DISPLAY_GROUP_OUTER);
formatter.printUsage();

    formatter.printWrapped("ssss");
*/
    Parser parser = new Parser();
    //PosixParser();
    parser.setGroup(gOptions1);
    parser.setHelpOption(oHelp);
    parser.setHelpFormatter(formatter);


    CommandLine commandLine = parser.parseAndHelp(args);

    if(commandLine != null) {
    // process these values


        //set IO settings
        String inputFileName=(String)commandLine.getValue(oI);
        String outputFileName=(String)commandLine.getValue(oO);
        IOSettings IOset=IOSettings.getIOSettings();

        IOset.setInputFile(inputFileName);
        IOset.setOutputFile(outputFileName);

        if(commandLine.hasOption(oStep))
        {         
            String sStep=(String)commandLine.getValue(oStep); 
            int iStep=Integer.parseInt(sStep);            
            IOset.setIStep(iStep);
        }

        //correlation settings
        if(commandLine.hasOption(oCorrelation)){
            IOset.setCalcCorrelation(true);
        }


        //set active metrics
        ActiveMetricsSet AMSet=ActiveMetricsSet.getActiveMetricsSet();
        
        DMSet=DefinedMetricsSet.getDefinedMetricsSet();
        DMetrics=DMSet.getDefinedMetrics();
        itDM=DMetrics.listIterator();

        while(itDM.hasNext()){
            Metric m=itDM.next();
            if(commandLine.hasOption("--"+m.getCommandLineName())){
                AMSet.addMetric(m);
            }
        }

         //set active statistics
        ActiveStatisticsSet ASSet=ActiveStatisticsSet.getActiveStatisticsSet();

        DSSet=DefinedStatisticsSet.getDefinedStatisticsSet();
        DStatistics=DSSet.getDefinedStatistics();
        itSM=DStatistics.listIterator();

        while(itSM.hasNext()){
            Statistic s=itSM.next();
            if(commandLine.hasOption("--"+s.getCommandLineName())){
                ASSet.addStatistic(s);
            }
        }


        //set active command
        Command cmd=null;
        if (commandLine.hasOption(oW)) {
            String sWindowSize = (String) commandLine.getValue(oW);
            int iWindowSize = Integer.parseInt(sWindowSize);
            cmd = new RunWCommand(1, "-w", iWindowSize);
        }
        if (commandLine.hasOption(oM)) {
            cmd = new RunMCommand(0, "-m");
        }
        if (commandLine.hasOption(oS)) {
            cmd = new RunSCommand(0, "-s");
        }
        if (commandLine.hasOption(oSum)) {
            cmd = new RunSumCommand(0, "-sum");
        }
        if (commandLine.hasOption(oSM)) {
            cmd = new RunSMCommand(0, "-sm");
        }
        if (commandLine.hasOption(oHist)) {
            cmd = new RunHistCommand(0, "-hist");
        }
        if (commandLine.hasOption(oHist2)) {
            cmd = new RunHist2Command(0, "-hist2");
        }
        if (commandLine.hasOption(oCons)) {
            String inputListOfTrees = (String) commandLine.getValue(oCons);
            cmd = new RunConsCommand(1, "-cons", inputListOfTrees);
        }
       if (commandLine.hasOption(oFM)) {
            cmd = new RunFMCommand(0, "-fm");
        }
       if (commandLine.hasOption(oSeq)) {
            cmd = new RunSeqCommand(0, "-seq");
        }

        return cmd;
       
    }else
    {
        //Error during parsing command line
        return null;
    }
}
}
