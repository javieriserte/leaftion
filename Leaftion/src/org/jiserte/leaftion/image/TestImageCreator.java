package org.jiserte.leaftion.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestImageCreator {

  public static void main(String[] args) {
    
    double period = 36;
    
    
    
    for (int i= 0 ; i< 300; i++) {
      
      BufferedImage im = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
      
      Graphics2D g = (Graphics2D) im.getGraphics();
      
      double y = 50 * Math.cos(Math.PI * 2 * i / period);
      
      g.setColor(Color.black);
      g.fillRect(0, 0, 200, 200);
      
      g.setColor(Color.white);
      g.fillRect(75, (int) (y+100), 50, 10);
      
      File output = new File("/home/ulab103/javier/leaftion/test/im" + String.format("%03d", i)+".jpg");
      
      try {
        ImageIO.write(im, "jpg", output);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      
    }
    
  }
  
}
