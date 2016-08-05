/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectd;

/**
 *
 * @author Gihan Herath
 */
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.awt.AWTException;
import java.io.IOException;


public class LiveTrack {
    static opencv_core.IplImage img;
    static opencv_core.CvMemStorage storage;
    public static final String XML_FILE = "haarcascade_frontalface_default.xml";
 
    
    public static void main(String[] args) throws Exception, IOException, AWTException {
        Runnable rr=new Runnable(){
            

            @Override
            public void run() {
                try {
                CanvasFrame frame = new CanvasFrame("Face Detection");
                FrameGrabber grabber = new OpenCVFrameGrabber(0);
                int count=0;
                while (frame!=null) {
                    grabber.start();
                    IplImage grabbedImage = grabber.grab();
                    int width  = grabbedImage.width();
                    int height = grabbedImage.height();
                    System.out.println(width);
                    System.out.println(height);
                    IplImage grayImg    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
                    CvMemStorage storage1 = CvMemStorage.create();
                    System.out.println("hello");
                    CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
                    cvCvtColor(grabbedImage, grayImg, CV_BGR2GRAY);
                    
                    System.out.println("Detecting faces...");
                    CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage1, 3, 2, CV_HAAR_DO_CANNY_PRUNING);
                    cvClearMemStorage(storage1);
                    int total = faces.total();
                    
                    for (int i = 0; i < total; i++) {
                        
                        CvRect r =new CvRect(cvGetSeqElem(faces, i));
                        //mark black rectangle over the face
                        cvRectangle(grabbedImage, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.BLACK, 2, CV_AA, 0);
                        //cvLine(grabbedImage,  cvPoint(0, 0), cvPoint(r.x() + r.width(), r.y() + r.height()),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
                        //cvLine(grabbedImage,  cvPoint(640, 0), cvPoint(r.x() , r.y()+r.height() ),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
                        
                        
                        if(total>0){
                            frame.showImage(grabbedImage);//show mark
                        }
                        
                          count++;
                        
                            
                        
                        
                       
                    }
                  
                     cvSaveImage("cctv/today/image"+count+".jpg", grabbedImage);   
                    
                      
                    
                }
                
            }catch(Exception e) {
                System.out.println("!!! Exception");
            }
            }
            
        };
    Thread tt=new Thread(rr);
    tt.start();
    
        
    }
    /*public void convert() {
        ByteBuffer bb = ByteBuffer.wrap(getBytesFromFile(filename));

H264Decoder decoder = new H264Decoder();
Picture out = Picture.create(1920, 1088, ColorSpace.YUV420); // Allocate output frame of max size
Picture real = decoder.decodeFrame(bb, out.getData());
BufferedImage bi = JCodecUtil.toBufferedImage(real); // If you prefere AWT image
        
    }*/
}

