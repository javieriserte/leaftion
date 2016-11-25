package org.jiserte.leaftion.image.filenamefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JpegFileNameFilter extends FileFilter {

  @Override
  public boolean accept( File file ) {
    
    return file.getName().matches("^.*[jJ][pP][eE]*[gG]$") ;
    
  }

  @Override
  public String getDescription() {
    return "JPG - Joint Photographic Experts Group(*.jpeg/ *.jpg)";
  }

}
