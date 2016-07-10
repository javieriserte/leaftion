package org.jiserte.leaftion.math;

import java.util.Arrays;

public class CosineModel {
	
	private double[] vars;
	private double[] maxChanges;
	
	public CosineModel(double amplitude, double phase, double period) {
		super();
		this.vars = new double[] {amplitude, phase,period};
		this.maxChanges = new double[] {0.05, (1.0/24), 1.0};
	}
	
	public CosineModel() {
		super();
		this.vars = new double[] {1, 0, 24};
		this.maxChanges = new double[] {0.1, (1.0/12), 2.0};
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

}
