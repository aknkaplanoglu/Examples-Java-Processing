
// Matrix2D.java
// Sajan Joseph, sajanjoseph@gmail.com
// http://code.google.com/p/javafaces/
// Modified by Andrew Davison, April 2011, ad@fivedots.coe.psu.ac.th


import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;


public class Matrix2D extends DenseDoubleMatrix2D
{

  public Matrix2D(double[][] data)
  {  super(data);  }

  public Matrix2D(DoubleMatrix2D dmat)
  { super(dmat.toArray()); }

  public Matrix2D(int rows, int cols)
  {  super(rows, cols); }


  public Matrix2D(double[] data, int rows)
  {
    super(rows, ((rows != 0) ? data.length/rows : 0));
    int columns = (rows != 0) ? data.length/rows : 0;
    if ((rows * columns) != data.length)
      throw new IllegalArgumentException("Array length must be a multiple of " + rows);

    double[][]vals = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++)
        vals[i][j] = data[i + (j * rows)];
    }
    assign(vals);		
  }  // end of Matrix2D()



  public Matrix2D getSubMatrix(int rows)
  {  return new Matrix2D(viewPart(0, 0, rows, super.columns()).copy()); }



  public static void fitToUnitLength(double[] data)
  {
    double max = max(data);
    for (int i = 0; i < data.length; i++)
      data[i] /= max;
  }  // end of fitToUnitLength()


  public void subtractMean()
  // subtract column mean from each row
  {  subtractFromEachRow( getAverageOfEachColumn() );  }


  public double[] getAverageOfEachColumn()
  // calculate mean face vector as an array
  {
    double[][] data = this.toArray();
    double total;
    double[] avgValues = new double[this.columns];

    for (int col = 0; col < this.columns; col++) {
      total = 0.0;
      for (int row = 0; row < this.rows; row++)
        total += data[row][col];
      avgValues[col] = total/this.rows;
    }
    return avgValues;
  }  // end of getAverageOfEachColumn()
	


  public void replaceRowsWithArray(double[] data)
  {
    if (this.columns != data.length)
      throw new RuntimeException(
             "matrix columns not matching number of input array elements");
		
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.columns; col++)
        set(row, col, data[col]);
    }
  }  // end of replaceRowsWithArray()



  public void normalise()
  {
    double[][] temp = this.toArray();
    double[] mvals = new double[temp.length];

    for (int i = 0; i < temp.length; i++)
      mvals[i] = max(temp[i]);

    for (int i = 0; i < temp.length; i++) {
      for (int j = 0; j < temp[0].length; j++)
        temp[i][j] /= mvals[i];
    }
    assign(temp);
  }  // end of normalise()



  private static double max(double[] arr)
  {
    double max = Double.MIN_VALUE;
    for (int i = 0; i < arr.length; i++)
      max = Math.max(max, arr[i]);			
    return max;
  }  // end of max()


  public void subtract(Matrix2D mat)
  {  assign(mat, Functions.functions.minus); }


  public void add(Matrix2D mat)
  {  assign(mat, Functions.functions.plus);  }


  public void subtractFromEachRow(double[] oneDArray)
  {
    double[][] denseArr = this.toArray();
    for (int i = 0; i < denseArr.length; i++) {
      for (int j = 0; j < denseArr[0].length; j++)
        denseArr[i][j] -= oneDArray[j];
    }
    assign(denseArr);
  }  // end of subtractFromEachRow()


  public Matrix2D multiply(Matrix2D mat)
  {  return new Matrix2D(this.zMult(mat, null));  }	


  public void multiplyElementWise(Matrix2D mat)
  {  assign(mat, Functions.functions.mult);  }


  public Matrix2D transpose()
  {  return new Matrix2D(this.viewDice()); }


  public double[] flatten()
  {
    double[] res = new double[this.rows * this.columns];
    int i = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.columns; col++)
        res[i++] = get(row, col);
    }
    return res;
  }  // end of flatten()
	

  public static double norm(double[] arr)
  {
    double val = 0.0;
    for (int i = 0; i < arr.length; i++)
      val += (arr[i] * arr[i]);
    return val;
  }  // end of norm()
	

  public static void subtract(double[] inputFace, double[] avgFace)
  {
    for (int i = 0; i < inputFace.length; i++)
      inputFace[i] -= avgFace[i];
  }
	

  public EigenvalueDecomp getEigenvalueDecomp()
  {  return new EigenvalueDecomp(this);  }	
	
}  // end of Matrix2D class
