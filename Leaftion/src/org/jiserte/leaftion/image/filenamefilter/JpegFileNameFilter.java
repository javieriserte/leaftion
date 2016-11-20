package org.jiserte.leaftion.image;

import java.io.File;
import java.io.FilenameFilter;

public class JpegFileNameFilter implements FilenameFilter {

  @Override
  public boolean accept(File dir, String filename) {
    
    return filename.matches("^.*[jJ][pP][eE]*[gG]$") ;
    
  }

}
