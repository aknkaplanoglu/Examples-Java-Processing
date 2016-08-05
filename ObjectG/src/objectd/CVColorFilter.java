/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

public class CVColorFilter {
    public static void main(String[] args) {
        IplImage img1,imghsv,imgbin;
        
        img1=cvLoadImage("Color.jpg");
        imghsv=cvCreateImage(cvGetSize(img1),8,3);
        imgbin=cvCreateImage(cvGetSize(img1),8,1);
        
        cvCvtColor(img1,imghsv,CV_BGR2HSV);
        //BLUE
        //CvScalar minc=cvScalar(95,150,75,0),maxc=cvScalar(145,255,255,0);
        
        //GREEN
        //CvScalar minc=cvScalar(40,150,75,0),maxc=cvScalar(80,255,255,0);
        
        //RED
        CvScalar minc=cvScalar(160,150,75,0),maxc=cvScalar(180,255,255,0);
        
        //YELLOW
        //CvScalar minc=cvScalar(20,50,100,0),maxc=cvScalar(40,255,255,0);
        cvInRangeS(imghsv,minc,maxc,imgbin);
        cvShowImage("color",img1);
        cvShowImage("binary",imgbin);
        cvWaitKey();
        cvReleaseImage(img1);
        cvReleaseImage(imghsv);
        cvReleaseImage(imgbin);
    }
}
