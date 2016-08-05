/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.image.BufferedImage;

public class MotionDetector {
    public static void main(String[] args) throws Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        IplImage frame = grabber.grab();
        IplImage image = null;
        IplImage prevImage = null;
        IplImage diff = null;

        CanvasFrame canvasFrame = new CanvasFrame("Some Title");
        canvasFrame.setCanvasSize(frame.width(), frame.height());
        
        BufferedImage bi=frame.getBufferedImage();
        BufferedImage ro=bi.getSubimage(75, 140, 60, 20);
        CvMemStorage storage = CvMemStorage.create();

        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
            if (image == null) {
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            } else {
                prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                prevImage = image;
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            }

            if (diff == null) {
                diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            }

            if (prevImage != null) {
                // perform ABS difference
                cvAbsDiff(image, prevImage, diff);
                // do some threshold for wipe away useless details
                cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);

                canvasFrame.showImage(diff);
               
                // recognize contours
                CvSeq contour = new CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

                while (contour != null && !contour.isNull()) {
                  
                    contour = contour.h_next();
                }
            }
        }
        grabber.stop();
        canvasFrame.dispose();
    }
}


