/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// ScanPanel.java
// Andrew Davison, July 2013, ad@fivedots.coe.psu.ac.th

/* This panel repeatedly snaps a picture and draw it onto
   the panel.  The panel processes the "Scan" and "Extract Grid" buttons from
   the top-level JFrame by calls to scan() and extractGrid() which pass most of
   the work to GridVisualizer.

   scan() retrieves the outline of the Sudoku grid, which is drawn as a 
   yellow polygon on top of the image. Only if an outline has been found
   will extractGrid() continue the processing.
*/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.videoInputLib.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.avutil.*;   // for grabber/recorder constants


public class ScanPanel extends JPanel implements Runnable
{
  /* dimensions of each image; the panel is the same size as the image */
  private static final int WIDTH = 640;  
  private static final int HEIGHT = 480;

  private static final int DELAY = 500;  // time (ms) between redraws of the panel

  private static final int CAMERA_ID = 0;


  private IplImage snapIm = null;
  private volatile boolean isRunning;
  
  private GridVisualizer gridVis;   // image processing class

  private Polygon gridPoly;     // holds the current sudoku outline polygon
  private boolean foundOutline = false;   // has an poutline been found?



  public ScanPanel()
  {
    setBackground(Color.white);

    gridPoly = new Polygon(); 
    gridVis = new GridVisualizer();

    new Thread(this).start();   // start updating the panel's image
  } // end of ScanPanel()



  public Dimension getPreferredSize()
  // make the panel wide enough for an image
  {   return new Dimension(WIDTH, HEIGHT); }



  public void run()
  /* display the current webcam image every DELAY ms.
     Sudoku processing is triggered by GUI buttons, and so processed
     in the separate Java GUI thread.
  */
  {
    FrameGrabber grabber = initGrabber(CAMERA_ID);
    if (grabber == null)
      return;

    long duration;
    isRunning = true;

    while (isRunning) {
	  long startTime = System.currentTimeMillis();

      snapIm = picGrab(grabber, CAMERA_ID); 
      repaint();

      duration = System.currentTimeMillis() - startTime;

      if (duration < DELAY) {
        try {
          Thread.sleep(DELAY-duration);  // wait until DELAY time has passed
        } 
        catch (Exception ex) {}
      }
    }
    closeGrabber(grabber, CAMERA_ID);
    gridVis.close();
  }  // end of run()



  private FrameGrabber initGrabber(int ID)
  {
    FrameGrabber grabber = null;
    System.out.println("Initializing grabber for " + videoInput.getDeviceName(ID) + " ...");
    try {
      grabber = FrameGrabber.createDefault(ID);
      grabber.setFormat("dshow");       // using DirectShow
      grabber.setImageWidth(WIDTH);     // default is too small: 320x240
      grabber.setImageHeight(HEIGHT);
      grabber.start();
    }
    catch(Exception e) 
    {  System.out.println("Could not start grabber");  
       System.out.println(e);
       System.exit(1);
    }
    return grabber;
  }  // end of initGrabber()



  private IplImage picGrab(FrameGrabber grabber, int ID)
  {
    IplImage im = null;
    try {
      im = grabber.grab();  // take a snap
    }
    catch(Exception e) 
    {  System.out.println("Problem grabbing image for camera " + ID);  }
    return im;
  }  // end of picGrab()



  private void closeGrabber(FrameGrabber grabber, int ID)
  {
    try {
      grabber.stop();
      grabber.release();
    }
    catch(Exception e) 
    {  System.out.println("Problem stopping grabbing for camera " + ID);  }
  }  // end of closeGrabber()




  public void paintComponent(Graphics g)
  // Draw the current image, and the detected Sudoku grid outline 
  { 
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    if (snapIm == null)
      g2.drawString("Connecting to Webcam. Please wait...", 40, WIDTH/2);
    else
      g2.drawImage(snapIm.getBufferedImage(), 0, 0, this);

    if (foundOutline) {
      g2.setColor(Color.YELLOW);
      g2.setStroke(new BasicStroke(6));   // thick yellow lines
      synchronized(gridPoly) {
        g2.drawPolygon(gridPoly);
      }
    }
  } // end of paintComponent()



  public void closeDown()
  {  isRunning = false;   } 



  // ------------------------- Sudoku grid processing ----------------------------


  public void scan()
  // look for a Sudoku grid outline in the current image
  {  
    if (snapIm == null)
      return;

    foundOutline = false;
    Point[] pts = gridVis.findOutline(snapIm);
    if (pts != null) {    // update polygon
      synchronized(gridPoly) {
        gridPoly.reset();
          for (int i = 0; i < pts.length; i++)
            gridPoly.addPoint(pts[i].x, pts[i].y);
      }
      foundOutline = true;
    }
  }  // end of scan()




  public boolean extractGrid(String fnm)
  /* If there's an outline then try to extract a sudoku grid from it,
     saving the resulting image in fnm */
  {  
    if (foundOutline)
      return gridVis.extractGrid(fnm);
    else
      return false;
  }  // end of extractGrid()


} // end of ScanPanel class

