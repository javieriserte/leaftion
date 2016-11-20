package org.jiserte.leaftion.thumbimagelist;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class AsyncImageLoader extends Thread {

  // ------------------------------------------------------------------------ //
  // Instance variables
  private File[] imageFiles;
  private BufferedImage[] imagesResult;
  private JComponent parent;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Constructor
  public AsyncImageLoader(File[] imageFiles, BufferedImage[] imagesResult,
      JComponent parent) {
    super();
    this.imageFiles = imageFiles;
    this.imagesResult = imagesResult;
    this.parent = parent;
  }
  // ------------------------------------------------------------------------ //


  
  // ------------------------------------------------------------------------ //
  // Public interface
  @Override
  public void run() {
    super.run();
    
    if (this.imagesResult.length == imageFiles.length) {
      
      for (int i = 0; i < this.imageFiles.length; i++) {
        
        BufferedImage img = null;
        try {
          img = ImageIO.read(this.imageFiles[i]);
          
          int maxWidthOrHeight = Math.max( img.getWidth(), img.getHeight() ); 
          
          int newSizeH = 100 * img.getWidth()  / maxWidthOrHeight;
          int newSizeV = 100 * img.getHeight() / maxWidthOrHeight;
          
          BufferedImage newImage = new BufferedImage( newSizeH, newSizeV, BufferedImage.TYPE_INT_RGB);
          
          Graphics g = newImage.createGraphics();
          g.drawImage(img, 0, 0, newSizeH, newSizeV, null);
          g.dispose();
          
          this.imagesResult[i] = newImage;
          
          if ( this.parent != null) {
            SwingUtilities.invokeLater(new Runnable() {
              
              @Override public void run() { parent.updateUI(); }
            
            });
          }

        } catch (Exception e) {
          
        }
      }
    }
  }
  // ------------------------------------------------------------------------ //



  
}
