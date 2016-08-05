/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
/**
 *
 * @author Dell
 */
public class JavacvBasic {
    public static void main(String[] args) {
        IplImage img=cvLoadImage("ggg.jpg");
        
        IplImage hsv=cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,3);
        IplImage gray=cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);
        
         cvCvtColor(img,hsv,CV_BGR2HSV);
         cvCvtColor(img, gray,CV_BGR2GRAY);
         cvShowImage("original",img);
         cvShowImage("hsv",hsv);
         cvShowImage("gray",gray);
         cvWaitKey();
         //cvReleaseImage(img);
         //cvReleaseImage(hsv);
         //cvReleaseImage(gray);
    }
}
