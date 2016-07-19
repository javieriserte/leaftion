package org.jiserte.leaftion.math;

import java.util.Arrays;

public class CosineModel {
	
	private double[] vars;
	private double[] maxChanges;
  private double[] objectiveSeries;
	private int[] acceptedIterations;
	
	public double[] getObjectiveSeries() {
    return objectiveSeries;
  }

  public void setObjectiveSeries(double[] objectiveSeries) {
    this.objectiveSeries = objectiveSeries;
  }

  public int[] getAcceptedIterations() {
    return acceptedIterations;
  }

  public void setAcceptedIterations(int[] acceptedIterations) {
    this.acceptedIterations = acceptedIterations;
  }

  public CosineModel(double amplitude, double phase, double period) {
		super();
		this.vars = new double[] {amplitude, phase,period};
		this.maxChanges = new double[] {0.2, (0.5), 8};
	}
	
	public CosineModel() {
		super();
		this.vars = new double[] {1, 0, 24};
		this.maxChanges = new double[] {0.2, (0.5), 8};
	}

	public double diff(double[] x, double[] y) {
		double diff = 0;
		for (int i = 0; i< x.length; i++) {
			double val = vars[0] * Math.cos(  
					( x[i] + this.vars[1]) * Math.PI * 2 / this.vars[2]  ); 
					
			diff += Math.pow(val - y[i],2);
		}
		return diff;
	}
	
	public CosineModel mutate() {
		int index = (int) (Math.random() * 3);
		
		double[] newVars = Arrays.copyOfRange(this.vars, 0, 3);
		newVars[index] += (Math.random() * 2 -1) * this.maxChanges[index];
		
		CosineModel newModel = new CosineModel();
		newModel.vars = newVars;
		
		return newModel;
		
		
	}
	
	public String toString() {
		return String.format("[ Amp: %7.4f Phase: %7.4f Period: %7.4f]", this.vars[0],this.vars[1], this.vars[2]);
	}
	
	public double getPeriod() {
		return this.vars[2];
	}

	public double getPhase() {
		return this.vars[1];
	}

}
