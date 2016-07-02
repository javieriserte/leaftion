package org.jiserte.leaftion;

/**
 * Esta clase es para probar el c�lculo del n�mero condicional de una matriz 
 * usando el paquete JAMA
 * @author Javier
 *
 */

import Jama.*; 

public class CondExamples {

  public static void main(String[] args) {

    double[][] m = new double[2][2];
    
    m[0][0] = 1;
    m[0][1] = 2;
    m[1][0] = 3;
    m[1][1] = 4;
    
    Matrix mat = new Matrix(m);
    
    System.out.println( mat.cond() );
    
  }

}
