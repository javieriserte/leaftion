package org.jiserte.leaftion.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageProcessor {

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
		int height = images.get(0).getHeight();
		
		double scaleFactor = 60 / ( Math.max(width, height) ); 
		
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
				
				result[x][y] = Math.min(intensity,255);
				
				
			}
			
		}
		
		return result;
		
	}
	
	
	
}
