/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FaceDetectionFromImage {
        
        
        
        private static final int SCALE = 2;
        // scaling factor to reduce size of input image
        // cascade definition for face detection

        //String CASCADE_FILE ="/home/ashwin/workspace/hadoop-computer-vision/classifiers/haarcascade_frontalface_alt2.xml";

        //String OUT_FILE = "markedFaces.jpg";


        public static void main(String[] args){

                /*if (args.length != 1) {
                        System.out.println("Usage: run FaceDetectionFromImage <input-file>");
                        return;
                }
                
                System.out.println("Starting OpenCV...");*/
                
                //      preload the opencv_objdetect module to work around a known bug
                //Loader.load(opencv_objdetect.class);
                
                // load an image
                //System.out.println("Loading image from " + args[0]);
                
                String CASCADE_FILE ="F:\\Netbeans Projects\\objectd\\haarcascade_frontalface_alt2.xml";
                String OUT_FILE = "markedFaces.jpg";
                
                IplImage origImg = cvLoadImage("honeykk9p.jpg", 1);
                //IplImage origImg = cvLoadImage(args[0]);
                
                // convert to grayscale
                IplImage grayImg = IplImage.create(origImg.width(),origImg.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(origImg, grayImg, CV_BGR2GRAY);
                
                // scale the grayscale (to speed up face detection)
                IplImage smallImg = IplImage.create(grayImg.width()/SCALE,grayImg.height()/SCALE, IPL_DEPTH_8U, 1);
                cvResize(grayImg, smallImg, CV_INTER_LINEAR);

                // equalize the small grayscale
                IplImage equImg = IplImage.create(smallImg.width(),smallImg.height(), IPL_DEPTH_8U, 1);
                cvEqualizeHist(smallImg, equImg);

                // create temp storage, used during object detection
                CvMemStorage storage = CvMemStorage.create();

                // instantiate a classifier cascade for face detection

                CvHaarClassifierCascade cascade =new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
                System.out.println("Detecting faces...");

                CvSeq faces = cvHaarDetectObjects(equImg, cascade, storage,1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

                cvClearMemStorage(storage);

                // draw thick yellow rectangles around all the faces
                int total = faces.total();
                System.out.println("Found " + total + " face(s)");

                for (int i = 0; i < total; i++) {

                        CvRect r = new CvRect(cvGetSeqElem(faces, i));
                        cvRectangle(origImg, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.RED, 6, CV_AA, 0);
 
                        String strRect = String.format("CvRect(%d,%d,%d,%d)", r.x(), r.y(), r.width(), r.height());
                        
                        System.out.println(strRect);
                        //undo image scaling when calculating rect coordinates
                }
                
                if (total > 0) {
                        System.out.println("Saving marked-faces version of " + " in " + OUT_FILE);

                        cvSaveImage(OUT_FILE, origImg);
                }
                
                
        } // end of main()

}               // end of FaceDetectionFromImage class
