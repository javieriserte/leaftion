package org.jiserte.leaftion.math;

import java.util.Arrays;
import java.util.List;

public class CosineFitResult {

  // ------------------------------------------------------------------------ //
  // Instance variables
  public double medianPeriod;
  public double medianPhase;
  public double period;
  public double phase;
  public double amplitude;
  public double stdPeriod;
  public double stdPhase;
  public double[] objMins;
  public double[] objMaxs;
  public double[] objMeans;
  public double[] acceptedIter;
  public double[] hist;
  public double minPer;
  public double maxPer;
  // ------------------------------------------------------------------------ //
  
  // ------------------------------------------------------------------------ //
  // Constructor
  public CosineFitResult(List<CosineModel> models, int iterations) {

    // ---------------------------------------------------------------------- //
    this.calculateMeanAndStdDev(models);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    this.calculateMedians(models);
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Build fitting profile
    this.buildObjectiveFunctionProfile(models, iterations);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Build means histogram
    this.buildHistogram(models);
    // ---------------------------------------------------------------------- //
  }
  // ------------------------------------------------------------------------ //

  private void calculateMedians(List<CosineModel> models) {

    double[] pers = new double[models.size()];
    double[] phases = new double[models.size()];
    
    int modelCounter = 0;
    for (CosineModel model : models) {
      pers[modelCounter] = model.getPeriod();
      phases[modelCounter] = model.getPhase();
      
      modelCounter++;
    }

    Arrays.sort(pers);
    Arrays.sort(phases);
    
    int medianIndex = (int)((models.size()-1) / 2);
    
    this.medianPeriod = pers[medianIndex];
    this.medianPhase = phases[medianIndex];

  }

  // ------------------------------------------------------------------------ //
  // Private methods
  private void buildHistogram(List<CosineModel> models) {
    this.hist = this.getHistogram(models);
    double[] minmax = this.getMinMaxPeriods(models);
    this.minPer = minmax[0];
    this.maxPer = minmax[1];
  }

  private void buildObjectiveFunctionProfile(List<CosineModel> models,
      int iterations) {
    double[][] objProfile = new double[5][50];

    double cellSize = iterations / 50;

    for (int jj = 0; jj < 50; jj++) {

      objProfile[0][jj] = (int) ((jj + 1) * cellSize); // Iteration
      // range
      objProfile[1][jj] = Double.MAX_VALUE; // Min Objective
                                            // Value
      objProfile[2][jj] = 0; // Mean Objective Value
      objProfile[3][jj] = 0; // Max Objective Value
      objProfile[4][jj] = 0; // counter

    }

    for (CosineModel m : models) {
      for (int k = 0; k < 50; k++) {
        double oValue = m.getObjectiveSeries()[k];

        objProfile[1][k] = Math.min(objProfile[1][k], oValue);
        objProfile[2][k] += oValue;
        objProfile[3][k] = Math.max(objProfile[3][k], oValue);
        objProfile[4][k] = objProfile[4][k] + 1;

      }
    }

    for (int k = 0; k < 50; k++) {
      if (objProfile[4][k] > 0) {
        objProfile[2][k] = objProfile[2][k] / objProfile[4][k];
      } else {
        objProfile[2][k] = -1;
      }
    }

    this.objMaxs = objProfile[3];
    this.objMeans = objProfile[2];
    this.objMins = objProfile[1];
    this.acceptedIter = objProfile[0];
  }

  private void calculateMeanAndStdDev(List<CosineModel> models) {
    int replicates = models.size();

    double meanPer = 0;
    double meanPha = 0;
    double stdPer = 0;
    double stdPha = 0;
    double amplitude = 0;
    
    for (CosineModel m : models) {
      meanPer += m.getPeriod();
      meanPha += m.getPhase();
      amplitude += m.getAmplitude();
    }

    meanPer /= replicates;
    meanPha /= replicates;
    amplitude /= replicates;

    for (CosineModel m : models) {
      stdPer += Math.pow(m.getPeriod() - meanPer, 2);
      stdPha += Math.pow(m.getPhase() - meanPha, 2);
    }
    stdPer = Math.sqrt(stdPer) / replicates;
    stdPha = Math.sqrt(stdPha) / replicates;

    this.period = meanPer;
    this.phase = meanPha;
    this.amplitude = amplitude;
    this.stdPeriod = stdPer;
    this.stdPhase = stdPha;
  }
  
  protected double[] getHistogram(List<CosineModel> models) {
    double[] minMax = getMinMaxPeriods(models);

    double minPer = minMax[0];
    double maxPer = minMax[1];

    double cellSize = (maxPer - minPer) / 10;
    double[] hist = new double[10];
    for (int i = 0; i < hist.length; i++) {
      hist[i] = 0;
    }
    for (CosineModel m : models) {

      double currentPeriod = m.getPeriod();

      int index = (int) Math.min((currentPeriod - minPer) / cellSize, 9);
      hist[index]++;

    }

    return hist;

  }
  
  private double[] getMinMaxPeriods(List<CosineModel> models) {
    double[] minMax = new double[2];
    minMax[0] = Double.MAX_VALUE;
    minMax[1] = 0;

    for (CosineModel m : models) {
      double currentPeriod = m.getPeriod();
      minMax[0] = Math.min(minMax[0], currentPeriod);
      minMax[1] = Math.max(minMax[1], currentPeriod);
    }
    return minMax;
  }
  // ------------------------------------------------------------------------ //

}
