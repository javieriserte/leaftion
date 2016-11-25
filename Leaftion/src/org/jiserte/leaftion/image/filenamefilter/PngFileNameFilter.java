package org.jiserte.leaftion.image.filenamefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PngFileNameFilter extends FileFilter {

  @Override
  public boolean accept(File arg0) {
    return arg0.getName().matches("^.*[Pn][nN][gG]$") ;
  }

  @Override
  public String getDescription() {
    return "PNG - Portable Network Graphics (.png)";
  }
  
}
