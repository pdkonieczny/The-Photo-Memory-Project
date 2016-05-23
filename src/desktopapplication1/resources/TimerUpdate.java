/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desktopapplication1.resources;

import java.awt.Label;
import java.util.TimerTask;
import javax.swing.JLabel;

/**
 *
 * @author Philip
 */
public class TimerUpdate extends TimerTask{
    
    Label label1;
    int time;
    int pause_time=0;
    boolean pause=false;
    int total=0;
    String totalString;
    
    public TimerUpdate(Label j){
        time=0;
        label1=j;
        totalString="";
       
    }
    
    public void setTotal(int t){
         total=t;
         int seconds= total%60;
            String strSeconds="";
            if(seconds<10){
                strSeconds="0"+seconds;
            }else{
                strSeconds=Integer.toString(seconds);
            }
            int minutes=(total/60)%60;
            String strMinutes="";

            if(minutes<10){
                strMinutes="0"+minutes;
            }else{
                strMinutes=Integer.toString(minutes);
            }
            int hours=(total/3600);
            String strhours="";

            if(hours<10){
                strhours="0"+hours;
            }else{
                strhours=Integer.toString(hours);
            }
            
            totalString="/ "+strhours+":"+strMinutes+":"+strSeconds;
    }
    
    public void pause(){
        pause_time=time;
        pause=true;
    }
    public void resume(){
        time=pause_time;
        pause=false;
    }
    
    @Override
    public void run() {
        if(!pause){
            time++;
            int seconds= time%60;
            String strSeconds="";
            if(seconds<10){
                strSeconds="0"+seconds;
            }else{
                strSeconds=Integer.toString(seconds);
            }
            int minutes=(time/60)%60;
            String strMinutes="";

            if(minutes<10){
                strMinutes="0"+minutes;
            }else{
                strMinutes=Integer.toString(minutes);
            }
            int hours=(time/3600);
            String strhours="";

            if(hours<10){
                strhours="0"+hours;
            }else{
                strhours=Integer.toString(hours);
            }


            label1.setText("Recording Time: "+strhours+":"+strMinutes+":"+strSeconds +totalString);
        }
    }
    
}
