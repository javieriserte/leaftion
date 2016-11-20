package org.jiserte.leaftion.thumbimagelist;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ThumbImageList extends JList<BufferedImage> {

  // ------------------------------------------------------------------------ //
  // Class Constants
  private static final long serialVersionUID = 1L;
  // ------------------------------------------------------------------------ //
  
  // ------------------------------------------------------------------------ //
  // Instance variables
  private BufferedImage[] imagesResult;
  // ------------------------------------------------------------------------ //
  
  
  // ------------------------------------------------------------------------ //
  // Constructor
  public ThumbImageList() {
    super();
    this.setCellRenderer( new ThumbnailImageCellRenderer());
  }
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Public Interface
  public void loadImages(File[] imageFiles) {

    // ---------------------------------------------------------------------- //
    // Initialize image emptu imagelist
    int numberOfImages = imageFiles.length;
    this.imagesResult = new BufferedImage[numberOfImages];
    Arrays.fill(this.imagesResult, null);
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Set the empty image list to the Jlist
    this.setListData(this.imagesResult);
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Load the images asynchronously
    AsyncImageLoader imgLoader = new AsyncImageLoader(
        imageFiles, this.imagesResult, this);
    imgLoader.start();
    // ---------------------------------------------------------------------- //
    
  }
  // ------------------------------------------------------------------------ //


  
  // ------------------------------------------------------------------------ //
  // Auxiliary Classes
  class ThumbnailImageCellRenderer extends JPanel implements ListCellRenderer<BufferedImage> {

    // ---------------------------------------------------------------------- //
    // Class Constants
    private static final long serialVersionUID = 5520452101863053962L;
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Instance variables
    private int defaultHSize = 100;
    private int defaultVSize = 100;
    private BufferedImage defaultIcon;
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Components
    private JLabel iconLabel;
    private JLabel numberLabel;
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Constructor
    public ThumbnailImageCellRenderer() {
      super();
      
      // -------------------------------------------------------------------- //
      // Get Icon for images that aren't loaded yet
      this.defaultIcon = this.getDefaultIconImage();
      // -------------------------------------------------------------------- //
      
      // -------------------------------------------------------------------- //
      // Set layout of the thumbnail
      this.setLayout( new BorderLayout());
      // -------------------------------------------------------------------- //

      // -------------------------------------------------------------------- //
      // Generate a labels for the current image thumbnail
      this.iconLabel = new JLabel();
      this.iconLabel.setHorizontalAlignment(JLabel.CENTER);
      this.add(this.iconLabel, BorderLayout.CENTER);
      
      this.numberLabel = new JLabel();
      this.numberLabel.setHorizontalAlignment(JLabel.CENTER);
      this.add(this.numberLabel, BorderLayout.SOUTH);
      // -------------------------------------------------------------------- //
      
      // -------------------------------------------------------------------- //
      // Define a border for the thumbnail
      this.setBorder( BorderFactory.createLineBorder(Color.black, 1) );      
      // -------------------------------------------------------------------- //

    }
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Public Interface
    @Override
    public Component getListCellRendererComponent(
        JList<? extends BufferedImage> list, BufferedImage value, int index,
        boolean isSelected, boolean cellHasFocus) {
      
      int hSize = this.defaultHSize;
      int vSize = this.defaultVSize;
          
      if (value != null) {
        hSize = value.getWidth();
        vSize = value.getHeight();
        this.setPreferredSize(new Dimension(hSize+20,vSize+30) );
        this.iconLabel.setPreferredSize(new Dimension(hSize,vSize));
        this.iconLabel.setIcon( new ImageIcon(value));
      } else {
        this.setPreferredSize(new Dimension(hSize+20,vSize+30) );
        this.iconLabel.setPreferredSize(new Dimension(hSize,vSize));
        this.iconLabel.setIcon( new ImageIcon(this.defaultIcon));
      }
      
      this.numberLabel.setText(String.valueOf(index+1));
      
      if (isSelected) {
        this.setBackground(new Color( 195,215,223 ));
      } else {
        this.setBackground(new Color( 245,245,245 ));
      }
 
      return this;
    }
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    //  Private methods
    
    private BufferedImage getDefaultIconImage() {

      BufferedImage defaultIcon = new BufferedImage(
          this.defaultHSize, 
          this.defaultVSize, 
          BufferedImage.TYPE_INT_RGB);
      
      Graphics2D g = (Graphics2D) this.defaultIcon.getGraphics();
      
      g.setColor(new Color (60,60,60));
      g.fillRect(0, 0, this.defaultHSize, this.defaultVSize);
      
      g.setColor(Color.red);
      g.setStroke(new BasicStroke(4));
      int circleSize = Math.min(defaultVSize, defaultVSize)/2;
      
      g.draw(new Ellipse2D.Double(
          this.defaultHSize/4,
          this.defaultVSize/4,
          circleSize, circleSize));
      g.dispose();
      
      return defaultIcon;
      
    }
    
    // ---------------------------------------------------------------------- //
    
  }
  // ------------------------------------------------------------------------ //
  



  

}
