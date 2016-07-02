package org.jiserte.leaftion.math;

import java.awt.image.BufferedImage;
import java.util.List;

import org.jiserte.leaftion.image.ImageProcessor;

import Jama.Matrix;

public class MotionEstimator {
	
	public static final double GRADIENT_THRESHOLD = 8;
	
	public void estimateMotionInSeries(List<BufferedImage> images) {
		
		ImageProcessor imgPr = new ImageProcessor();
		
		double[][][] timeSeries = imgPr.timeSeriesToGrayScaleArray(images);
		
		double[] blurKernel = new double[]{ 1/64, 6/64, 15/64, 20/64, 15/64, 
				6/64, 1/64};
		
		int xdim = timeSeries[0].length;
		int ydim = timeSeries[0][0].length;
		
		int numberOfBlurredFrames = images.size() - blurKernel.length +1;
		
		double[][][] Vx = Matrix2dOp.zeros3D(
				xdim, 
				ydim, 
				numberOfBlurredFrames);
		
		double[][][] Vy = Matrix2dOp.zeros3D(
				xdim, 
				ydim, 
				numberOfBlurredFrames);
		
		for (int k = 0 ; k < numberOfBlurredFrames; k++) {

			DerivativeResults derivRes = SpaceTimeDerivatives.derivate(timeSeries, k, blurKernel.length);
			
			double[][] fx2 = Convolution.conv(
					Matrix2dOp.elementWiseTimes(derivRes.getFx(), 
							derivRes.getFx()), blurKernel, 1);
			fx2 = Convolution.conv( fx2, blurKernel, 0);

			double[][] fy2 = Convolution.conv(
					Matrix2dOp.elementWiseTimes(derivRes.getFy(), 
							derivRes.getFy()), blurKernel, 1);
			fy2 = Convolution.conv( fy2, blurKernel, 0);

			double[][] fxy = Convolution.conv(
					Matrix2dOp.elementWiseTimes(derivRes.getFx(), 
							derivRes.getFy()), blurKernel, 1);
			fxy = Convolution.conv( fxy, blurKernel, 0);

			
			double[][] fxt = Convolution.conv(
					Matrix2dOp.elementWiseTimes(derivRes.getFx(), 
							derivRes.getFt()), blurKernel, 1);
			fxt = Convolution.conv( fxt, blurKernel, 0);

			
			double[][] fyt = Convolution.conv(
					Matrix2dOp.elementWiseTimes(derivRes.getFy(), 
							derivRes.getFt()), blurKernel, 1);
			fyt = Convolution.conv( fyt, blurKernel, 0);

			double[][] grad = Matrix2dOp.power(
					Matrix2dOp.sum(
						Matrix2dOp.power( derivRes.getFx(), 2 ) ,
						Matrix2dOp.power( derivRes.getFy() ,2 )
							), 1/2);
			
			Matrix2dOp.border(grad, 5, 0);
			

			
			// -------------------------------------------------------------- //
			// Compute optical Flow
			// -------------------------------------------------------------- //
			
			int cx = 0;
			
			int badPixels = 0;
			
			for (int x = 0 ; x < xdim ; x++) {
				
				int cy = 0;
				
				for (int y = 0 ; y < ydim ; y++) {
					
					Matrix bigM = new Matrix(new double[][]{ 
						{fx2[x][y], fxy[x][y]} ,
						{fxy[x][y], fy2[x][y]}
					} );
					
					Matrix smallB = new Matrix ( new double[][]{ { fxt[x][y], fyt[x][y] } }); 
					
					if ( grad[x][y] < GRADIENT_THRESHOLD || 
							bigM.cond() > 100) {
						Vx[x][y][k] = 0;
						Vy[x][y][k] = 0;
						badPixels++;
					
					} else {
						
						Matrix v = bigM.inverse().arrayTimes(smallB);
						
						Vx[x][y][k] = v.get(0, 0);
						Vy[x][y][k] = v.get(0, 1);
						
					}
					
					
					cy++;
					
				}
				
				cx ++;
				
			}
			
		}
		
		
		// compute average horizontal and vertical motion

		
		
		
		
	}
	

}
