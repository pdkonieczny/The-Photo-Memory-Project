/*
 * DesktopApplication1View.java
 */

package desktopapplication1;


import java.awt.Button;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * The application's main frame.
 */
public class DesktopApplication1View extends FrameView {
    
    String PictureOpened;

    public DesktopApplication1View(SingleFrameApplication app) {
        super(app);

        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dim.width -= 100;
        dim.height -= 100;
       
        System.out.println(jLabel2.getWidth() +" "+ jLabel2.getHeight());
        System.out.println(jLabel2.getIcon().getIconWidth()+ " "+jLabel2.getIcon().getIconHeight());
        
        this.getFrame().setSize(dim);
        this.getFrame().setPreferredSize(dim);
        this.getFrame().setMinimumSize(dim);
        this.getFrame().setResizable(false); 
        
        File f= new File(System.getProperty("user.dir")+"\\projects");
        
        if(!f.exists()){
            try{
                f.mkdir();
            }catch(Exception e){
                
                System.out.println("ERROR CREATING PROJECTS FOLDER");
            }
        }
        File f2= new File(System.getProperty("user.dir")+"\\projects\\audioFiles");
        if(!f2.exists()){
            try{
                f2.mkdir();
            }catch(Exception e){
                
                System.out.println("ERROR CREATING audioFiles FOLDER");
            }
        }
        File f3= new File(System.getProperty("user.dir")+"\\projects\\pictures");
        if(!f3.exists()){
            try{
                f3.mkdir();
            }catch(Exception e){
                
                System.out.println("ERROR CREATING picutres FOLDER");
            }
        }
        

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
               
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                       
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                   
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    
                }
            }
        });
        
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            for (int i = 0; i < mixerInfos.length; i++)
            {
                Mixer mixer = AudioSystem.getMixer(mixerInfos[i]);
                Line.Info[] targetLineInfos = mixer.getTargetLineInfo();

                for (int j = 0; j < targetLineInfos.length; j++)
                {
                    setVolume(targetLineInfos[j], (float)1.0);
                }
                mixer.close();
            }
        
        
        
        
        
        buttonLabels = new JTextArea[6];
        buttons=new Button[6];
        buttons[0]=AudioButton1;
        buttons[1]=AudioButton2;
        buttons[2]=AudioButton3;
        buttons[3]=AudioButton4;
        buttons[4]=AudioButton5;
        buttons[5]=AudioButton6;
        buttonLabels[0]=jTextArea1;
        buttonLabels[1]=jTextArea2;
        buttonLabels[2]=jTextArea3;
        buttonLabels[3]=jTextArea4;
        buttonLabels[4]=jTextArea5;
        buttonLabels[5]=jTextArea6;
        setRecentProjects(null);
        
        this.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(projectOpen){
                    int s=JOptionPane.showConfirmDialog(DesktopApplication1.getApplication().getMainFrame(), "Do you want want to save changes you made to "+fileOpened, " Picture Memory Project", JOptionPane.YES_NO_CANCEL_OPTION);
                    if(s==JOptionPane.CANCEL_OPTION){
                        return;
                    }else if(s==JOptionPane.YES_OPTION){
                        saveFile();
                        System.out.println("save");
                    }
                }
                System.exit(0);
            }
        });
        
    }

    
    private void setVolume(Line.Info lineInfo, float value) {
            Line line=null;
            try {
                line = AudioSystem.getLine(lineInfo);
                line.open();
                
                FloatControl control = (FloatControl)line.getControl(FloatControl.Type.VOLUME);
                control.setValue(value);
                line.close();
                
            } catch(Exception e) {
                
                if(line!=null)
                    line.close();
            }
}
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            aboutBox = new DesktopApplication1AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DesktopApplication1.getApplication().show(aboutBox);
     
    }
    
    public boolean showRecordingInfoBox(int but){
        
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            RecordInfo= new NewOkCancelDialog(mainFrame, projectOpen,but,audios,fileOpened);
            RecordInfo.setLocationRelativeTo(mainFrame);
            if(audios[but]!=null){
                RecordInfo.textField1.setText(audios[but].Author);
                RecordInfo.textField2.setText(audios[but].Location);
                RecordInfo.textArea1.setText(audios[but].description);
            }
        
        DesktopApplication1.getApplication().show(RecordInfo);
        
        if(RecordInfo.getReturnStatus()==0){
            RecordInfo=null;
            return false; //cancelled
            
        }
        else
            RecordInfo=null;
            return true;
      
    }
    
    public void showRecordingStartStop(int num){
        
        ImageIcon imic= new ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\"+jLabel4.getText());
        ImageZoomerFrame imageZoomer= new ImageZoomerFrame(imic.getImage(),10,jLabel4.getText(),fileOpened,num);
        
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jFileChooser1 = new javax.swing.JFileChooser();
        jButton2 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        AudioButton4 = new java.awt.Button();
        AudioButton1 = new java.awt.Button();
        AudioButton5 = new java.awt.Button();
        AudioButton6 = new java.awt.Button();
        AudioButton2 = new java.awt.Button();
        jLabel2 = new javax.swing.JLabel();
        AudioButton3 = new java.awt.Button();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1View.class);
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        menuBar.setAlignmentY(5.0F);
        menuBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menuBar.setDoubleBuffered(true);
        menuBar.setFocusCycleRoot(true);
        menuBar.setName("menuBar"); // NOI18N
        menuBar.setNextFocusableComponent(menuBar);

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setDoubleBuffered(true);
        fileMenu.setFocusCycleRoot(true);
        fileMenu.setFocusPainted(true);
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setNextFocusableComponent(menuBar);
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem3);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setEnabled(false);
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem7);

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem20.setText(resourceMap.getString("jMenuItem20.text")); // NOI18N
        jMenuItem20.setEnabled(false);
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem20);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setEnabled(false);
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem6);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setEnabled(false);
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenu1.add(jMenu2);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setEnabled(false);
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenu1.add(jMenu3);

        jMenu4.setText(resourceMap.getString("jMenu4.text")); // NOI18N
        jMenu4.setEnabled(false);
        jMenu4.setName("jMenu4"); // NOI18N

        jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem12);

        jMenuItem13.setText(resourceMap.getString("jMenuItem13.text")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem13);

        jMenu1.add(jMenu4);

        jMenu5.setText(resourceMap.getString("jMenu5.text")); // NOI18N
        jMenu5.setEnabled(false);
        jMenu5.setName("jMenu5"); // NOI18N

        jMenuItem14.setText(resourceMap.getString("jMenuItem14.text")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText(resourceMap.getString("jMenuItem15.text")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenu1.add(jMenu5);

        jMenu6.setText(resourceMap.getString("jMenu6.text")); // NOI18N
        jMenu6.setEnabled(false);
        jMenu6.setName("jMenu6"); // NOI18N

        jMenuItem16.setText(resourceMap.getString("jMenuItem16.text")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem16);

        jMenuItem17.setText(resourceMap.getString("jMenuItem17.text")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem17);

        jMenu1.add(jMenu6);

        jMenu7.setText(resourceMap.getString("jMenu7.text")); // NOI18N
        jMenu7.setEnabled(false);
        jMenu7.setName("jMenu7"); // NOI18N

        jMenuItem18.setText(resourceMap.getString("jMenuItem18.text")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem18);

        jMenuItem19.setText(resourceMap.getString("jMenuItem19.text")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem19);

        jMenu1.add(jMenu7);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getActionMap(DesktopApplication1View.class, this);
        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setDebugGraphicsOptions(javax.swing.DebugGraphics.BUFFERED_OPTION);
        jPanel1.setMaximumSize(new java.awt.Dimension(725, 446));
        jPanel1.setMinimumSize(new java.awt.Dimension(725, 446));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(725, 446));

        AudioButton4.setActionCommand(resourceMap.getString("AudioButton4.actionCommand")); // NOI18N
        AudioButton4.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton4.setEnabled(false);
        AudioButton4.setLabel(resourceMap.getString("AudioButton4.label")); // NOI18N
        AudioButton4.setName("AudioButton4"); // NOI18N
        AudioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton4ActionPerformed(evt);
            }
        });

        AudioButton1.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton1.setEnabled(false);
        AudioButton1.setLabel(resourceMap.getString("AudioButton1.label")); // NOI18N
        AudioButton1.setName("AudioButton1"); // NOI18N
        AudioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton1ActionPerformed(evt);
            }
        });

        AudioButton5.setActionCommand(resourceMap.getString("AudioButton5.actionCommand")); // NOI18N
        AudioButton5.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton5.setEnabled(false);
        AudioButton5.setLabel(resourceMap.getString("AudioButton5.label")); // NOI18N
        AudioButton5.setName("AudioButton5"); // NOI18N
        AudioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton5ActionPerformed(evt);
            }
        });

        AudioButton6.setActionCommand(resourceMap.getString("AudioButton6.actionCommand")); // NOI18N
        AudioButton6.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton6.setEnabled(false);
        AudioButton6.setLabel(resourceMap.getString("AudioButton6.label")); // NOI18N
        AudioButton6.setName("AudioButton6"); // NOI18N
        AudioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton6ActionPerformed(evt);
            }
        });

        AudioButton2.setActionCommand(resourceMap.getString("AudioButton2.actionCommand")); // NOI18N
        AudioButton2.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton2.setEnabled(false);
        AudioButton2.setLabel(resourceMap.getString("AudioButton2.label")); // NOI18N
        AudioButton2.setName("AudioButton2"); // NOI18N
        AudioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton2ActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\PageNotFound.jpg"));
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setDoubleBuffered(true);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setIconTextGap(0);
        jLabel2.setMaximumSize(new java.awt.Dimension(100000, 100000));
        jLabel2.setMinimumSize(new java.awt.Dimension(1, 1));
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(100000, 100000));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        AudioButton3.setActionCommand(resourceMap.getString("AudioButton3.actionCommand")); // NOI18N
        AudioButton3.setBackground(resourceMap.getColor("AudioButton6.background")); // NOI18N
        AudioButton3.setEnabled(false);
        AudioButton3.setLabel(resourceMap.getString("AudioButton3.label")); // NOI18N
        AudioButton3.setName("AudioButton3"); // NOI18N
        AudioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AudioButton3ActionPerformed(evt);
            }
        });

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(null);
        jTextArea1.setDoubleBuffered(true);
        jTextArea1.setFocusable(false);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setBorder(null);
        jTextArea2.setDoubleBuffered(true);
        jTextArea2.setFocusable(false);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea3.setColumns(20);
        jTextArea3.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setWrapStyleWord(true);
        jTextArea3.setBorder(null);
        jTextArea3.setDoubleBuffered(true);
        jTextArea3.setFocusable(false);
        jTextArea3.setName("jTextArea3"); // NOI18N
        jScrollPane3.setViewportView(jTextArea3);

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextArea4.setColumns(20);
        jTextArea4.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setWrapStyleWord(true);
        jTextArea4.setBorder(null);
        jTextArea4.setDoubleBuffered(true);
        jTextArea4.setFocusable(false);
        jTextArea4.setName("jTextArea4"); // NOI18N
        jScrollPane4.setViewportView(jTextArea4);

        jScrollPane5.setBorder(null);
        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jTextArea5.setColumns(20);
        jTextArea5.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(5);
        jTextArea5.setWrapStyleWord(true);
        jTextArea5.setBorder(null);
        jTextArea5.setDoubleBuffered(true);
        jTextArea5.setFocusable(false);
        jTextArea5.setName("jTextArea5"); // NOI18N
        jScrollPane5.setViewportView(jTextArea5);

        jScrollPane6.setBorder(null);
        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane6.setDoubleBuffered(true);
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextArea6.setColumns(20);
        jTextArea6.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea6.setLineWrap(true);
        jTextArea6.setRows(5);
        jTextArea6.setWrapStyleWord(true);
        jTextArea6.setBorder(null);
        jTextArea6.setDoubleBuffered(true);
        jTextArea6.setFocusable(false);
        jTextArea6.setName("jTextArea6"); // NOI18N
        jScrollPane6.setViewportView(jTextArea6);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(AudioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(AudioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(AudioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(AudioButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(AudioButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(AudioButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                    .addComponent(jScrollPane6)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(346, 346, 346)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(336, 336, 336))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(AudioButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AudioButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AudioButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(AudioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AudioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AudioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addGap(6, 6, 6)
                .addComponent(jLabel4)
                .addGap(75, 75, 75))
        );

        AudioButton1.getAccessibleContext().setAccessibleName(resourceMap.getString("button10.AccessibleContext.accessibleName")); // NOI18N
        AudioButton5.getAccessibleContext().setAccessibleName(resourceMap.getString("button11.AccessibleContext.accessibleName")); // NOI18N

        setComponent(jPanel1);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, menuBar, org.jdesktop.beansbinding.ObjectProperty.create(), this, org.jdesktop.beansbinding.BeanProperty.create("menuBar"));
        bindingGroup.addBinding(binding);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    
    //open project
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       
       jFileChooser1.addChoosableFileFilter(new ImageFilter2());
       jFileChooser1.setAcceptAllFileFilterUsed(false);
       jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")+"\\projects\\"));
        int showOpenDialog = jFileChooser1.showOpenDialog(this.getFrame());
        if(showOpenDialog==JFileChooser.APPROVE_OPTION){
           
            System.out.println(jFileChooser1.getSelectedFile().getAbsolutePath());
            File project= new File(jFileChooser1.getSelectedFile().getAbsolutePath());
            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream(project);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String pictureName=br.readLine();
                jLabel2.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\"+pictureName));
                jLabel4.setText(pictureName);
                int number= Integer.parseInt(br.readLine());
                for(int i=0;i<number;i++){
                    int buttonNum=Integer.parseInt(br.readLine());
                    String f=br.readLine();
                    String a=br.readLine();
                    String l=br.readLine();
                    String d=br.readLine();
                    long e= Long.parseLong(br.readLine());
                    audios[buttonNum]=new audioFile(a,l,d,e,f);
                    if(buttonNum==0){
                        jTextArea1.setText("Author: "+a + "\nDate: "+(audios[0].date.get(Calendar.MONTH)+1)+"/"+audios[0].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[0].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton1.setLabel("Playback Audio");
                    }else if(buttonNum==1){
                        jTextArea2.setText("Author: "+a + "\nDate: "+(audios[1].date.get(Calendar.MONTH)+1)+"/"+audios[1].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[1].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton2.setLabel("Playback Audio");
                    }else if(buttonNum==2){
                        jTextArea3.setText("Author: "+a + "\nDate: "+(audios[2].date.get(Calendar.MONTH)+1)+"/"+audios[2].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[2].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton3.setLabel("Playback Audio");
                    }else if(buttonNum==3){
                        jTextArea4.setText("Author: "+a + "\nDate: "+(audios[3].date.get(Calendar.MONTH)+1)+"/"+audios[3].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[3].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton4.setLabel("Playback Audio");
                    }else if(buttonNum==4){
                        jTextArea5.setText("Author: "+a + "\nDate: "+(audios[4].date.get(Calendar.MONTH)+1)+"/"+audios[4].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[4].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton5.setLabel("Playback Audio");
                    }else if(buttonNum==5){
                        jTextArea6.setText("Author: "+a + "\nDate: "+(audios[5].date.get(Calendar.MONTH)+1)+"/"+audios[5].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[5].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                        AudioButton6.setLabel("Playback Audio");
                    }
                }


            } catch (Exception ex) {
                Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                              
            resize_IconImage();
            fileOpened =jFileChooser1.getSelectedFile().getAbsolutePath();
            
            setRecentProjects(fileOpened);
            
            AudioButton1.setEnabled(true);
            AudioButton2.setEnabled(true);
            AudioButton3.setEnabled(true);
            AudioButton4.setEnabled(true);
            AudioButton5.setEnabled(true);
            AudioButton6.setEnabled(true);
            jMenuItem2.setEnabled(true);
            jMenuItem1.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jMenuItem4.setEnabled(false);
            jMenuItem5.setEnabled(false);
            jMenuItem6.setEnabled(false);
            jMenuItem7.setEnabled(true);
            jMenuItem20.setEnabled(true);
            jMenu2.setEnabled(true);
            jMenu3.setEnabled(true);
            jMenu4.setEnabled(true);
            jMenu5.setEnabled(true);
            jMenu6.setEnabled(true);
            jMenu7.setEnabled(true);
            projectOpen=true;
        }else{
         //do nothing since user cancelled
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    //close project
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        String pictureName=jLabel4.getText();
        jLabel4.setText("");
        jLabel2.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\PageNotFound.jpg"));
        AudioButton1.setLabel("Record Audio");
        AudioButton2.setLabel("Record Audio");
        AudioButton3.setLabel("Record Audio");
        AudioButton4.setLabel("Record Audio");
        AudioButton5.setLabel("Record Audio");
        AudioButton6.setLabel("Record Audio");
        jTextArea1.setText("");
        jTextArea2.setText("");
        jTextArea3.setText("");
        jTextArea4.setText("");
        jTextArea5.setText("");
        jTextArea6.setText("");
        AudioButton1.setEnabled(false);
        AudioButton2.setEnabled(false);
        AudioButton3.setEnabled(false);
        AudioButton4.setEnabled(false);
        AudioButton5.setEnabled(false);
        AudioButton6.setEnabled(false);
        jMenuItem2.setEnabled(false);
        jMenuItem1.setEnabled(true);
        jMenuItem3.setEnabled(true);
        jMenuItem4.setEnabled(true);
        jMenuItem5.setEnabled(true);
        jMenuItem6.setEnabled(true);
        jMenuItem7.setEnabled(false);
        jMenuItem20.setEnabled(false);
        jMenu2.setEnabled(false);
        jMenu3.setEnabled(false);
        jMenu4.setEnabled(false);
        jMenu5.setEnabled(false);
        jMenu6.setEnabled(false);
        jMenu7.setEnabled(false);
        projectOpen=false;
        try {
            FileWriter outFile = new FileWriter(fileOpened);
            PrintWriter out = new PrintWriter(outFile);
            int actives=0;
            out.println(pictureName);
            for(int i=0;i<audios.length;i++){
                if(audios[i]!=null){
                    actives++;
                }
            }
            out.println(actives);
            for(int i=0;i<audios.length;i++){
                if(audios[i]!=null){
                    out.println(i);
                    out.println(audios[i].fileName);
                    out.println(audios[i].Author);
                    out.println(audios[i].Location);
                    out.println(audios[i].description);
                    out.println(audios[i].date.getTimeInMillis());
                }
                audios[i]=null;
            }
            outFile.close();
            out.close();
        }catch(Exception e){}
        DesktopApplication1.getApplication().getMainFrame().setTitle("The Photo Memory Project");
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    private void buttonClicked(int num){
        if(buttons[num].getLabel().equals("Record Audio")){
            boolean ok_clicked=showRecordingInfoBox(num);
            if(ok_clicked){
                buttonLabels[num].setText("Author: "+audios[num].Author + "\nDate: "+ (audios[num].date.get(Calendar.MONTH)+1) +"/"+audios[num].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[num].date.get(Calendar.YEAR)+"\nLocation: "+audios[num].Location+"\nDescription: "+ audios[num].description);
                buttons[num].setLabel("Playback Audio");
                showRecordingStartStop(num);
                saveFile();
            }
            
        }else{
            
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            //mainFrame.setEnabled(false);
            String temp=fileOpened.replaceAll("\\\\", "_");
            temp=temp.replaceAll(":", "_");
           File	soundFile = new File(audios[num].fileName); 
           if(!soundFile.exists()){
               int answer=JOptionPane.showConfirmDialog(DesktopApplication1.getApplication().getMainFrame(),"Someone has deleted or moved the audio file: \n"+soundFile.getAbsolutePath()+"\nWould you like to locate it?","File Not Found Error",JOptionPane.YES_NO_OPTION);
               if(answer==JOptionPane.YES_OPTION){
                    jFileChooser1.addChoosableFileFilter(new ImageFilter3());
                    jFileChooser1.setAcceptAllFileFilterUsed(false);
                    jFileChooser1.setDialogTitle("Select Audio File");
                    jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
                   int showOpenDialog = jFileChooser1.showOpenDialog(this.getFrame());
                   if(showOpenDialog== JFileChooser.APPROVE_OPTION){
                       audios[num].fileName=jFileChooser1.getSelectedFile().getAbsolutePath();
                       saveFile();
                   }else{
                       return;
                   }
               }else{
                   return;
               }
           }
           playback = new playback_startstop(mainFrame, true, audios[num].fileName);
           playback.setLocationRelativeTo(mainFrame); 
           
           DesktopApplication1.getApplication().show(playback);
            //mainFrame.setEnabled(true);
            playback=null;
            
        }
    }
            
        //new project
        private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
           String s;
           File f;
            do{
               s = (String)JOptionPane.showInputDialog(this.getFrame(),"Enter the Name for the Project:\n","Project Name",JOptionPane.PLAIN_MESSAGE,null,null,"");
               
               if(s.endsWith(".pmp"))
                   s=s.substring(0, s.length()-4);
               System.out.println(s);
               f = new File(System.getProperty("user.dir")+"\\projects\\"+s+".pmp");
               if(f.exists()){
                   JOptionPane.showMessageDialog(this.getFrame(), "A file by that name already exists.");
               }
               
           }
           while ( s.length()==0 || f.exists());
            
           if(s==null)
               return;
           
           jFileChooser1.addChoosableFileFilter(new ImageFilter());
           jFileChooser1.setAcceptAllFileFilterUsed(false);
           jFileChooser1.setDialogTitle("Select Picture for Project");
           jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
           int showOpenDialog = jFileChooser1.showOpenDialog(this.getFrame());
           if(showOpenDialog== JFileChooser.APPROVE_OPTION){
            
               
              //copy over picture to pictures folder
              File pictFile=new File(System.getProperty("user.dir")+"\\projects\\pictures\\"+jFileChooser1.getSelectedFile().getName());
              if(!pictFile.exists()){
                   try{
                        InputStream in = new FileInputStream(new File(jFileChooser1.getSelectedFile().getAbsolutePath()));
                        OutputStream out = new FileOutputStream(pictFile);

                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                            in.close();
                            out.close();
                    }catch(Exception e){System.out.println("error copying"); return;}
                   
              }
            
            fileOpened=System.getProperty("user.dir")+"\\projects\\"+s+".pmp";
            DesktopApplication1.getApplication().getMainFrame().setTitle(fileOpened);
            setRecentProjects(fileOpened);
            projectOpen=true;
            try {
                FileWriter outFile = new FileWriter(System.getProperty("user.dir")+"\\projects\\"+s+".pmp");
                PrintWriter out = new PrintWriter(outFile);
                int actives=0;
                out.println(jFileChooser1.getSelectedFile().getName());
                out.println(0);
                out.close();
              
           }catch(Exception e){System.out.println("error creating"); return;}
            jLabel2.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\"+jFileChooser1.getSelectedFile().getName()));
            resize_IconImage();
            jLabel4.setText(jFileChooser1.getSelectedFile().getName());
            AudioButton1.setEnabled(true);
            AudioButton2.setEnabled(true);
            AudioButton3.setEnabled(true);
            AudioButton4.setEnabled(true);
            AudioButton5.setEnabled(true);
            AudioButton6.setEnabled(true);
            jMenuItem3.setEnabled(false);
            jMenuItem2.setEnabled(true);
            jMenuItem1.setEnabled(false);
            jMenuItem4.setEnabled(false);
            jMenuItem5.setEnabled(false);
            jMenuItem6.setEnabled(false);
            jMenuItem7.setEnabled(true);
            jMenuItem20.setEnabled(true);
            jMenu2.setEnabled(true);
            jMenu3.setEnabled(true);
            jMenu4.setEnabled(true);
            jMenu5.setEnabled(true);
            jMenu6.setEnabled(true);
            jMenu7.setEnabled(true);
            
           }
           
        }//GEN-LAST:event_jMenuItem3ActionPerformed

        private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
            saveFile();
        }//GEN-LAST:event_jMenuItem7ActionPerformed
        
        private void setRecentProjects(String name){
            FileInputStream fstream = null;
                try {
                    fstream = new FileInputStream(new File(System.getProperty("user.dir")+"\\projects\\"+"recent.txt"));
                    // Get the object of DataInputStream
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    int number= Integer.parseInt(br.readLine());
                    for(int i=0;i<number;i++){
                        String a=br.readLine();
                        if(i==0){
                            jMenuItem4.setText(a);
                        }else if(i==1){
                            jMenuItem5.setText(a);
                        }else{
                            jMenuItem6.setText(a);
                        }
                    }


                } catch (Exception ex) {
                    System.out.println(ex);
                    jMenuItem4.setText("");
                    jMenuItem5.setText("");
                    jMenuItem6.setText("");
                } finally {
                    try {
                        fstream.close();
                    } catch (Exception ex) {
                        
                    }
                }
               
                if(name!=null)                    
                            setRecentButtons(name);
               
               
               
                try {
                    FileWriter outFile = new FileWriter(System.getProperty("user.dir")+"\\projects\\recent.txt");
                    PrintWriter out = new PrintWriter(outFile);
                    if(jMenuItem4.getText().equals("")){
                        jMenuItem4.setVisible(false);
                        jMenuItem5.setVisible(false);
                        jMenuItem6.setVisible(false);
                        out.println(0);
                    }else if(jMenuItem5.getText().equals("")){
                        jMenuItem4.setVisible(true);
                        jMenuItem5.setVisible(false);
                        jMenuItem6.setVisible(false);
                        out.println(1);
                        out.println(jMenuItem4.getText());
                    }else if(jMenuItem6.getText().equals("")){
                        jMenuItem4.setVisible(true);
                        jMenuItem5.setVisible(true);
                        jMenuItem6.setVisible(false);
                        out.println(2);
                        out.println(jMenuItem4.getText());
                        out.println(jMenuItem5.getText());
                    }else{
                        jMenuItem4.setVisible(true);
                        jMenuItem5.setVisible(true);
                        jMenuItem6.setVisible(true);
                        out.println(3);
                        out.println(jMenuItem4.getText());
                        out.println(jMenuItem5.getText());
                        out.println(jMenuItem6.getText());
                    }
               
                    outFile.close();
                    out.close();
            }catch(Exception e){}
            
        }
        
        private void setRecentButtons(String name){
            String b1=jMenuItem4.getText();
            String b2=jMenuItem5.getText();
            String b3=jMenuItem6.getText();
            
            if(b1.equals(name)){
                return;
            }
            
            if(b2.equals(name)){
                jMenuItem5.setText(b1);
                jMenuItem4.setText(name);
                return;
            }
            
            if(b3.equals(name)){
                jMenuItem6.setText(b2);
                jMenuItem5.setText(b1);
                jMenuItem4.setText(name);
                return;
            }


            if(b1.equals("")){
                jMenuItem4.setText(name);
                return;
            }
            
            if(b2.equals("")){
                jMenuItem5.setText(b1);
                jMenuItem4.setText(name);
                return;
            }
            
            if(b3.equals("")){
                jMenuItem6.setText(b2);
                jMenuItem5.setText(b1);
                jMenuItem4.setText(name);
                return;
            }
            
            jMenuItem6.setText(b2);
            jMenuItem5.setText(b1);
            jMenuItem4.setText(name);
            

                     
        }
        
        private void openRecentProject(String name){
                File project= new File(name);
                FileInputStream fstream = null;
                try {
                    fstream = new FileInputStream(project);
                    // Get the object of DataInputStream
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String pictureName=br.readLine();
                    jLabel2.setIcon(new ImageIcon(System.getProperty("user.dir")+"\\projects\\pictures\\"+pictureName));
                    jLabel4.setText(pictureName);
                    int number= Integer.parseInt(br.readLine());
                    for(int i=0;i<number;i++){
                        int buttonNum=Integer.parseInt(br.readLine());
                        String f=br.readLine();
                        String a=br.readLine();
                        String l=br.readLine();
                        String d=br.readLine();
                        long e= Long.parseLong(br.readLine());
                        audios[buttonNum]=new audioFile(a,l,d,e,f);
                        if(buttonNum==0){
                            jTextArea1.setText("Author: "+a + "\nDate: "+(audios[0].date.get(Calendar.MONTH)+1)+"/"+audios[0].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[0].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton1.setLabel("Playback Audio");
                        }else if(buttonNum==1){
                            jTextArea2.setText("Author: "+a + "\nDate: "+(audios[1].date.get(Calendar.MONTH)+1)+"/"+audios[1].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[1].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton2.setLabel("Playback Audio");
                        }else if(buttonNum==2){
                            jTextArea3.setText("Author: "+a + "\nDate: "+(audios[2].date.get(Calendar.MONTH)+1)+"/"+audios[2].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[2].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton3.setLabel("Playback Audio");
                        }else if(buttonNum==3){
                            jTextArea4.setText("Author: "+a + "\nDate: "+(audios[3].date.get(Calendar.MONTH)+1)+"/"+audios[3].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[3].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton4.setLabel("Playback Audio");
                        }else if(buttonNum==4){
                            jTextArea5.setText("Author: "+a + "\nDate: "+(audios[4].date.get(Calendar.MONTH)+1)+"/"+audios[4].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[4].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton5.setLabel("Playback Audio");
                        }else if(buttonNum==5){
                            jTextArea6.setText("Author: "+a + "\nDate: "+(audios[5].date.get(Calendar.MONTH)+1)+"/"+audios[5].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[5].date.get(Calendar.YEAR)+"\nLocation: "+l+"\nDescription: "+ d);
                            AudioButton6.setLabel("Playback Audio");
                        }
                    }


                } catch (Exception ex) {
                    Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fstream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                resize_IconImage();
                fileOpened =name;
                DesktopApplication1.getApplication().getMainFrame().setTitle(fileOpened);
                setRecentProjects(fileOpened);
                AudioButton1.setEnabled(true);
                AudioButton2.setEnabled(true);
                AudioButton3.setEnabled(true);
                AudioButton4.setEnabled(true);
                AudioButton5.setEnabled(true);
                AudioButton6.setEnabled(true);
                jMenuItem2.setEnabled(true);
                jMenuItem1.setEnabled(false);
                jMenuItem3.setEnabled(false);
                jMenuItem4.setEnabled(false);
                jMenuItem5.setEnabled(false);
                jMenuItem6.setEnabled(false);
                jMenuItem7.setEnabled(true);
                jMenuItem20.setEnabled(true);
                jMenu2.setEnabled(true);
                jMenu3.setEnabled(true);
                jMenu4.setEnabled(true);
                jMenu5.setEnabled(true);
                jMenu6.setEnabled(true);
                jMenu7.setEnabled(true);
                projectOpen=true;
           
        }
        
        private void editRecordInfo(int num){
            if(buttons[num].getLabel().equals("Playback Audio")){
                    Calendar c=audios[num].date;
                    boolean ok_clicked=showRecordingInfoBox(num);
                if(ok_clicked){
                    audios[num].date=c;
                    buttonLabels[num].setText("Author: "+audios[num].Author + "\nDate: "+ (audios[num].date.get(Calendar.MONTH)+1) +"/"+audios[num].date.get(Calendar.DAY_OF_MONTH)+"/"+audios[num].date.get(Calendar.YEAR)+"\nLocation: "+audios[num].Location+"\nDescription: "+ audios[num].description);
                    
                }
            
            }else{
                JOptionPane.showMessageDialog(DesktopApplication1.getApplication().getMainFrame(), "Cannot edit a blank item");

            }
        }
        private void deleteRecord(int num){
           if(buttons[num].getLabel().equals("Playback Audio")){
               int option_yes= JOptionPane.showConfirmDialog(DesktopApplication1.getApplication().getMainFrame(),"Are you sure you want to delete record "+(1+num)+"?","Confirm",JOptionPane.YES_NO_OPTION);
               if(option_yes==JOptionPane.YES_OPTION){
                   String temp=fileOpened.replaceAll("\\\\", "_");
                   temp=temp.replaceAll(":", "_");
                    new File(audios[num].fileName).delete();
                    buttonLabels[num].setText("");
                    audios[num]=null;
                    buttons[num].setLabel("Record Audio");
                    saveFile();
               }
           }else{
               JOptionPane.showMessageDialog(DesktopApplication1.getApplication().getMainFrame(), "Cannot delete a blank item");
           }
        }
        
        
        
        private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
            openRecentProject(jMenuItem4.getText());
        }//GEN-LAST:event_jMenuItem4ActionPerformed

        private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
            openRecentProject(jMenuItem5.getText());
        }//GEN-LAST:event_jMenuItem5ActionPerformed

        private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
            openRecentProject(jMenuItem6.getText());
        }//GEN-LAST:event_jMenuItem6ActionPerformed

        private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
            if(projectOpen){
                int s=JOptionPane.showConfirmDialog(DesktopApplication1.getApplication().getMainFrame(), "Do you want want to save changes you made to "+fileOpened, " Picture Memory Project", JOptionPane.YES_NO_CANCEL_OPTION);
                if(s==JOptionPane.CANCEL_OPTION){
                    return;
                }else if(s==JOptionPane.YES_OPTION){
                    saveFile();
                    System.out.println("save");
                }
            }
            System.exit(0);
        }//GEN-LAST:event_exitMenuItemActionPerformed
        //delete record 1
        private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
            deleteRecord(0);
        }//GEN-LAST:event_jMenuItem9ActionPerformed
        //edit record 1
        private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
            editRecordInfo(0);
        }//GEN-LAST:event_jMenuItem8ActionPerformed
        //edit record 2
        private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
           editRecordInfo(1);
        }//GEN-LAST:event_jMenuItem10ActionPerformed
        //delete record 2
        private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
            deleteRecord(1);
        }//GEN-LAST:event_jMenuItem11ActionPerformed
        //edit record 3
        private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
            editRecordInfo(2);
        }//GEN-LAST:event_jMenuItem12ActionPerformed
        //delete record 3
        private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
            deleteRecord(2);
        }//GEN-LAST:event_jMenuItem13ActionPerformed
        //edit record 4
        private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
            editRecordInfo(3);
        }//GEN-LAST:event_jMenuItem14ActionPerformed
        //delete record 4
        private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
            deleteRecord(3);
        }//GEN-LAST:event_jMenuItem15ActionPerformed
        //edit record 5
        private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
            editRecordInfo(4);
        }//GEN-LAST:event_jMenuItem16ActionPerformed
        //delete record 5
        private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
            deleteRecord(4);
        }//GEN-LAST:event_jMenuItem17ActionPerformed
        //edit record 6
        private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
            editRecordInfo(5);
        }//GEN-LAST:event_jMenuItem18ActionPerformed
        //delete record 6
        private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
            deleteRecord(5);
        }//GEN-LAST:event_jMenuItem19ActionPerformed

        private void AudioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton3ActionPerformed
            buttonClicked(2);
}//GEN-LAST:event_AudioButton3ActionPerformed

        private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
            resize_IconImage();
        }//GEN-LAST:event_jLabel2MouseClicked

        private void AudioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton2ActionPerformed
            buttonClicked(1);
}//GEN-LAST:event_AudioButton2ActionPerformed

        private void AudioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton6ActionPerformed
            buttonClicked(5);
}//GEN-LAST:event_AudioButton6ActionPerformed

        private void AudioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton5ActionPerformed
            buttonClicked(4);
}//GEN-LAST:event_AudioButton5ActionPerformed

        private void AudioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton1ActionPerformed
            buttonClicked(0);
        }//GEN-LAST:event_AudioButton1ActionPerformed

        private void AudioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AudioButton4ActionPerformed
            buttonClicked(3);
}//GEN-LAST:event_AudioButton4ActionPerformed

        private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
           
        }//GEN-LAST:event_fileMenuActionPerformed
        
        
        //SAVE AS
        private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
            jFileChooser1.addChoosableFileFilter(new ImageFilter2());
            jFileChooser1.setAcceptAllFileFilterUsed(false);
            jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")+"\\projects\\"));
            int showOpenDialog = jFileChooser1.showSaveDialog(this.getFrame());
            if(showOpenDialog==JFileChooser.APPROVE_OPTION){
                
                
                System.out.println(jFileChooser1.getSelectedFile().getAbsolutePath());
                
                String NewFile;
                if(jFileChooser1.getSelectedFile().getAbsolutePath().endsWith(".pmp"))
                    NewFile=jFileChooser1.getSelectedFile().getAbsolutePath();
                else
                    NewFile=jFileChooser1.getSelectedFile().getAbsolutePath()+".pmp";
              
                
                fileOpened=NewFile;
                DesktopApplication1.getApplication().getMainFrame().setTitle(fileOpened);
                setRecentProjects(fileOpened);
                if(projectOpen){
                    try {
                        FileWriter outFile = new FileWriter(fileOpened);
                        PrintWriter out = new PrintWriter(outFile);
                        int actives=0;
                        out.println(jLabel4.getText());
                        for(int i=0;i<audios.length;i++){
                            if(audios[i]!=null){
                                actives++;
                            }
                        }
                        out.println(actives);
                        for(int i=0;i<audios.length;i++){
                            if(audios[i]!=null){
                                out.println(i);
                                out.println(audios[i].fileName);
                                out.println(audios[i].Author);
                                out.println(audios[i].Location);
                                out.println(audios[i].description);
                                out.println(audios[i].date.getTimeInMillis());
                            }
                        }
                        outFile.close();
                        out.close();
                    }catch(Exception e){}
                }
            }
        }//GEN-LAST:event_jMenuItem20ActionPerformed
    
        
        private void saveFile(){
            if(projectOpen){
                try {
                    FileWriter outFile = new FileWriter(fileOpened);
                    PrintWriter out = new PrintWriter(outFile);
                    int actives=0;
                    out.println(jLabel4.getText());
                    for(int i=0;i<audios.length;i++){
                        if(audios[i]!=null){
                            actives++;
                        }
                    }
                    out.println(actives);
                    for(int i=0;i<audios.length;i++){
                        if(audios[i]!=null){
                            out.println(i);
                            out.println(audios[i].fileName);
                            out.println(audios[i].Author);
                            out.println(audios[i].Location);
                            out.println(audios[i].description);
                            out.println(audios[i].date.getTimeInMillis());
                        }
                    }
                    outFile.close();
                    out.close();
                }catch(Exception e){}
            }
            
        }
        
    //close project
    public void resize_IconImage(){
        Double picDim= ((double)jLabel2.getIcon().getIconWidth())/jLabel2.getIcon().getIconHeight();
        Double labelDim = ((double)jLabel2.getWidth())/jLabel2.getHeight();
        
        ImageIcon imic= (ImageIcon) jLabel2.getIcon();
        Image temp=null;
        jLabel2.removeAll();
        if(picDim > labelDim){
           temp=imic.getImage().getScaledInstance(jLabel2.getWidth(), -1, Image.SCALE_SMOOTH);
        }else{
         temp=imic.getImage().getScaledInstance(-1,jLabel2.getHeight(),Image.SCALE_SMOOTH);
            
        }
        jLabel2.setIcon(new ImageIcon(temp));
        jLabel2.setSize(jLabel2.getIcon().getIconWidth(),jLabel2.getIcon().getIconHeight());
        jLabel2.setPreferredSize(new Dimension(jLabel2.getIcon().getIconWidth(),jLabel2.getIcon().getIconHeight()));
        System.out.println(jLabel2.getIcon().getIconWidth()+ " "+jLabel2.getIcon().getIconHeight());
        System.out.println(jLabel2.getWidth()+ " "+ jLabel2.getHeight());
        jLabel2.repaint();
        jPanel1.repaint();
        this.getFrame().pack();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button AudioButton1;
    private java.awt.Button AudioButton2;
    private java.awt.Button AudioButton3;
    private java.awt.Button AudioButton4;
    private java.awt.Button AudioButton5;
    private java.awt.Button AudioButton6;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JMenuBar menuBar;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private boolean projectOpen = false;
    public audioFile[] audios= new audioFile[6];

    private JDialog aboutBox;
    private playback_startstop playback;
    private NewOkCancelDialog RecordInfo;
    private Button[] buttons;
    private JTextArea[] buttonLabels;
    private String fileOpened;
}   
