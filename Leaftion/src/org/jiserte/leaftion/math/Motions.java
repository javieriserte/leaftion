package org.jiserte.leaftion.math;

import java.util.Arrays;

public class Motions {

  private double[] h_motion;
  
  private double[] v_motion;

  public double[] getH_motion() {
    return h_motion;
  }

  public void setH_motion(double[] h_motion) {
    this.h_motion = h_motion;
  }

  public double[] getV_motion() {
    return v_motion;
  }

  public void setV_motion(double[] v_motion) {
    this.v_motion = v_motion;
  }

  public Motions(double[] h_motion, double[] v_motion) {
    super();
    this.h_motion = h_motion;
    this.v_motion = v_motion;
  }

  public Motions() {
    super();
  }

  @Override
  public String toString() {
    return "Motions [h_motion=" + Arrays.toString(h_motion) + ",s v_motion="
        + Arrays.toString(v_motion) + "]";
  }
  
 
  
}
