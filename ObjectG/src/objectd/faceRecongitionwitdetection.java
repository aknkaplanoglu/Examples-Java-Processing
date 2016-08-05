/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
//import static com.googlecode.javacv.cpp.opencv_highgui.cvNamedWindow;
//import com.googlecode.javacv.CanvasFrame;
//import com.googlecode.javacv.VideoInputFrameGrabber;

public class faceRecongitionwitdetection {

    public static final String XML_FILE = "F:\\Netbeans Projects\\WebCam\\haarcascade_frontalface_default.xml";
    private static final int SCALE = 1;

    @SuppressWarnings("unused")
    private static boolean isRunning = true;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {

        // CanvasFrame canvas = new CanvasFrame("Camera");
        // canvas.setDefaultCloseOperation(CanvasFrame.EXIT_ON_CLOSE);
        // canvas.addWindowListener(new SetBoolean());

        IplImage image;

        FrameGrabber grabber = new OpenCVFrameGrabber("somebody.mp4");

        if (grabber == null) {
            System.out.println("!!! Failed OpenCVFrameGrabber");
            return;
        }

        try {
            grabber.start();
            IplImage frame = null;
            int i = 1;
            int frame_counter = 1;
            while (true) {
                frame = grabber.grab();
                if (frame == null) {
                    System.out.println("!!! Failed grab");
                    break;
                }
                IplImage grayImg = cvCreateImage(cvGetSize(frame),
                        IPL_DEPTH_8U, 1);
                cvCvtColor(frame, grayImg, CV_BGR2GRAY);
                if ((frame_counter % 500) == 0) {

                    // cvSaveImage(i + "teja.jpg", grayImg);

                    detect(grayImg);

                    cvShowImage("Result", grayImg);

                    cvSaveImage(i + "teja.jpg", grayImg);
                    String xyz = i + "teja.jpg";
                    detector(xyz);

                }

                int key = cvWaitKey(1);
                if (key == 100) {
                    break;
                }

                frame_counter++;
                i++;
            }

        } catch (Exception e) {
            System.out.println("!!! Exception");
        }
    }
    

    public static void detect(IplImage src) {
        // IplImage grayImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
        // cvCvtColor(src, grayImg, CV_BGR2GRAY);

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
                cvLoad(XML_FILE));
        CvMemStorage storage = CvMemStorage.create();
        CvSeq sign = cvHaarDetectObjects(src, cascade, storage, 3, 2,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);

        int total_Faces = sign.total();

        for (int i = 0; i < total_Faces; i++) {
            CvRect r = new CvRect(cvGetSeqElem(sign, i));
            cvRectangle(src, cvPoint(r.x(), r.y()),
                    cvPoint(r.width() + r.x(), r.height() + r.y()),
                    CvScalar.GREEN, 3, CV_AA, 0);
        }
    }

    public static void detector(String grab) {
        if (grab.length() < 0) {
            System.out.println("No Input File");
            return;
        }

        System.out.println("Starting OpenCV...");

        // preload the opencv_objdetect module to work around a known bug
        Loader.load(opencv_objdetect.class);

        // load an image
        System.out.println("Loading image from " + grab);
        IplImage origImg = cvLoadImage(grab);

        IplImage grayImg = cvCreateImage(cvGetSize(origImg), IPL_DEPTH_8U, 1);
        cvCvtColor(origImg, grayImg, CV_BGR2GRAY);
        CvMemStorage storage = CvMemStorage.create();
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
                cvLoad(XML_FILE));
        System.out.println("Detecting faces...");
        CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage, 3, 2,
                CV_HAAR_DO_CANNY_PRUNING);
        cvClearMemStorage(storage);

        // iterate over the faces and draw yellow rectangles around them
        int total = faces.total();
        System.out.println("Found " + total + " face(s)");
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));

            cvSetImageROI(grayImg, r);
            cvRectangle(grayImg, cvPoint(r.x() * SCALE, r.y() * SCALE), // undo
                                                                        // the
                                                                        // scaling
                    cvPoint((r.x() + r.width()) * SCALE, (r.y() + r.height())

                    * SCALE), CvScalar.BLUE, 6, CV_AA, 0);

            if (total > 0) {
                // int j=1;
                System.out.println("Saving marked-faces version of " + grab
                        + " in " + grab);
                cvSaveImage(grab, grayImg);

            }
        }
    }
}
