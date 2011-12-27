/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Damian
 */
public class TimeDate {

    public static final String DATE_FORMAT_NOW="yyyy-MM-dd HH-mm-ss";

    public static String now()
    {

        Calendar cal=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
        
    }




}
