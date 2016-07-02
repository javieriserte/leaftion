package org.jiserte.leaftion.math;

public class DerivativeResults {

	double[][] fx;
	double[][] fy;
	double[][] ft;
	
	
	
	public DerivativeResults(double[][] fx, double[][] fy, double[][] ft) {
		super();
		this.fx = fx;
		this.fy = fy;
		this.ft = ft;
	}
	
	
	
	public DerivativeResults() {
		super();
	}

	public double[][] getFx() {
		return fx;
	}
	public void setFx(double[][] fx) {
		this.fx = fx;
	}
	public double[][] getFy() {
		return fy;
	}
	public void setFy(double[][] fy) {
		this.fy = fy;
	}
	public double[][] getFt() {
		return ft;
	}
	public void setFt(double[][] ft) {
		this.ft = ft;
	}
	
	
}
