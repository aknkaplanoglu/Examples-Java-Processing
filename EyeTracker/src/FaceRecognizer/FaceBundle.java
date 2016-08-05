
// FaceBundle.java
// Sajan Joseph, sajanjoseph@gmail.com
// http://code.google.com/p/javafaces/
// Modified by Andrew Davison, April 2011, ad@fivedots.coe.psu.ac.th



import java.io.Serializable;
import java.util.*;


public class FaceBundle implements Serializable
{
  private double[][] imageRows;  // each row contains a training image 
  private ArrayList<String> imageFnms;	
  private double[] avgImage;   // average training image
  private double[][] eigenFaces;     // the eigenvectors for the face images
  private double[] eigenValues;
  private int imageWidth, imageHeight;
	

  public FaceBundle(ArrayList<String> nms, double[][] ims, double[] avgImg, 
                    double[][] facesMat, double[] evals, int w, int h)
  {
    imageFnms = nms;
    imageRows = ims;		
    avgImage = avgImg;
    eigenFaces = facesMat;
    eigenValues = evals;
    imageWidth = w;
    imageHeight = h;
  }  // end of FaceBundle()
	

  public double[][] getImages()
  {  return imageRows;  }

  public double[][] getEigenFaces()
  {  return eigenFaces;  }

  public int getNumEigenFaces()
  {  return eigenFaces.length;  }

  public double[] getAvgImage()
  {  return avgImage; }

  public double[] getEigenValues()
  {  return eigenValues;  }

  public ArrayList<String> getImageFnms()
  {  return imageFnms;  }

  public int getImageWidth()
  {  return imageWidth; }

  public int getImageHeight()
  {  return imageHeight;  }



  public double[][] calcWeights(int numEFs)
  /* Calculate the weights for the chosen subset of eigenfaces.
     The weights can be thought of as the rotated image coordinates
     so the eigenfaces (eigenvectors) becomes axes.
  */
  {
    Matrix2D imsMat = new Matrix2D(imageRows);

    Matrix2D facesMat = new Matrix2D(eigenFaces);
    Matrix2D facesSubMatTr = facesMat.getSubMatrix(numEFs).transpose();

    Matrix2D weights  = imsMat.multiply(facesSubMatTr);
    return weights .toArray();
  }  // end of calcWeights()



}  // end of FaceBundle class
