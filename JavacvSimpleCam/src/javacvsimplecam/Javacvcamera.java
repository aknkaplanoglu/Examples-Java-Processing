/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacvsimplecam;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import java.awt.AWTException;
import java.awt.Dimension;

/**
 *
 * @author Dell
 */
public class Javacvcamera {
        
	static opencv_core.IplImage frame;
	static opencv_core.CvMemStorage storage;
        static Dimension screenSize, canvasSize;
private static void startTracking() throws AWTException {        
		// 0 = default camera, 1 = next, etc.
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		

		try {
			grabber.start();
			opencv_core.IplImage img = grabber.grab();
			final CanvasFrame canvas = new CanvasFrame("Tracking Feed");
			storage = opencv_core.CvMemStorage.create();

			while (img != null) {
				canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				canvasSize = canvas.getCanvasSize();
				cvFlip(img, img, 1);
                                //cvRectangle r=new cvRectangle();
				canvas.showImage(img);
				img = grabber.grab();
			}
		} catch (Exception e) {
                    
		} 
	}
public static void main(String[] args) throws AWTException {
   startTracking();
}
}
