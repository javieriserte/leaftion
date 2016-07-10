package org.jiserte.leaftion.math;

import java.awt.image.BufferedImage;
import java.util.List;

import org.jiserte.leaftion.image.ImageProcessor;

import Jama.Matrix;

public class MotionEstimator {
	
	public static final double GRADIENT_THRESHOLD = 8;
	
	public Motions estimateMotionInSeries(List<BufferedImage> images) {
		
		ImageProcessor imgPr = new ImageProcessor();
		
		double[][][] timeSeries = imgPr.timeSeriesToGrayScaleArray(images);

		double scaleFactor = imgPr.getScaleFactor();
		
		double[] blurKernel = new double[]{ 1f/64, 6f/64, 15f/64, 20f/64, 15f/64, 
				6f/64, 1f/64};
		
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

			double[][] elementWiseTimes = Matrix2dOp.elementWiseTimes(derivRes.getFx(), 
      		derivRes.getFx());

      double[][] fx2 = Convolution.conv(
					elementWiseTimes, blurKernel, 1);
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

			
			
			double[][] fx_2 = Matrix2dOp.power( derivRes.getFx(), 2 );
			
      double[][] fy_2 = Matrix2dOp.power( derivRes.getFy() ,2 );
      
      double[][] f_sum_xy = Matrix2dOp.sum( fx_2 ,	fy_2 	);
      
      double[][] grad = Matrix2dOp.power( f_sum_xy, 0.5f);
			
			Matrix2dOp.border(grad, 5, 0);
			
			
			// -------------------------------------------------------------- //
			// Compute optical Flow
			// -------------------------------------------------------------- //
			
			//int badPixels = 0;
			
			for (int x = 0 ; x < xdim ; x++) {
				
				for (int y = 0 ; y < ydim ; y++) {
					
					Matrix bigM = new Matrix(new double[][]{ 
						{fx2[x][y], fxy[x][y]} ,
						{fxy[x][y], fy2[x][y]}
					} );
					
				
					Matrix smallB = new Matrix ( new double[][]{ { fxt[x][y]}, {fyt[x][y] } }); 
					
					double cond = bigM.cond();
					
          if ( grad[x][y] < MotionEstimator.GRADIENT_THRESHOLD || 
							cond > 100 ||
							Double.isNaN(cond)) {
						Vx[k][x][y] = 0;
						Vy[k][x][y] = 0;
						//badPixels++;
					
					} else {

					  Matrix inverse = bigM.inverse();
            Matrix v = inverse.times(smallB);

						Vx[k][x][y] = v.get(0, 0);
						Vy[k][x][y] = v.get(1, 0);
						
					}
					
				}
				
			}
			
		}

		// compute average horizontal and vertical motion

		double[] blurTemporalKernel = new double[13];
		
		for (int i = 0; i < blurTemporalKernel.length ; i++) {
		  
		  blurTemporalKernel[i] = 1f/ 13;
		  
		}
		
		int numberOfTempBlurredFrames = numberOfBlurredFrames - blurTemporalKernel.length +1;
		
    double[] v_motion = new double[numberOfTempBlurredFrames];
    
    double[] h_motion = new double[numberOfTempBlurredFrames];

		
		for (int i = 0 ; i < numberOfTempBlurredFrames; i++) {
		  
		  double[][] vx = Matrix2dOp.zeros2D(xdim, ydim);
      double[][] vy = Matrix2dOp.zeros2D(xdim, ydim);
      
      
      for (int k = 0; k < blurTemporalKernel.length; k++) {
        for (int x = 0; x <xdim ; x++) {
          for (int y = 0; y < ydim ; y++) {
            vx[x][y] += blurTemporalKernel[k] * Vx[k+i][x][y];
            vy[x][y] += blurTemporalKernel[k] * Vy[k+i][x][y];
          }
        }
      }

      double eps = Math.pow(2f, -52);
      int nx_eps = 0;
      int ny_eps = 0;
 
//      int dropped_x = 0;
//      int dropped_y = 0;
      
      for (int x = 0; x <xdim ; x++) {
        for (int y = 0; y < ydim ; y++) {

          if (Math.abs(vx[x][y]) > eps) {
            h_motion[i] += vx[x][y];
            nx_eps++;
          } else {
//            dropped_x++;  
          }

          if (Math.abs(vy[x][y]) > eps) {
            v_motion[i] += vy[x][y];
            ny_eps++;
          } else {
//            dropped_y++;
            
          }
        }
      }
      
      h_motion[i] = h_motion[i] / ( scaleFactor * nx_eps); 
      v_motion[i] = -1 * v_motion[i] / ( scaleFactor * ny_eps); 
		  
		}
		
		return new Motions(h_motion, v_motion);
		
	}

}
