package org.jiserte.leaftion.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageLibrary {

	private File[] imageFiles;

	private BufferedImage currentImage;

	private int currentImageIndex = 0;

	private int lastRetrievedImageIndex = -1;

	private BufferedImage brightCompositeImage;

	public BufferedImage getBrightCompositeImage() {
    return brightCompositeImage;
  }

  public void getImageFilesInFolder(File folder) {

		this.setImageFiles(folder.listFiles(new JpegFileNameFilter()));

	}

	public BufferedImage next() {

		this.currentImageIndex++;

		return this.getCurrentImage();

	}

	public File[] getImageFiles() {
		return imageFiles;
	}

	private void setImageFiles(File[] imageFiles) {
		this.imageFiles = imageFiles;
	}

	public void exportBright(File outfile) {
		try {
			ImageIO.write(this.brightCompositeImage, "jpeg", outfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BufferedImage getCurrentImage() {

		if (this.currentImageIndex < 0) {
			this.currentImageIndex = 0;
		}

		if (this.currentImageIndex >= this.getImageFiles().length) {
			this.currentImageIndex = this.getImageFiles().length - 1;
		}

		if (this.lastRetrievedImageIndex != this.currentImageIndex) {
			this.setCurrentImage();
		}

		return currentImage;

	}

	public void setCurrentImage() {

		try {
			this.currentImage = ImageIO.read(this.getImageFiles()[this.currentImageIndex]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lastRetrievedImageIndex = this.currentImageIndex;

	}

	public void makeBrightComposite() {

		if (this.currentImage == null) {
			this.setCurrentImage();
		}

		int width = this.currentImage.getWidth();
		int height = this.currentImage.getHeight();
		double[][] colapse = new double[width][height];

		ImageProcessor ip = new ImageProcessor();

		int imageCounter = 0;

		for (File file : this.getImageFiles()) {

			try {

				imageCounter++;

				System.out.println("Current Image Number : " + imageCounter);

				BufferedImage cImage = ImageIO.read(file);

				double[][] cImageInt = ip.toGrayScaleArray(cImage);

				for (int x = 0; x < width; x++) {

					for (int y = 0; y < height; y++) {

						colapse[x][y] = Math.max(colapse[x][y], cImageInt[x][y]);

					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		BufferedImage bright = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++) {

			for (int y = 0; y < height; y++) {

				float br = (float) colapse[x][y] / 255;

				bright.setRGB(x, y, new Color(br, br, br).getRGB());

			}

		}

		this.brightCompositeImage = bright;

	}

	public List<List<BufferedImage>> cropRegions(List<Rectangle> regions) {

		List<List<BufferedImage>> result = new ArrayList<>();
		
		for (int i =0 ; i< regions.size(); i++) {
			result.add(new ArrayList<>());
		}
		
		List<String> absolutePathNames = new ArrayList<>();
		
		for (File file : this.imageFiles) {
			
			absolutePathNames.add(file.getAbsolutePath());
			
		}		
		
		Collections.sort(absolutePathNames);
		
		for (String fileString : absolutePathNames) {
			
			File file = new File(fileString);
			
			System.out.println(file.getName());
			
			int index = 0;

			try {

				BufferedImage ci = ImageIO.read(file);

				for (Rectangle region : regions) {
					
					BufferedImage im = new BufferedImage(
							(int)region.getWidth(), 
							(int)region.getHeight(), 
							BufferedImage.TYPE_INT_RGB) ;
					
					Graphics2D g = (Graphics2D) im.getGraphics();
					
					g.drawImage(ci,
							0, 0,
							(int)region.getWidth(),(int)region.getHeight(),
							(int)region.getX(), (int)region.getY(), 
							(int)(region.getX() + region.getWidth()) ,
							(int)(region.getY() + region.getHeight()) , null);
					
					result.get(index).add(im);
					
					index++;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return result;

	}

	public static void main(String[] args) {

		ImageLibrary il = new ImageLibrary();

		il.getImageFilesInFolder(new File("c:\\javier\\TRiP-master\\input"));

		il.makeBrightComposite();

		il.exportBright(new File("c:\\javier\\TRiP-master\\bright.jpg"));

	}

}
