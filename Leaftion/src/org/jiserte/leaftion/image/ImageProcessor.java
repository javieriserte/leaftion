package org.jiserte.leaftion.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageProcessor {
  
  private double scaleFactor;

	public double getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(double scaleFactor) {
    this.scaleFactor = scaleFactor;
  }

  public ImageProcessor() {
		
		super();
		
	}

	/**
	 * Get a list of images and converts them of a 3D matrix (x,y,time) of
	 * gray scale intensity values.
	 * @param images
	 * @return
	 */
	public double[][][] timeSeriesToGrayScaleArray( List<BufferedImage> images) {

		
		int width = images.get(0).getWidth();
		System.out.println("widht:" + width);
		int height = images.get(0).getHeight();
    System.out.println("height" + height);
		
    double scaleFactor = 60 / (double)( Math.max(width, height) );
		
		this.setScaleFactor(scaleFactor);
		
		int newWidth  = (int) (width * scaleFactor);
		int newHeight = (int) (height * scaleFactor);
		
		
		double[][][] result = new double[images.size()][width][height];

		for (int frame=0; frame < images.size(); frame++) {
			
			BufferedImage currentImage = images.get(frame);
			
			BufferedImage resized = new BufferedImage(newWidth, newHeight, 
					currentImage.getType());
			
			Graphics2D g = resized.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			g.drawImage(currentImage, 0, 0, newWidth, newHeight, 0, 0, width,
					height, null);
			
			g.dispose();
      result[frame] = this.toGrayScaleArray(resized);
      
      System.out.println(
      
      new Color(currentImage.getRGB(0, 0)) + "\n" +

      new Color(currentImage.getRGB(0, 1))  + "\n" +

      new Color(currentImage.getRGB(0, 2)) + "\n" +

      new Color(currentImage.getRGB(0, 3)) + "\n"

      );
//      result[frame] = this.toGrayScaleArray(currentImage);
		
		}
		
		return result;
		
		
	}
	
	public double[][] toGrayScaleArray(BufferedImage image) {

		double[][] result = new double[image.getWidth()][image.getHeight()]; 
		
		
		for (int x = 0; x < image.getWidth(); x++) {
			
			for (int y = 0 ; y < image.getHeight(); y++) {
				
				Color c = new Color(image.getRGB(x, y));
				
				double intensity =  c.getRed() * 0.2989 ;
				
				intensity += c.getGreen() * 0.5870 ;
				
				intensity += c.getBlue() * 0.114 ;
				
				result[x][y] = Math.round(Math.min(intensity,255));
				
				
			}
			
		}
		
		return result;
		
	}
	
	
	
}
