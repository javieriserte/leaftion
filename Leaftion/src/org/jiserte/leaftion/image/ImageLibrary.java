package org.jiserte.leaftion.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import org.jiserte.leaftion.events.ProcessingFramesEvent;
import org.jiserte.leaftion.events.ProcessingFramesListener;
import org.jiserte.leaftion.image.filenamefilter.JpegFileNameFilter;

public class ImageLibrary {

  private File[] imageFiles;
  private List<ProcessingFramesListener> brightImageListeners = new ArrayList<>();
  private List<ProcessingFramesListener> cropImageListeners = new ArrayList<>();
  private BufferedImage currentImage;
  private int currentImageIndex = 0;

  private int lastRetrievedImageIndex = -1;

  private BufferedImage brightCompositeImage;

  public BufferedImage getBrightCompositeImage() {
    return brightCompositeImage;
  }

//  @Deprecated
//  public void getImageFilesInFolder(File folder) {
//
//    File[] listFiles = folder.listFiles(new JpegFileNameFilter());
//    
//    Arrays.sort(listFiles, new Comparator<File>() {
//		@Override
//		public int compare(File o1, File o2) {
//			return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
//		}
//	});
//    
//	this.setImageFiles(listFiles);
//
//  }

  public BufferedImage next() {
    this.currentImageIndex++;
    return this.getCurrentImage();
  }

  public BufferedImage previous() {
    this.currentImageIndex--;
    return this.getCurrentImage();
  }


  public BufferedImage imageAt(int index) {
    this.currentImageIndex = index;
    return this.getCurrentImage();
  }
  
  public File[] getImageFiles() {
    return imageFiles;
  }

  public void setImageFiles( File[] imageFiles ) {
    this.imageFiles = imageFiles;
  }

  public void exportBright(File outfile) {
    try {
      ImageIO.write(this.brightCompositeImage, "jpeg", outfile);
    } catch (IOException e) {
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
      this.currentImage = ImageIO
          .read(this.getImageFiles()[this.currentImageIndex]);
    } catch (IOException e) {
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

    for (ProcessingFramesListener l : this.brightImageListeners) {
      ProcessingFramesEvent e = new ProcessingFramesEvent();
      e.currentFrame = 0;
      e.numberOfFrames = this.getImageFiles().length;
      l.startProccess(e);
    }

    for (File file : this.getImageFiles()) {

      try {

        imageCounter++;

        BufferedImage cImage = ImageIO.read(file);

        double[][] cImageInt = ip.toGrayScaleArray(cImage);

        for (int x = 0; x < width; x++) {

          for (int y = 0; y < height; y++) {

            colapse[x][y] = Math.max(colapse[x][y], cImageInt[x][y]);

          }

        }

        for (ProcessingFramesListener l : this.brightImageListeners) {
          ProcessingFramesEvent e = new ProcessingFramesEvent();
          e.currentFrame = imageCounter;
          e.numberOfFrames = this.getImageFiles().length;
          l.updateFrame(e);
        }

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    for (ProcessingFramesListener l : this.brightImageListeners) {
      ProcessingFramesEvent e = new ProcessingFramesEvent();
      e.currentFrame = this.getImageFiles().length;
      e.numberOfFrames = this.getImageFiles().length;
      l.finnishProccess(e);
    }

    BufferedImage bright = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);

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

    if (regions.size() == 0) {
      return result;
    }
    
    for (int i = 0; i < regions.size(); i++) {
      result.add(new ArrayList<>());
    }

    List<String> absolutePathNames = new ArrayList<>();

    for (File file : this.imageFiles) {

      absolutePathNames.add(file.getAbsolutePath());

    }

    Collections.sort(absolutePathNames);

    for (ProcessingFramesListener l : this.cropImageListeners) {
      ProcessingFramesEvent e = new ProcessingFramesEvent();
      e.currentFrame=0;
      e.proccessType=ProcessingFramesEvent.PROCCESS_CROP_IMAGES;
      e.numberOfFrames=absolutePathNames.size();
      l.startProccess(e);
    }
    
    int imageIndex = 0;
    for (String fileString : absolutePathNames) {

      File file = new File(fileString);

      int index = 0;

      try {

        BufferedImage ci = ImageIO.read(file);
        
        for (Rectangle region : regions) {

          BufferedImage im = new BufferedImage((int) region.getWidth(),
              (int) region.getHeight(), BufferedImage.TYPE_INT_RGB);

          Graphics2D g = (Graphics2D) im.getGraphics();

          g.drawImage(ci, 0, 0, (int) region.getWidth(),
              (int) region.getHeight(), (int) region.getX(),
              (int) region.getY(), (int) (region.getX() + region.getWidth()),
              (int) (region.getY() + region.getHeight()), null);

          result.get(index).add(im);

          index++;
        }
        imageIndex++;
        for (ProcessingFramesListener l : this.cropImageListeners) {
          ProcessingFramesEvent e = new ProcessingFramesEvent();
          e.currentFrame=imageIndex;
          e.proccessType=ProcessingFramesEvent.PROCCESS_CROP_IMAGES;
          e.numberOfFrames=absolutePathNames.size();
          l.updateFrame(e);
        }
        
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    
    for (ProcessingFramesListener l : this.cropImageListeners) {
      ProcessingFramesEvent e = new ProcessingFramesEvent();
      e.currentFrame=0;
      e.proccessType=ProcessingFramesEvent.PROCCESS_CROP_IMAGES;
      e.numberOfFrames=absolutePathNames.size();
      l.finnishProccess(e);
    }


    return result;

  }
  
  public int getImageIndex() {
    return this.currentImageIndex +1 ;
  }

  public void addBrightImageProccesingListener(ProcessingFramesListener l) {
    this.brightImageListeners.add(l);
  }
  
  public void addCropImageProccessingListener(ProcessingFramesListener l) {
    this.cropImageListeners.add(l);
  }
  

}
