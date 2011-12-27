/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import treecmp.metric.Metric;
import treecmp.statistic.Statistic;

/**
 *
 * @author Damian
 * 
 * ConfigSettings class is implemeted as singleton
 */
public class ConfigSettings {

    private static ConfigSettings config;

    protected ConfigSettings() {
        config = null;

    }

    public static ConfigSettings getConfig() {
        if (config == null) {
            config = new ConfigSettings();

        }
        return config;
    }

    public void readConfigFromFile() {
    }

    public void readConfigFromFile(File xmlFile) {


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Use the factory to create a builder
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            String className = "";
            String metricName = "";
            String commandLineName = "";
            String metricDesc="";

            String statisticName = "";
            String statisticDesc="";

            /**
             * Update defined metric set
             * 
             */
            DefinedMetricsSet DMset = DefinedMetricsSet.getDefinedMetricsSet();

            NodeList list = doc.getElementsByTagName("metric");
            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);
                //System.out.println(getTextValue(element, "class"));
                className = getTextValue(element, "class");
                metricName = getTextValue(element, "name");
                commandLineName = getTextValue(element, "command_name");
                metricDesc=getTextValue(element, "description");

                if(className!=null) {
                    Class cl = Class.forName(className);
                    Metric m=(Metric) cl.newInstance();

                    m.setName(metricName);
                    m.setCommandLineName(commandLineName);
                    m.setDescription(metricDesc);
                    DMset.addMetric(m);
                }
            }

            //parse statistic section

            DefinedStatisticsSet DSset = DefinedStatisticsSet.getDefinedStatisticsSet();

            list = doc.getElementsByTagName("statistic");
            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);
                //System.out.println(getTextValue(element, "class"));
                className = getTextValue(element, "class");
                statisticName = getTextValue(element, "name");
                commandLineName = getTextValue(element, "command_name");
                statisticDesc=getTextValue(element, "description");

                if(className!=null) {
                    Class cl = Class.forName(className);
                    Statistic s=(Statistic) cl.newInstance();

                    s.setName(statisticName);
                    s.setCommandLineName(commandLineName);
                    s.setDescription(statisticDesc);
                    DSset.addStatistic(s);
                }
            }

            list = doc.getElementsByTagName("reporting");
            Element element = (Element) list.item(0);
            String sSep=getTextValue(element, "filed_separator");
            IOSettings IOs=IOSettings.getIOSettings();

            if(sSep.compareTo("tab")==0) {
                IOs.setSSep("\t");
            } else {
                IOs.setSSep(sSep);
            }
            

        } catch (SAXException ex) {
            Logger.getLogger(ConfigSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ConfigSettings.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is 'name' I will return John
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
            textVal=textVal.trim();
        }

        return textVal;
    }
}
