
// EigenvalueDecomp.java
// Sajan Joseph, sajanjoseph@gmail.com
// http://code.google.com/p/javafaces/
// Modified by Andrew Davison, April 2011, ad@fivedots.coe.psu.ac.th



import cern.colt.matrix.linalg.EigenvalueDecomposition;


public class EigenvalueDecomp extends EigenvalueDecomposition
{
  public EigenvalueDecomp(Matrix2D dmat)
  {  super(dmat); }


  public double[] getEigenValues()
  {  return diag( getD().toArray());  }


  public double[][] getEigenVectors()
  {  return getV().toArray(); }


  private double[] diag(double[][] m)
  {
    double[] diag = new double[m.length];
    for (int i = 0; i < m.length; i++)
      diag[i] = m[i][i];
    return diag;
  }  // end of diag()
	
}  // end of EigenvalueDecomp class
