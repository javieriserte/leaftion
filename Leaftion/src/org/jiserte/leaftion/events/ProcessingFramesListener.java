package org.jiserte.leaftion.events;

public interface ProcessingFramesListener {

  public void startProccess(ProcessingFramesEvent e);

  public void updateFrame(ProcessingFramesEvent e);
  
  public void finnishProccess(ProcessingFramesEvent e);
  
  
  
}
