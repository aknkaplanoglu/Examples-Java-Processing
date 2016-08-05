/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import java.nio.Buffer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class HandDetection {
      public Buffer buf = null;
    public static void main(String[] args) throws Exception {
        String classifierName = "haarcascade_hand.xml";


        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);


        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad("haarcascade_hand.xml"));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }
        CanvasFrame frame = new CanvasFrame("Hand Detection");

        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        
        while(frame!=null){
        grabber.start();
        IplImage grabbedImage = grabber.grab();

        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        CvMemStorage storage = CvMemStorage.create();

       
        CvPoint hatPoints = new CvPoint(3);
    int counter=0;
     //while (frame.isVisible() && (grabbedImage = grabber.grab()) != null && counter==0)
     //{

            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
                1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            int total = faces.total();
            for (int i = 0; i < total; i++)
            {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.BLUE, 1, CV_AA, 0);
                    // To access the elements of a native array, use the `position()` method
                hatPoints.position(0).x(x-w/10);    hatPoints.y(y-h/10);
                hatPoints.position(1).x(x+w*11/10); hatPoints.y(y-h/10);
                hatPoints.position(2).x(x+w/2);     hatPoints.y(y-h/2);
                
                cvFillConvexPoly(grabbedImage, hatPoints.position(0), 3, CvScalar.GREEN, CV_AA, 0);

            }
           //cvSave("D:\\img1.bmp", grabbedImage);
           cvSaveImage("toko.png", grabbedImage);
            counter=1;
            cvThreshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            frame.showImage(grabbedImage);


            cvClearMemStorage(storage);
        }
        }
        //grabber.stop();
        //frame.dispose();
    //}
}