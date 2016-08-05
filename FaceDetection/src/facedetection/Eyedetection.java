/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.awt.AWTException;
import java.io.IOException;

public class Eyedetection {

        static opencv_core.IplImage img;
	static opencv_core.IplImage frame;
	static opencv_core.CvMemStorage storage;
	       
    
    public static final String XMLEYE_FILE = "F:\\Netbeans Projects\\FaceDetection\\haarcascade_eye.xml";
    private static final int SCALE = 2;

    @SuppressWarnings("unused")
    private static boolean isRunning = true;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception, IOException, AWTException {

            try{
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
	
            grabber.start();                 
                       
                frame = grabber.grab();
                final CanvasFrame canvas = new CanvasFrame("Tracking Feed");
                CvMemStorage storage = CvMemStorage.create();
                
                while(frame!=null){   
                    
                    IplImage grayImg = cvCreateImage(cvGetSize(frame),IPL_DEPTH_8U, 1);
                    
                    
                    cvFlip(img, img, 1);
                    cvCvtColor(frame, grayImg, CV_BGR2GRAY );
                    canvas.showImage(grayImg);
                    
                    System.out.println("hello");
                    
                    frame=grabber.grab();
               
                    CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XMLEYE_FILE));
                    
                    
                cvSaveImage("teja.jpg", grayImg);
               String xyz =  "teja.jpg";
               

        System.out.println("Starting OpenCV...");

        // preload the opencv_objdetect module to work around a known bug
        Loader.load(opencv_objdetect.class);

        // load an image
        System.out.println("Loading image from " + xyz);
        IplImage origImg = cvLoadImage(xyz);
        cvCvtColor(origImg, grayImg, CV_BGR2GRAY);
        System.out.println("Detecting faces...");
        CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage, 3, 2,CV_HAAR_DO_CANNY_PRUNING);
        cvClearMemStorage(storage);

        // iterate over the faces and draw yellow rectangles around them
        int total = faces.total();
   
        
        System.out.println("Found " + total + " face(s)");
        
        for (int i = 0; i < total; i++) {
            
            CvRect r =new CvRect(cvGetSeqElem(faces, i));
            //mark black rectangle over the face
            cvRectangle(origImg, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.BLACK, 2, CV_AA, 0);
            canvas.showImage(origImg);//show mark
        
            if(total>0){
            //imageROI
            cvSetImageROI(grayImg, r);
            //cut face rectangle from grayImg
            cvRectangle(grayImg, cvPoint(r.x() * SCALE, r.y() * SCALE),cvPoint((r.x() + r.width()) * SCALE, (r.y() + r.height())* SCALE), CvScalar.BLUE, 6, CV_AA, 0);
            System.out.println("Saving marked-faces version of " + xyz + " in " + xyz);
            //save face image
            cvSaveImage("toko.png", grayImg);
            }
          }
    }                    
    }catch(Exception e) {
            System.out.println("!!! Exception");
        }
    }  
}

