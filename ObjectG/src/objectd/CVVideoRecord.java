/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameRecorder;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
public class CVVideoRecord {
    public static void main(String[] args) throws FrameRecorder.Exception {
        CvCapture capture1=cvCreateCameraCapture(CV_CAP_ANY);
        cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_WIDTH,320);
        cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_HEIGHT,240);
        
        cvNamedWindow("live",0);
        
        FrameRecorder recorder1=new OpenCVFrameRecorder("Recorder.avi",320,240);
        recorder1.setVideoCodec(CV_FOURCC('M','J','P','G'));
        recorder1.setFrameRate(15);
        recorder1.setPixelFormat(1);
        recorder1.start();
        
        IplImage img1;
        for(;;)
        {
        img1=cvQueryFrame(capture1);
        if(img1==null) break;
        cvShowImage("live",img1);
        recorder1.record(img1);
        
        char c=(char)cvWaitKey(15);
        if(c=='q')break;
        }
        recorder1.stop();
        cvDestroyWindow("live");
    }
}
