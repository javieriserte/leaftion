package org.jiserte.leaftion.events;

public class ProcessingFramesEvent {

  // ------------------------------------------------------------------------ //
  // Class Constants
  public static final int PROCCESS_BRIGTH_IMAGE = 1;
  public static final int PROCCESS_MOTION_ESTIMATE =2;
  public static final int PROCCESS_CROP_IMAGES =3;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Instance variables
  public int numberOfFrames;
  public int currentFrame;
  public int proccessType; 
  // ------------------------------------------------------------------------ //
  
}
