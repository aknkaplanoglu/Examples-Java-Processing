/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.awt.AWTException;
import java.io.IOException;

public class Facedetection {

        static opencv_core.IplImage img;
	//static opencv_core.IplImage frame;
	static opencv_core.CvMemStorage storage;
	       
    public static final String XML_FILE = "haarcascade_frontalface_default.xml";
    
    private static final int SCALE = 2;

    @SuppressWarnings("unused")
    private static boolean isRunning = true;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception, IOException, AWTException {

            try{
           CanvasFrame frame = new CanvasFrame("Hand Detection");

        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        
        
        while(frame!=null){
        //grabber.setImageHeight(240);
        //grabber.setImageWidth(320);
        grabber.start();
        IplImage grabbedImage = grabber.grab();

        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImg    = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        CvMemStorage storage = CvMemStorage.create();
                   // cvFlip(img, img, 1);
                   // cvCvtColor(frame, grayImg, CV_RGB2GRAY );
                    //canvas.showImage(grayImg);
                    
                    System.out.println("hello");
                    
                    //frame=grabber.grab();
               
                    CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
                    
                    
                cvSaveImage("teja.jpg", grayImg);
               String xyz =  "teja.jpg";
               

        //System.out.println("Starting OpenCV...");

        // preload the opencv_objdetect module to work around a known bug
        //Loader.load(opencv_objdetect.class);

        // load an image
        //System.out.println("Loading image from " + xyz);
        //IplImage origImg = cvLoadImage(xyz);
        cvCvtColor(grabbedImage, grayImg, CV_BGR2GRAY);
        System.out.println("Detecting faces...");
  
        CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage, 3, 2,CV_HAAR_DO_CANNY_PRUNING);
        cvClearMemStorage(storage);

        // iterate over the faces and draw yellow rectangles around them
        int total = faces.total();
   
        
        System.out.println("Found " + total + " face(s)");
        
        for (int i = 0; i < total; i++) {
            
            CvRect r =new CvRect(cvGetSeqElem(faces, i));
            //mark black rectangle over the face
            cvRectangle(grabbedImage, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.BLACK, 2, CV_AA, 0);
            cvLine(grabbedImage,  cvPoint(0, 0), cvPoint(r.x() + r.width(), r.y() + r.height()),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
            cvLine(grabbedImage,  cvPoint(640, 0), cvPoint(r.x() , r.y()+r.height() ),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
        
            if(total>0){
            //imageROI
            //cvSetImageROI(grayImg, r);
            //cut face rectangle from grayImg
            //cvarr cvpoint cvpoint cvscalr int int int
            
            //cvRectangle(grayImg, cvPoint(r.x() * SCALE, r.y() * SCALE),cvPoint((r.x() + r.width()) * SCALE, (r.y() + r.height())* SCALE), CvScalar.BLUE, 6, CV_AA, 0);
            System.out.println("Saving marked-faces version of " + xyz + " in " + xyz);
            //save face image
            frame.showImage(grabbedImage);//show mark
            //cvSaveImage("toko.png", grayImg);
            }
          }
    }                    
    }catch(Exception e) {
            System.out.println("!!! Exception");
        }
    }  
}

