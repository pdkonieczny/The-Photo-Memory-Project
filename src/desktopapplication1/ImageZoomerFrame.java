package desktopapplication1;



/* 
* This example is from javareference.com 
* for more information visit, 
* http://www.javareference.com 
*/ 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class creates a Image Zoomer
 * @author Rahul Sapkal(rahul@javareference.com)
 */
public class ImageZoomerFrame extends JFrame 
       implements MouseListener, MouseMotionListener, ActionListener
{
    private ImagePanel m_imagePanel;
    private JScrollPane m_srollPane;
    private    JPanel m_imageContainer;
    private JLabel m_zoomedInfo;
    private JButton m_zoomInButton;
    private JButton m_zoomOutButton;
    private JButton m_originalButton;
    private Cursor m_zoomCursor;
    private boolean _canDrag  = false;
    private Point start;
    private String file;
    private int number;
    /**
     * Constructor
     * @param image
     * @param zoomPercentage
     * @param imageName
     */    
    public ImageZoomerFrame(Image image, double zoomPercentage, String imageName,String fileName, int num)
    {
        super("Image Zoomer [" + imageName + "]");
        file=fileName;
        number=num;
        start=new Point();
        if(image == null)
        {
            add(new JLabel("Image " + imageName + " not Found"));
        }
        else
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            DisplayMode dm = gs[0].getDisplayMode();
            int screenWidth = dm.getWidth()*9/10;
            int screenHeight = dm.getHeight()*9/10;
            
            Double picDim= ((double)image.getWidth(null))/image.getHeight(null);
            Double screenDim = ((double)screenWidth)/screenHeight;
            
            if(picDim > screenDim){
               image=image.getScaledInstance(screenWidth, -1, Image.SCALE_SMOOTH);
              
            }else{
                image=image.getScaledInstance(-1,screenHeight,Image.SCALE_SMOOTH);
                
            }
            
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(screen);
            this.setPreferredSize(screen);
            this.setMinimumSize(screen);
            this.setResizable(false); 
            
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            m_zoomInButton = new JButton("Zoom In");
            m_zoomInButton.addActionListener(this);
            
            m_zoomOutButton = new JButton("Zoom Out");
            m_zoomOutButton.addActionListener(this);
            
            m_originalButton = new JButton("Original");
            m_originalButton.addActionListener(this);
            
            m_zoomedInfo = new JLabel("Zoomed to 100%");
            
            topPanel.add(new JLabel("Zoom Percentage is " + 
                                    (int)zoomPercentage + "%"));
            topPanel.add(m_zoomInButton);
            topPanel.add(m_originalButton);
            topPanel.add(m_zoomOutButton);
            topPanel.add(m_zoomedInfo);
                                    
            m_imagePanel = new ImagePanel(image, zoomPercentage);
            m_imagePanel.addMouseListener(this);
            m_imagePanel.addMouseMotionListener(this);
            m_imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            m_imageContainer.setBackground(Color.WHITE);
            m_imageContainer.add(m_imagePanel);
            
            m_srollPane = new JScrollPane(m_imageContainer);
            m_srollPane.setAutoscrolls(true);
            start_stop ss = new start_stop(number,file,this);
            getContentPane().add(BorderLayout.NORTH, ss); 
            getContentPane().add(BorderLayout.SOUTH, topPanel);
            getContentPane().add(BorderLayout.CENTER, m_srollPane);
            
            
            m_imagePanel.repaint();
        }
        
        pack();
        setVisible(true);
    }
    
    /**
     * Action Listener method taking care of 
     * actions on the buttons
     */
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource().equals(m_zoomInButton))
        {
            m_imagePanel.zoomIn();
            adjustLayout();
        }
        else if(ae.getSource().equals(m_zoomOutButton))
        {
            m_imagePanel.zoomOut();
            adjustLayout();
        }
        else if(ae.getSource().equals(m_originalButton))
        {
            m_imagePanel.originalSize();
            adjustLayout();
        }
    }
    
    /**
     * This method takes the Zoom Cursor Image
     * and creates the Zoom Custom Cursor which is 
     * shown on the Image Panel on mouse over
     * 
     * @param zoomcursorImage
     */
    public void setZoomCursorImage(Image zoomcursorImage)
    {
        m_zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                        zoomcursorImage, new Point(0, 0), "ZoomCursor");
        
    }
    
    /**
     * This method adjusts the layout after 
     * zooming
     *
     */
    private void adjustLayout()
    {
        m_imageContainer.doLayout();        
        m_srollPane.doLayout();
       
        m_zoomedInfo.setText("Zoomed to " + (int)m_imagePanel.getZoomedTo() + "%");
    }
    
    /**
     * This method handles mouse clicks
     */
    
        
    public void mouseEntered(MouseEvent e)
    {
        m_imageContainer.setCursor(m_zoomCursor);             
    }
           
    public void mouseExited(MouseEvent e)
    {
        m_imageContainer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));         
    }
           
    public void mousePressed(MouseEvent e)
    {
        start=e.getPoint();
        _canDrag=true;
    }
           
    public void mouseReleased(MouseEvent e)
    {
     
    }
    
    public void mouseDragged(MouseEvent e){
       Point cp = e.getPoint();
       JViewport vport=m_srollPane.getViewport();
        Point vp = vport.getViewPosition();
        //= SwingUtilities.convertPoint(vport,0,0,label);
        
       
       int max_x=m_srollPane.getWidth()-vport.getWidth();
       int max_y=m_srollPane.getHeight()-vport.getHeight();
       
       JScrollBar vert_bar=m_srollPane.getVerticalScrollBar();
       JScrollBar horiz_bar=m_srollPane.getHorizontalScrollBar();
       System.out.println(horiz_bar.getValue()+" " +horiz_bar.getVisibleAmount()+ " "+horiz_bar.getMaximum()+ " v "+ vert_bar.getValue()+" " +vert_bar.getVisibleAmount()+ " "+vert_bar.getMaximum()+ " end: "+cp.x+ " "+cp.y+" start: "+ start.x + " "+start.y);

       boolean bot_pic=(vert_bar.getValue()+vert_bar.getVisibleAmount()==vert_bar.getMaximum() && start.y>=cp.y);
       boolean top_pic= (vert_bar.getValue()==0 && start.y<=cp.y);
       boolean left_pic= (horiz_bar.getValue()==0 && start.x<=cp.x);
       boolean right_pic= (horiz_bar.getValue()+horiz_bar.getVisibleAmount()==horiz_bar.getMaximum() && start.x>=cp.x);
       if(left_pic || right_pic){
           if(bot_pic || top_pic){
               vp.translate(0, 0);
               System.out.println("do nothing");
           }else{
               vp.translate(0, start.y-cp.y);
               System.out.println("x");
           }
       }
       else if(bot_pic || top_pic){
           if(left_pic || right_pic){
               vp.translate(0, 0);
               System.out.println("do nothing");
           }else{
               vp.translate(start.x-cp.x, 0);
                System.out.println("y");
           }
       }else{
           vp.translate(start.x-cp.x, start.y-cp.y);
            System.out.println("both");
       }
           
            m_srollPane.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
       
            vport.setViewPosition(vp);
            vport.repaint();
            m_srollPane.repaint();
            start.setLocation(cp);
       

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    

    /**
     * This class is the Image Panel where the image
     * is drawn and scaled.
     * 
     * @author Rahul Sapkal(rahul@javareference.com)
     */
    public class ImagePanel extends JPanel
    {
        private double m_zoom = 1.0;
        private double m_zoomPercentage;
        private Image m_image;
                
        /**
         * Constructor
         * 
         * @param image
         * @param zoomPercentage
         */                
        public ImagePanel(Image image, double zoomPercentage)
        {
            m_image = image;
            m_zoomPercentage = zoomPercentage / 100;
        }
        
        /**
         * This method is overriden to draw the image
         * and scale the graphics accordingly
         */
        public void paintComponent(Graphics grp) 
        { 
            Graphics2D g2D = (Graphics2D)grp;
            
            //set the background color to white
            g2D.setColor(Color.WHITE);
            //fill the rect
            g2D.fillRect(0, 0, getWidth(), getHeight());
            
            //scale the graphics to get the zoom effect
            g2D.scale(m_zoom, m_zoom);
            
            //draw the image
            g2D.drawImage(m_image, 0, 0, this);
            
        }
         
        /**
         * This method is overriden to return the preferred size
         * which will be the width and height of the image plus
         * the zoomed width width and height. 
         * while zooming out the zoomed width and height is negative
         */
        public Dimension getPreferredSize()
        {
            return new Dimension((int)(m_image.getWidth(this) + 
                                      (m_image.getWidth(this) * (m_zoom - 1))),
                                 (int)(m_image.getHeight(this) + 
                                      (m_image.getHeight(this) * (m_zoom -1 ))));
        }
        
        /**
         * Sets the new zoomed percentage
         * @param zoomPercentage
         */
        public void setZoomPercentage(int zoomPercentage)
        {
            m_zoomPercentage = ((double)zoomPercentage) / 100;    
        }
        
        /**
         * This method set the image to the original size
         * by setting the zoom factor to 1. i.e. 100%
         */
        public void originalSize()
        {
            m_zoom = 1; 
        }
        
        /**
         * This method increments the zoom factor with
         * the zoom percentage, to create the zoom in effect 
         */
        public void zoomIn()
        {
            m_zoom += m_zoomPercentage;
        }            
        
        /**
         * This method decrements the zoom factor with the 
         * zoom percentage, to create the zoom out effect 
         */
        public void zoomOut()
        {
            m_zoom -= m_zoomPercentage;
            
            if(m_zoom < m_zoomPercentage)
            {
                if(m_zoomPercentage > 1.0)
                {
                    m_zoom = 1.0;
                }
                else
                {
                    zoomIn();
                }
            }
        }
        
        /**
         * This method returns the currently
         * zoomed percentage
         * 
         * @return
         */
        public double getZoomedTo()
        {
            return m_zoom * 100; 
        }
    }
} 