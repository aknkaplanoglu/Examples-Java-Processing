/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
/**
 *
 * @author Dell
 */
public class CVVideoPlayer {
    public static void main(String[] args) {
        
    
    CvCapture capture=cvCreateFileCapture("gotye.mp4");
    IplImage frame;
    cvNamedWindow("video",CV_WINDOW_AUTOSIZE);
    for(;;)
    {
    frame=cvQueryFrame(capture);
    cvShowImage("video",frame);
    char c=(char) cvWaitKey(30);
    if(c==27) break;
    }
    cvReleaseCapture(capture);
    cvDestroyWindow("video");
    }        
}
