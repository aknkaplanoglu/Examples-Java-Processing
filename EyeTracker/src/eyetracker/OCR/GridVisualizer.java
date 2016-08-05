/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// GridVisualizer.java
// Andrew Davison, Jan 2013, ad@fivedots.coe.psu.ac.th

/* All the OpenCV processing of the input image is located in this class
   using its Java binding, JavaCV (http://code.google.com/p/javacv/).

   The processing is carried out in two phases, which are started by calls to
   findOutline() and extractGrid()

   findOutline() locates the outline of the sudoku grid, while extractGrid()
   saves a cleaned-up inverted binary image of the grid in a local file.

  Some ideas borrowed from:
    http://www.aishack.in/2010/08/detecting-a-sudoku-puzzle-in-an-image-part-1/
  and
    http://www.aishack.in/2010/01/an-introduction-to-contours/
*/



import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacpp.Loader;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;



public class GridVisualizer
{
  private static final double SMALLEST_QUAD =  50000.0;    // was 600.0;
            // ignore contours smaller than SMALLEST_QUAD pixels

  private static final int NUM_POINTS = 4;  // number of coords in box

  private static final int IM_SIZE = 600;  // size of final grid image

  private static final int NUM_BOXES = 9;
  private static final int LINE_WIDTH = 6;   // width of sudoku lines drawn onto grid


  private CanvasFrame procCanvas;   
        /* a JavaCV canvas showing the progress of the OpenCV operations;
           useful for debugging; should be removed when code is finished */

  private IplImage binaryImg;
  private Point[] pts;     // the four corners of the sudoku grid outline




  public GridVisualizer()
  {
    System.out.print("Starting OpenCV...");

    procCanvas = new CanvasFrame("Processing Canvas");
             // useful for showing JavaCV IplImage objects, to check on image processing
    procCanvas.setLocation(0, 0);
  }  // end of GridVisualizer()



  public void close()
  {  procCanvas.dispose(); } 



  public Point[] findOutline(IplImage img)
  // find grid outline points inside the image
  {
    long startTime = System.currentTimeMillis();

    IplImage im = enhance(img);
    procCanvas.showImage(im);  

    binaryImg = cvCloneImage(im);    // save for later

    CvSeq quad = findBiggestQuad(im);
    if (quad == null) {
      // System.out.println("Sudoku grid not found");
      return null;
    }

    pts = clockSort(quad);

    long duration = System.currentTimeMillis() - startTime;
    System.out.println("Grid outline found in " + Math.round(duration) + "ms");
    System.out.println();

    return pts;
  }  // end of findOutline()




  private IplImage enhance(IplImage img)
  /* enhance the image quality by smoothing and adaptive thresholding,
     returning an inverted binary image */
  {
    // convert to grayscale
    IplImage im = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
    cvCvtColor(img, im, CV_BGR2GRAY);  

    // remove image noise
    cvSmooth(im, im, CV_GAUSSIAN, 7, 7, 0, 0);    // was 11 x 11

    /* compensate for glare, and convert image to inverse b&w image
       -- black background, white letters, border, etc.  */
    cvAdaptiveThreshold(im, im, 255,
            CV_ADAPTIVE_THRESH_MEAN_C,     // CV_ADAPTIVE_THRESH_GAUSSIAN_C
            CV_THRESH_BINARY_INV,          // CV_THRESH_BINARY,
            5, 2);   // block size and offset (3, 5)

    return im;
  }  // end of enhance()



  private CvSeq findBiggestQuad(IplImage img)
  /* return the biggest contour above a minimum area of SMALLEST_QUAD
     which is a convex shape with NUM_POINTS (4) corners (i.e. is a 
     quadrilateral).
  */
  {
    CvMemStorage storage = CvMemStorage.create();

    CvSeq bigQuad = null;

    // generate all the contours in the threshold image as a list
    CvSeq contours = new CvSeq(null);
    cvFindContours(img, storage, contours, Loader.sizeof(CvContour.class),
                                                CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
    // find the largest quad in the list of contours
    double maxArea = SMALLEST_QUAD;

    while (contours != null && !contours.isNull()) {
      if (contours.elem_size() > 0) {
        CvSeq quad = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, 
                      CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.08, 0);     // 0.02   --- 0.1
                          // the maximum distance between the original curve and its approximation.
        CvSeq convexHull = cvConvexHull2(quad, storage, CV_CLOCKWISE, 1);

        if(convexHull.total() == NUM_POINTS) {
          double area = Math.abs( cvContourArea(convexHull, CV_WHOLE_SEQ, 0) );
          if (area > maxArea) {
            // System.out.println("hull area: " + area);
            maxArea = area;
            bigQuad = convexHull;
          }
        }
      }
      contours = contours.h_next();
    }
    return bigQuad;
  }  // end of findBiggestQuad()



  private Point[] clockSort(CvSeq quad)
  /* Create a Java array of Point objects, which are ordered 
     in clockwise order starting from the point nearest the origin.
     
     This ordering is needed for the subsequent warping of the image.
  */
  {
    Point[] pts = new Point[NUM_POINTS];
    CvPoint pt;
    for(int i=0; i < NUM_POINTS; i++) {
      pt = new CvPoint( cvGetSeqElem(quad, i));
      pts[i] = new Point(pt.x(), pt.y());
    }

    // move pt that is closest to origin into pts[0]
    int minDist = dist2(pts[0]);
    Point temp;
    for (int i=1; i < pts.length; i++) {
      int d2 = dist2(pts[i]);
      if (d2 < minDist) {
        temp = pts[i];   // swap points
        pts[i] = pts[0];
        pts[0] = temp;
        minDist = d2;
      }
    }
      
/*
    System.out.println("Nat Point");
    for(Point p : pts)
      System.out.println(p);
*/
    // sort into clockwise order starting with the base point pts[0].
    ClockwiseComparator pComp = new ClockwiseComparator(pts[0]);
    Arrays.sort(pts, pComp);
/*
    System.out.println("Point Comp");
    for(Point p : pts)
      System.out.println(p);
*/
    return pts;
  }  // end of clockSort()



  private int dist2(Point pt)
  {  return (pt.x*pt.x) + (pt.y*pt.y);  }


  //--------------------------- 2nd stage -----------------------------------


  public boolean extractGrid(String outFnm)
  // extract grid from image, clean it, and save to outFnm
  {
    long startTime = System.currentTimeMillis();

    if (pts == null) {
      System.out.println("No grid found");
      return false;
    }

    IplImage squareIm = warp(binaryImg, pts);   // warp grid into a square
    procCanvas.showImage(squareIm);  

    IplImage gridIm = cleanGrid(squareIm);

    long duration = System.currentTimeMillis() - startTime;
    System.out.println("Grid extracted in " + Math.round(duration) + "ms");
    System.out.println();

    procCanvas.showImage(gridIm);  

    System.out.println("Saving grid image to " + outFnm);
    cvSaveImage(outFnm, gridIm);
    return true;
  }  // end of extractGrid()




  private IplImage warp(IplImage im, Point[] pts)
  /* Apply perspective warping to the image as bordered by the points
     in pts[]. Warp into to a IMSIZE*IM_SIZE square
   */
  {
    CvMat srcPts = CvMat.create(NUM_POINTS, 2);
    for(int i=0; i < NUM_POINTS; i++) {
      // System.out.println("" + i + ". (" + pts[i].x + ", " + pts[i].y + ")");
      srcPts.put(i, 0, pts[i].x);
      srcPts.put(i, 1, pts[i].y);
    }

    CvMat dstPts = CvMat.create(4, 2);
    dstPts.put(0, 0, 0);      // clockwise ordering (0,0)
    dstPts.put(0, 1, 0);

    dstPts.put(1, 0, 0);       // (0,size)
    dstPts.put(1, 1, IM_SIZE-1);

    dstPts.put(2, 0, IM_SIZE-1);    // (size, size)
    dstPts.put(2, 1, IM_SIZE-1); 

    dstPts.put(3, 0, IM_SIZE-1);    // (size, 0)
    dstPts.put(3, 1, 0);

    CvMat totalWarp = CvMat.create(3, 3);
    JavaCV.getPerspectiveTransform(srcPts.get(), dstPts.get(), totalWarp);
    // System.out.println("totalWarp: " + totalWarp);
    
    IplImage warpImg = IplImage.create(IM_SIZE, IM_SIZE, IPL_DEPTH_8U, 1);
    cvWarpPerspective(im, warpImg, totalWarp);

    cvSaveImage("warp.png", warpImg);

    return warpImg;
  }  // end of warp()



  private IplImage cleanGrid(IplImage im)
  /* Fill the long white vertical lines of the Sudoku grid with black.
     This uses flood-filling so wll also blacken any horizontal lines
     connected to the vertical ones, leaving only the Sudoku numbers (hopefully) 
  */
  {
    cvDilate(im, im, null, 2);   
            // make white stuff larger (e.g. letters, lines)
    procCanvas.showImage(im);  

    highlightVerticals(im);
       // make the vertical grid lines continuous and thicker

    // flood-fill at small intervals across the top of the image
    for(int i=0; i < im.width(); i=i+3)   // across
      cvFloodFill(im, new CvPoint(i, 1), CvScalar.BLACK,
             cvScalarAll(5), cvScalarAll(5), null, 4, null);
           // max lower and upper brightness differences; 4 is pixel connectivity

    cvErode(im,im, null, 2);   // return white stuff to previous size

    return im;
  }  // end of cleanGrid()





  private void highlightVerticals(IplImage im)
  /* Draws white vertical lines over the assumed location of the vertical
     Grid lines to make them easier to delete.
  */
  {
    double lineStep =  ((double)(IM_SIZE-1))/NUM_BOXES;
    int imHeight = im.height();
    int imWidth = im.width();
    int offset = LINE_WIDTH/2;
 
    // draw vertical lines
    for(int x=0; x < imWidth; x+= lineStep)
      cvRectangle(im, cvPoint((int)x-offset, 0), cvPoint((int)x+offset, imHeight), 
                    CvScalar.WHITE, CV_FILLED, CV_AA, 0); 
  }  // end of highlightVerticals()



  // --------------- Point comparators -----------------


  private class ClockwiseComparator implements Comparator<Point> 
  /* This comparator compares points according to the polar angle
    they make with an 'origin' point. The polar angle is measured with
    respect to a ray emanating from the origin and pointing eastward.
  
     The points are placed in clocwise order realative to the origin
  */
  {
    private Point orig;

    public ClockwiseComparator(Point orig)
    {  this.orig = orig;  }


    public int compare(Point p1, Point p2) 
    /* The three points (orig, p1, p2) are in clockwise order if
       ccw < 0, counter-clockwise if ccw > 0, and collinear if ccw = 0.
       ccw gives the signed area of the triangle formed by orig, p1, p2.
       Based on ccw() at:
         http://algs4.cs.princeton.edu/25applications/Point2D.java.html
    */
    {
      int ccw = (p1.x-orig.x)*(p2.y-orig.y) - (p1.y-orig.y)*(p2.x-orig.x);
      if (ccw < 0)       // clockwise
        return -1;
      else if (ccw > 0)  // counter-clockwise
        return 1;
      else    // collinear
        return 0;
    }  // end of compare()

  }  // end of ClockwiseComparator class

} // end of GridVisualizer class

