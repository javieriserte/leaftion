package org.jiserte.leaftion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jiserte.leaftion.image.ImageLoader;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;

public class Leaftion {

  public static void main(String[] args) {

    File folder = new File("C:\\Javier\\TRiP-master\\input\\crop_plant1");
    
    try {
      
      List<BufferedImage> images = (new ImageLoader()).loadFromFolder(folder);
      
      MotionEstimator mest = new MotionEstimator();
      
      Motions motions = mest.estimateMotionInSeries(images);
    
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

}
