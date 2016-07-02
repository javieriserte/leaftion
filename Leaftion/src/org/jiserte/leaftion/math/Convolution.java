package org.jiserte.leaftion.math;

public class Convolution {
  
  public static final int AXIS_X=0;
  public static final int AXIS_Y=1;
  
  public static double[][] conv(double[][] input, double[] kernel, int axis) {
    
    int len_x = input.length;
    int len_y = input[0].length;
    
    int len_c = axis == AXIS_X ? len_x : len_y;

    
    double[][] result = new double[len_x][len_y];
    
    int semi_k = (int) ((kernel.length - 1) /2);
    
    for ( int i =0; i < len_x ; i++) {
      for ( int j =0; j < len_y ; j++) {

        double new_val = 0;
        
        int c_a = axis == AXIS_X ? i : j;
       
        int i_index = Math.min(Math.max( 0,  semi_k-c_a ) , semi_k);
        int e_index = Math.min(Math.max( semi_k,  len_c - (c_a+1)+semi_k ) , kernel.length-1);
        
        for (int k = i_index ; k<= e_index; k++) {
          
          new_val += kernel[k] * input[i+(k-semi_k) * (1-axis)] [j+(k-semi_k)*(axis)]; 

        }
        
        result[i][j] = new_val;
        
      }

    }
    
    return result;
    
  }
  
  public static void main(String[] args) {
    
    double[][] m = new double[][] { 
      {1 ,   2 ,   3   , 4  ,  5  ,  6},
      {2 ,   4 ,   6   , 8  , 10  , 12},
      {3 ,   6 ,   9   ,12  , 15  , 18},
      {4 ,   8 ,  12   ,16  , 20  , 24},
      {5 ,  10 ,  15   ,20  , 25  , 30},
      {6 ,  12 ,  18   ,24  , 30  , 36}  };
      
    System.out.println(m[0][5]);
      
    double[] kernel = new double[]{0.3,0.4,0.3};
    
    double[][] cmat = conv(m, kernel, 0);
    cmat = conv(cmat, kernel, 1);
      
      
    System.out.println(matrixToString(m));
    System.out.println(matrixToString(cmat));
      
    

  }
  
  public static String matrixToString(double[][] mat) {
    
    StringBuilder sb = new StringBuilder();

      for (int i =0; i< mat.length; i++) {
        
        for (int j=0; j < mat[0].length;j++) {
          

        sb.append( String.format("%6.2f ", mat[i][j])); 
        
      }
      
      sb.append( "\n" );
      
    }
    
    return sb.toString();
    
  }

}
