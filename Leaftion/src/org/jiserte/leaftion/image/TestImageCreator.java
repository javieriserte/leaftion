package org.jiserte.leaftion.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TestImageCreator extends JFrame{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {

        TestImageCreator instance = new TestImageCreator();
        
        instance.setLayout( new FlowLayout() );
        TestActionListener listener = instance.new TestActionListener(instance);
        
        JButton Test01Button = new JButton("Test 01");
        Test01Button.setActionCommand("TEST01");
        Test01Button.addActionListener(listener);
        instance.add(Test01Button);
        
        JButton Test02Button = new JButton("Test 02");
        Test02Button.setActionCommand("TEST02");
        Test02Button.addActionListener(listener);
        instance.add(Test02Button);
        
        instance.setPreferredSize(new Dimension(500, 400));
        
        instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        instance.pack();
        instance.setVisible(true);
        
      }
    });
    
  }
  
  
  public void makeTestSet01(File outfolfer) {
    double period = 36;
    
    for (int i= 0 ; i< 300; i++) {
      
      BufferedImage im = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
      
      Graphics2D g = (Graphics2D) im.getGraphics();
      
      double y = 50 * Math.cos(Math.PI * 2 * i / period);
      
      g.setColor(Color.black);
      g.fillRect(0, 0, 200, 200);
      
      g.setColor(Color.white);
      g.fillRect(75, (int) (y+100), 50, 10);
      
      File output = new File(outfolfer, String.format("im%03d", i)+".jpg");
      
      try {
        ImageIO.write(im, "jpg", output);
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      
    }

  }
  
  public class TestActionListener implements ActionListener {
    
    private TestImageCreator parent;

    public TestActionListener(TestImageCreator parent) {
    
      this.parent = parent;
      
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      
      JFileChooser jfc = new JFileChooser(".");
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setAcceptAllFileFilterUsed(false);
      jfc.showOpenDialog(this.parent);
      File outfolfer = jfc.getSelectedFile();
      
      switch (e.getActionCommand()) {
      case "TEST01":
        this.parent.makeTestSet01(outfolfer);
        break;

      case "TEST02":
        this.parent.makeTestSet02(outfolfer);
        break;
      }
      
    }
  }

  public void makeTestSet02(File outfolfer) {

    double lowerAngle = 2 * Math.PI / 360 * 45;
    double upperAngle = 2 * Math.PI / 360 * 85;
    
    double period = 36;
    
    double x0 = 30;
    
    double y0 = 55;
    
    double l = 25;
    
    for (int i = 0 ; i < 300 ; i ++ ) { 
      
      double cAngle = (lowerAngle + upperAngle)/2 + (upperAngle - lowerAngle)/2 * 
          Math.cos( 2 * Math.PI * i / period ); 
      
      double cX = l * Math.cos(cAngle);
      double cY = l * Math.sin(cAngle);
      
      System.out.println("cAngle: " + cAngle);
      
      BufferedImage im  = new BufferedImage(60, 60,
          BufferedImage.TYPE_INT_RGB);
      
      Graphics2D g2d = (Graphics2D) im.getGraphics();
      
      g2d.setColor( Color.black ); 
      g2d.fillRect(0, 0, 60, 60);
      
      g2d.setColor(Color.white);
      
      g2d.setStroke(new BasicStroke(5));
      
      int leafWidth=8;
      
      g2d.drawLine((int)x0,(int) y0,(int) (x0 + cX),(int) (y0 - cY));
      g2d.drawLine((int)x0,(int) y0,(int) (x0 -cX),(int) (y0 - cY));
      
      g2d.fillOval((int)( x0 + cX-leafWidth/2), (int)(y0-cY-leafWidth/2), leafWidth, leafWidth);
      g2d.fillOval((int) (x0 - cX - leafWidth/2), (int)(y0-cY-leafWidth/2), leafWidth, leafWidth);
      
      g2d.dispose();
      
      File outfile = new File(outfolfer, String.format("im%03d.jpg", i));
      
      try {
        ImageIO.write(im, "jpg", outfile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    
    }
    
    
  }
  
}
