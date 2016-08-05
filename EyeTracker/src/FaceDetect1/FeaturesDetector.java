/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FaceDetect1;


// FeaturesDetector.java
// Andrew Davison, May 2013, ad@fivedots.psu.ac.th

/* Use Haar cascade classifiers to find the face, left eye, right eye,
   nose, and mouth in an input image. The matching bounding boxes are
   drawn as differently colored rectangles over the input image (if they
   are found).

   ROIs (regions of interest) are used to improve the chances of the
   classifiers finding a feature in the image.

   The test input images are loaded from the FACE_DIR directory (faces/)

   Usage:
      run FeaturesDetector ad.jpg
*/

import javax.swing.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacpp.Loader;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;



public class FeaturesDetector
{
  private static final String FACE_DIR = "F:\\Netbeans Projects\\EyeTracker\\faces\\";     // where the test input images are stored

  private static final String HAAR_DIR = "F:\\Netbeans Projects\\EyeTracker\\";

  private static final String FACE = "haarcascade_frontalface_alt2.xml";
  private static final String LEFT_EYE = "haarcascade_mcs_lefteye.xml";
  private static final String RIGHT_EYE = "haarcascade_mcs_righteye.xml";
  private static final String NOSE = "haarcascade_mcs_nose.xml";
  private static final String MOUTH = "haarcascade_mcs_mouth.xml";

  // JavaCV variables
  private static CvMemStorage storage;
  private static IplImage drawImg;    // original color image




  public static void main(String[] args)
  {
    if (args.length != 1) {
      System.out.println("Usage: run FeaturesDetector <fnm>");
      System.exit(0);
    }

    // preload the opencv_objdetect module to work around a known bug
    Loader.load(opencv_objdetect.class);

    storage = CvMemStorage.create(); // create storage used during object detection


    IplImage grayImage = loadGrayImage(args[0]);

    // find the face first
    CvRect faceRect = detectFeature(grayImage, "face", FACE, 
                                                 0, 0, null, CvScalar.RED);
    if (faceRect == null)
      System.out.println("No face detected");
    else
      detectFacialFeatures(grayImage, faceRect);
    
    // draw the input image and any feature boxes
    CanvasFrame canvas = new CanvasFrame("Features Detector");
    canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    canvas.showImage(drawImg);
  }  // end of main()



  private static IplImage loadGrayImage(String fnm)
  {
    IplImage img = cvLoadImage(FACE_DIR+fnm);
    if (img == null) {
      System.out.println("Could not load " + FACE_DIR+fnm);
      System.exit(1);
    }
    else
      System.out.println("Loaded " + FACE_DIR+fnm);

    drawImg = img.clone();    // original color image

    // convert to grayscale
    IplImage grayImage = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
    cvCvtColor(img, grayImage, CV_BGR2GRAY);

    // equalize the grayscale
    cvEqualizeHist(grayImage, grayImage);

    return grayImage;
  }  // end of loadGrayImage()



  private static CvRect detectFeature(IplImage im, String featureName, 
                        String haarFnm,  int xF, int yF,  CvRect selectRect, CvScalar color)
  /* Load the Haar classifier from the named file, and apply it to im
     restricted to the area specified by the selection rectangle (selectRect).
   
     Return the first matching feature rectangle (fRect) 
     *and* draw the rectangle in the specified color
     onto the user's input image. This requires the (x,y) coordinates of the
     rectangle (fRect) be converted into input image coordinates.

     This conversion is tricky since fRect is defined relative to the selection rectangle (selectRect),
     which is defined relative to the image, im. im may *not* be the
     input image, in which case (xF, yF) is im's origin relative to the input image.
  */
  {
    // instantiate a classifier cascade for the feature detection
    CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(HAAR_DIR + haarFnm));
    if (classifier.isNull()) {
      System.out.println("Could not load the classifier: " + haarFnm + " for " + featureName);
      return null;
    }

    // use selection rectangle to apply a ROI to the image
    int xSelect = 0;
    int ySelect = 0;
    if (selectRect != null) {
      cvSetImageROI(im, selectRect);
      xSelect = selectRect.x();
      ySelect = selectRect.y();
    }
    // System.out.println(featureName + " y: " + ySelect);

    CvSeq featureSeq = cvHaarDetectObjects(im, classifier, storage, 1.1, 1,
                              CV_HAAR_DO_ROUGH_SEARCH | CV_HAAR_FIND_BIGGEST_OBJECT);
        // speed things up by searching for only a single, largest feature subimage

    cvClearMemStorage(storage);
    cvResetImageROI(im);

    int total = featureSeq.total();
    if (total == 0) {
      System.out.println("  No " + featureName + " found");
      return null;
    }

    if (total > 1) // this case should not happen, but included for safety
      System.out.println("Multiple features detected (" + total + ") for " +
                              featureName + "; using the first");

    CvRect fRect = new CvRect(cvGetSeqElem(featureSeq, 0));
        /* this feature rectangle is defined relative to the coordinates of
           the select rectangle */

    // draw feature rectangle on input image
    int xDraw = xF + xSelect + fRect.x();    // convert fRect back to input image coords
    int yDraw = yF + ySelect + fRect.y();
    if (fRect != null)
      cvRectangle(drawImg, cvPoint(xDraw, yDraw),
                     cvPoint(xDraw + fRect.width(), yDraw + fRect.height()),
                  color, 2, CV_AA, 0);    // thicker line
    return fRect;
  }  // end of detectFeature()




  private static void detectFacialFeatures(IplImage grayImage, CvRect faceRect)
  // features detected: left eye, right eye, nose, mouth, all relative to the face
  {
    int xF = faceRect.x();     // these (x,y) coords are relative to the gray image 
    int yF = faceRect.y();
    int wF = faceRect.width();
    int hF = faceRect.height();
    // System.out.println("face : (" + xF + ", " + yF + "); " + wF + " - " + hF);


    /* extract the face image from the larger grayscale input image; 
       use this for the rest of the detections  */
    IplImage faceImage = IplImage.create(faceRect.width(), 
                              faceRect.height(), IPL_DEPTH_8U, 1);
    cvSetImageROI(grayImage, faceRect);
    cvCopy(grayImage, faceImage);


    // detect eyes (assume in left and right halves of image)

    CvRect selectRect = new CvRect(0, 0, wF/2, hF);     // left eye 
         /* the selection (x,y) coords are relative to the face image */
    CvRect leRect = detectFeature(faceImage, "left eye", LEFT_EYE, 
                                              xF, yF, selectRect, CvScalar.MAGENTA);

    selectRect = new CvRect(wF/2, 0, wF/2, hF);    // right eye
    CvRect reRect = detectFeature(faceImage, "right eye", RIGHT_EYE, 
                                              xF, yF, selectRect, CvScalar.YELLOW);

    // nose detection (below top of eyes)
    int yEye = hF/3;  // guess eye top
    if ((leRect != null) && (reRect != null))
      yEye = (leRect.y() < reRect.y()) ? leRect.y() : reRect.y();    // choose higher

    selectRect = new CvRect(0, yEye, wF, hF-yEye);
    CvRect noseRect = detectFeature(faceImage, "nose", NOSE, 
                                                xF, yF, selectRect, CvScalar.GREEN);

    // mouth detection  (below nose middle)
    int noseMid = hF/2;  // guess middle of nose
    if (noseRect != null)
      noseMid = yEye + noseRect.y() + noseRect.height()/2;    // a tricky calculation :)

    selectRect = new CvRect(0, noseMid, wF, hF-noseMid);
    detectFeature(faceImage, "mouth", MOUTH, xF, yF, selectRect, CvScalar.BLUE);
  }  // end of detectFacialFeatures()


}  // end of FeaturesDetector class
