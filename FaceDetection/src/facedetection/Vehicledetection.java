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
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class Vehicledetection {
    static opencv_core.IplImage img;
	static opencv_core.IplImage frame;
	static opencv_core.CvMemStorage storage;
//public Buffer buf = null;
public static void main(String[] args) throws Exception {
        
  Runnable r1=new Runnable(){
      
  public void run(){  
        
        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

  try{
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad("cars3.xml"));
       
        CanvasFrame frame = new CanvasFrame("Hand Detection");
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        
        grabber.setImageHeight(240);
        grabber.setImageWidth(320);
        grabber.start();
        while(grabber!=null){
      
        
         IplImage grabbedImage = grabber.grab();
         
       int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        CvMemStorage storage = CvMemStorage.create();
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            int total = faces.total();
            for (int i = 0; i < total; i++)
            {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.GREEN, 1, CV_AA, 0);
            }
            cvThreshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);
            frame.showImage(grabbedImage);
            cvClearMemStorage(storage);
       }
        }catch(Exception e){
            System.out.println(e.getMessage());
       } 
       
        }};
  Thread t=new Thread(r1);
  t.start();
       
}
}
    
    