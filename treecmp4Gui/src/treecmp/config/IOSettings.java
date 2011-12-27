/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.config;

/**
 *
 * @author Damian
 */
public class IOSettings {




    private static IOSettings IOConf;
    private String inputFile;
    private String outputFile;
    private String sSep;
    //defalut false
    private boolean calcCorrelation;

    public boolean isCalcCorrelation() {
        return calcCorrelation;
    }

    public void setCalcCorrelation(boolean calcCorrelation) {
        this.calcCorrelation = calcCorrelation;
    }

    private int iStep;

    public String getSSep() {
        return sSep;
    }

    public void setSSep(String sSep) {
        this.sSep = sSep;
    }

    public int getIStep() {
        return iStep;
    }

    public void setIStep(int iStep) {
        this.iStep = iStep;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
    

     protected IOSettings()
     {
         inputFile=null;
         outputFile=null;
         iStep=1;
         calcCorrelation=false;

     }
     public static IOSettings getIOSettings()
    {
        if(IOConf==null)
        {
            IOConf=new IOSettings();
        }
        return IOConf;
    }


}


