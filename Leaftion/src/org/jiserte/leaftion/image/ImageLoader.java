package org.jiserte.leaftion.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageLoader {

  public List<BufferedImage> loadFromFolder(File folder) throws IOException {

    File[] img_files = folder.listFiles(new JpegFileNameFilter());
    
    Arrays.sort(img_files);
    
    List<BufferedImage> result = new ArrayList<>();
    
    for (File current : img_files) {
      
      result.add(  ImageIO.read(current)  );
      
    }
    
    return result;
    
  }
  
}
