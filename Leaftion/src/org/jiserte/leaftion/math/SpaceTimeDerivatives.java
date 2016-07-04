package org.jiserte.leaftion.math;

import java.util.Arrays;

public class SpaceTimeDerivatives {
	
	private static final double[][] pre = new double[][] {  
		{ 0.5       ,0.5 }      , 
		{ 0.223755  ,0.552490   , 0.223755 } ,
		{ 0.092645  ,0.407355   , 0.407355   , 0.092645 } , 
		{ 0.036420  ,0.248972   , 0.429217   , 0.248972   , 0.036420 } , 
		{ 0.013846  ,0.135816   , 0.350337   , 0.350337   , 0.135816   , 0.01384  } ,
		{ 0.005165  ,0.068654   , 0.244794   , 0.362775   , 0.244794   , 0.068654 , 0.005165 } };
                                                                      
	private static final double[][] deriv = new double[][] {  
        { -1        , 1 }       , 
        { -0.453014 , 0.0       , 0.453014 } , 
        { -0.236506 , -0.267576 , 0.267576   , 0.236506 } , 
        { -0.108415 , -0.280353 , 0.0        , 0.280353   , 0.108415 } , 
        { -0.046266 , -0.203121 , -0.158152  , 0.158152   , 0.203121   , 0.046266 } , 
        { -0.018855 , -0.123711 , -0.195900  , 0.0        , 0.195900   , 0.123711 , 0.018855 } };
		                                                  
	
	public static DerivativeResults derivate( double[][][] timeSeries , int startFrame, int length) {

		int width  = timeSeries[0].length;
		int height = timeSeries[0][0].length; 
		
		
	    double[] currentPre   = SpaceTimeDerivatives.getPre(length);
	    double[] currentDeriv = SpaceTimeDerivatives.getDeriv(length);
	    
		double[][] fdt = Matrix2dOp.zeros2D(width, height);
		double[][] fpt = Matrix2dOp.zeros2D(width, height);
	  
		for (int i=0; i< length; i++) {
			
			fdt = Matrix2dOp.sum(fdt, Matrix2dOp.times( 
					timeSeries[startFrame + i], currentDeriv[i]) );
			
			fpt = Matrix2dOp.sum(fpt, Matrix2dOp.times( 
					timeSeries[startFrame + i], currentPre[i]) );
			
		}
		
	    double[][] fx = Convolution.conv(fpt, currentPre, 1);
	    fx =  Convolution.conv(fx, currentDeriv, 0);		

	    double[][] fy = Convolution.conv(fpt, currentPre, 0);
	    fy =  Convolution.conv(fy, currentDeriv, 1);	
	    
	    double[][] ft = Convolution.conv(fdt, currentPre, 1);
	    ft =  Convolution.conv(ft, currentPre, 0);	

	    return new DerivativeResults(fx, fy, ft);
		
		
	}
	
	private static double[] getPre(int size) {

		if (size >=2 && size <=7) {
			return pre[size-2];
		}
		
		return null;
		
	}
	
	private static double[] getDeriv(int size) {

		if (size >=2 && size <=7) {
			return deriv[size-2];
		}
		
		return null;
		
	}
	
	
	public static void main(String[] args) {
		
	  double[][][] timeSeries = new double[7][10][10];
	  
	  for (int f = 0 ; f < 7 ; f++) {
	    
	    for (int x = 0; x < 10; x++) {
	      
	      for (int y = 0 ; y < 10 ; y ++) {
	        
	        timeSeries[f][x][y] = x+1 + 2*(y+1) +f+1;
	        
	      }
	      
	    }
	    
	  }
	  
	  DerivativeResults r = SpaceTimeDerivatives.derivate(timeSeries, 0, 7);
	  
	  System.out.println( Matrix2dOp.toString(r.getFx(),null) );

    System.out.println( Matrix2dOp.toString(r.getFy(),null) );

	  System.out.println( Matrix2dOp.toString(r.getFt(),null) );

	  
	}

}

/*
N	= size( f, 2 );
dims	= size( f(1).im );

%%% DEFINE DERIVATIVE KERNELS (farid&simoncelli, 1997)
if( N == 2 )
	pre	= [0.5 0.5];
	deriv	= [-1 1];
elseif( N == 3 )
	pre	= [0.223755 0.552490 0.223755];
	deriv	= [-0.453014 0.0 0.453014];
elseif( N == 4 )
	pre	= [0.092645 0.407355 0.407355 0.092645];
	deriv	= [-0.236506 -0.267576 0.267576 0.236506];
elseif( N == 5 )
	pre	= [0.036420 0.248972 0.429217 0.248972 0.036420];
	deriv	= [-0.108415 -0.280353 0.0 0.280353 0.108415];
elseif( N == 6 )
	pre 	= [0.013846 0.135816 0.350337 0.350337 0.135816 0.01384];
	deriv	= [-0.046266 -0.203121 -0.158152 0.158152 0.203121 0.046266];
elseif( N == 7 )
	pre = [0.005165 0.068654 0.244794 0.362775 0.244794 0.068654 0.005165];
	deriv= [-0.018855 -0.123711 -0.195900 0.0 0.195900 0.123711 0.018855];
else
	warning( sprintf( 'No such filter size (N=%d)', N ) );
end

%%% SPACE/TIME DERIVATIVES
fdt	= zeros( dims );
fpt	= zeros( dims );
for i = 1 : N
   fdt = fdt + deriv(i)*f(i).im;
   fpt = fpt + pre(i)*f(i).im;
end

fx	= conv2( conv2( fpt, pre', 'same' ), deriv, 'same' );
fy	= conv2( conv2( fpt, pre, 'same' ), deriv', 'same' );
ft	= conv2( conv2( fdt, pre', 'same' ), pre, 'same' );

*/