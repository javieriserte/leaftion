package org.jiserte.leaftion;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import org.jiserte.leaftion.image.ImageLoader;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;

public class Leaftion {

  public static void main(String[] args) {

    File folder = new File("C:\\Javier\\TRiP-master\\input\\crop_plant1");
    
    File outfile = new File("C:\\Javier\\TRiP-master\\input\\my_plat.csv");
    
    try {
      
      List<BufferedImage> images = (new ImageLoader()).loadFromFolder(folder);
      
      MotionEstimator mest = new MotionEstimator();
      
      Motions motions = mest.estimateMotionInSeries(images);
      
      PrintStream out = new PrintStream(outfile);
      
      for ( int i =0 ; i< motions.getV_motion().length ; i++ ) {
        
        out.println( String.format(new Locale("es", "ar"), "%6.4f;%6.4f ",motions.getV_motion()[i] ,motions.getH_motion()[i])) ;
        
      }
      
      out.close();
      
      
    
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

}
