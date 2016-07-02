package org.jiserte.leaftion.math;

import java.util.Arrays;

public class Matrix2dOp {
	
	public static double[][] power( double[][] matrix, double power) {
		
		int lengthX = matrix.length;
		
		int lengthY = matrix[0].length;
		
		double[][]  result = new double[lengthX][lengthY];
		
		for (int x = 0; x < lengthX; x++) {
			
			for (int y = 0; y < lengthY; y++) {
				
				
				result[x][y] =  Math.pow(matrix[x][y] ,power);
				
				
			}
			
		}
		
		return result;
		
	}
	
	public static double[][] sum ( double[][] matrixA, double[][] matrixB ) {
		
		int lengthX = matrixA.length;
		
		int lengthY = matrixA[0].length;
		
		double[][]  result = new double[lengthX][lengthY];
		
		for (int x = 0; x < lengthX; x++) {
			
			for (int y = 0; y < lengthY; y++) {
				
				
				result[x][y] =  matrixA[x][y] + matrixB[x][y];  
				
				
			}
			
		}
		
		return result;
		
	}
	
	
	public static double[][] times ( double[][] matrixA, double scalar ) {
		
		int lengthX = matrixA.length;
		
		int lengthY = matrixA[0].length;
		
		double[][]  result = new double[lengthX][lengthY];
		
		for (int x = 0; x < lengthX; x++) {
			
			for (int y = 0; y < lengthY; y++) {
				
				
				result[x][y] =  matrixA[x][y] * scalar;  
				
				
			}
			
		}
		
		return result;
		
	}
	
	public static double[][] elementWiseTimes( double[][] matrixA, double[][] matrixB ) {
		
		
		int lengthX = matrixA.length;
		
		int lengthY = matrixA[0].length;
		
		double[][]  result = new double[lengthX][lengthY];
		
		for (int x = 0; x < lengthX; x++) {
			
			for (int y = 0; y < lengthY; y++) {
				
				
				result[x][y] =  matrixA[x][y] * matrixB[x][y];  
				
				
			}
			
		}
		
		return result;
		
		
	}
	
	
	public static double[][] zeros2D(int width, int height) {
		
		double[][] result = new double[width][height];
		
		for (int i = 0 ; i < width; i++) {
			
			Arrays.fill(result[i], 0);
		
		}
		return result; 
		
	}
	
	public static double[][][] zeros3D(int width, int height, int depth) {
	
		double[][][] result = new double[depth][width][height];
		
		for (int i = 0 ; i < depth; i++) {
			
			Arrays.fill(result[i], zeros2D(width, height));
		
		}
		return result; 
		
		
	}
	
	public static void main(String ... strings) {
		
		double[][] a = zeros2D(5, 10);
		
		System.out.println(toString(a,"%4.2f"));
		
	}
	
	public static String toString (double[][] matrix, String format ) {
		
		StringBuilder sb = new StringBuilder();
		
		if (format ==null) {
			format = "%8.4f";
		}
		
		for (int y=0; y < matrix[0].length; y++) {
			sb.append("[ ");
			for (int x=0; x < matrix.length; x++) {
				
				
						;
				sb.append(String.format( format+" ", matrix[x][y]));
				
			}
			
			sb.append("]"+System.getProperty("line.separator"));
			
		}
		
		return sb.toString();
		
	}
	
	public static void border(double[][] matrix, int width, double value) {
		
		for (int i=0 ; i < width; i++) {
			
			for (int x=0; x < matrix.length; x++) {
				
				matrix[x][i] = value;
				
				matrix[x][matrix[0].length-1-i] = value;
				
			}
			
			for (int y=0; y < matrix[0].length; y++) {
				
				matrix[y][i] = value;
				
				matrix[y][matrix.length-1-i] = value;
				
			}
			
		}
		
	}

}
