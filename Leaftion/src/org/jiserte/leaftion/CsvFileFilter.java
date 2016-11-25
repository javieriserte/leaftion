package org.jiserte.leaftion;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CsvFileFilter extends FileFilter {

  @Override
  public boolean accept(File arg0) {
    return arg0.getName().matches("^.+[.][cC][sS][vV]$");
  }

  @Override
  public String getDescription() {
    return "Comma separated values (*.csv)";
  }

}
