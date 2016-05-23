/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desktopapplication1;

import java.util.Date;
import java.util.Calendar;

/**
 *
 * @author Philip
 */
public class audioFile {
    public String Author;
    public Calendar date;
    public String Location;
    public String description;
    public String fileName;
    
    
    public audioFile(String a, String l, String d, long e,String f){
        Author=a;
        Location=l;
        description=d;
        date= Calendar.getInstance();
        date.setTimeInMillis(e);
        fileName=f;
    }
     
    
}
