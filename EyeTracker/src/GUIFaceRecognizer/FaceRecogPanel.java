
// FaceRecogPanel.java
// Andrew Davison, March 2011, ad@fivedots.psu.ac.th

/* This panel repeatedly snaps a picture and draw it onto
   the panel.  A face is highlighted with a yellow rectangle, which is updated 
   as the face moves. A "crosshairs" graphic is also drawn, positioned at the
   center of the rectangle.

   The highlighted part of the image can be recognized.

   Face detection is done using a Haar face classifier in JavaCV. 
   It is executed inside its own thread since the processing can be lengthy,
   and I don't want the image grabbing speed to be affected.

   This is an extension of the FacesPanel class in the Face Tracking example.
   The recognition is done with the FaceRecognition class from the JavaFaces
   example.
*/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.videoInputLib.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.avutil.*;   // for grabber/recorder constants



public class FaceRecogPanel extends JPanel implements Runnable
{
  /* dimensions of each image; the panel is the same size as the image */
  private static final int WIDTH = 640;  
  private static final int HEIGHT = 480;

  private static final int DELAY = 100;  // ms 

  private static final int CAMERA_ID = 0;

  private static final int IM_SCALE = 4;   
  private static final int SMALL_MOVE = 5;
  private static final int DETECT_DELAY = 500;   
                    // time (ms) between each face detection
  private static final int MAX_TASKS = 4;    
                    // max no. of tasks that can be waiting to be executed

  // circle and cross-hairs dimensions (only used if crosshairs image cannot be loaded)
  private static final int CIRCLE_SIZE = 40;  
  private static final int LINES_LEN = 60;  

  // cascade definition to be used for face detection
  private static final String FACE_CASCADE_FNM = "haarcascade_frontalface_alt.xml";
                                                 // "haarcascade_frontalface_alt2.xml";
     /* others are in C:\OpenCV2.2\data\haarcascades\
        and at http://alereimondo.no-ip.org/OpenCV/34
     */

  private static final String CROSSHAIRS_FNM = "crosshairs.png";

  // for recognizing a detected face image
  // private static final String FACE_FNM = "savedFace.png";
  private static final int FACE_WIDTH = 125;
  private static final int FACE_HEIGHT = 150;

  private IplImage snapIm = null;
  private volatile boolean isRunning;
  
  // used for the average ms snap time information
  private int imageCount = 0;
  private long totalTime = 0;
  private Font msgFont;

  // JavaCV variables
  private CvHaarClassifierCascade classifier;
  private CvMemStorage storage;
  private CanvasFrame debugCanvas;
  private IplImage grayIm;

  // used for thread that executes the face detection
  private ExecutorService executor;
  private AtomicInteger numTasks;
       // used to record number of detection tasks
  private long detectStartTime = 0;

  private Rectangle faceRect;     // holds the coordinates of the highlighted face
  private BufferedImage crosshairs;

  private volatile boolean recognizeFace = false;
  private FaceRecognition faceRecog;   // this class comes from the javaFaces example
  private String faceName = null;      // name associated with last recognized face

  private FaceRecognizer top;


  public FaceRecogPanel(FaceRecognizer top)
  {
    this.top = top;
    setBackground(Color.white);
    msgFont = new Font("SansSerif", Font.BOLD, 18);

    // load the crosshairs image (a transparent PNG)
    crosshairs = loadImage(CROSSHAIRS_FNM);

    faceRecog = new FaceRecognition(22);

    executor = Executors.newSingleThreadExecutor();
      /* this executor manages a single thread with an unbounded queue.
         Only one task can be executed at a time, the others wait.
      */
    numTasks = new AtomicInteger(0);    
      // used to limit the size of the executor queue

    initDetector();
    faceRect = new Rectangle(); 

    new Thread(this).start();   // start updating the panel's image
  } // end of FaceRecogPanel()



  private BufferedImage loadImage(String imFnm)
  // return an image
  {
    BufferedImage image = null;
    try {
      image = ImageIO.read( new File(imFnm) );   // read in as an image
       System.out.println("Reading image " + imFnm);
    }
    catch (Exception e) {
      System.out.println("Could not read image from " + imFnm);
    }
    return image;
  }  // end of loadImage()



  private void initDetector()
  {
    // instantiate a classifier cascade for face detection
    classifier = new CvHaarClassifierCascade(cvLoad(FACE_CASCADE_FNM));
    if (classifier.isNull()) {
      System.out.println("\nCould not load the classifier file: " + FACE_CASCADE_FNM);
      System.exit(1);
    }

    storage = CvMemStorage.create();  // create storage used during object detection

    // debugCanvas = new CanvasFrame("Debugging Canvas");
             // useful for showing JavaCV IplImage objects, to check on image processing
  }  // end of initDetector()



  public Dimension getPreferredSize()
  // make the panel wide enough for an image
  {   return new Dimension(WIDTH, HEIGHT); }



  public void run()
  /* display the current webcam image every DELAY ms
     The time statistics gathered here will NOT include the time taken to
     find a face, which are farmed out to a separate thread in trackFace().

     Tracking is only started at least every DETECT_DELAY (1000) ms, and only
     if the number of tasks is < MAX_TASKS (one will be executing, the others
     waiting)
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

      if (((System.currentTimeMillis() - detectStartTime) > DETECT_DELAY) &&
          (numTasks.get() < MAX_TASKS))
        trackFace(snapIm); 
      imageCount++;
      repaint();

      duration = System.currentTimeMillis() - startTime;
      totalTime += duration;
      if (duration < DELAY) {
        try {
          Thread.sleep(DELAY-duration);  // wait until DELAY time has passed
        } 
        catch (Exception ex) {}
      }
    }
    closeGrabber(grabber, CAMERA_ID);
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
  /* Draw the image, the rectangle (and crosshairs) around a detected
     face, and the average ms snap time at the bottom left of the panel. 
     Show the currently recognized face name at the bottom middle.
     This time does NOT include the face detection task.
  */
  { 
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    if (snapIm != null)
      g2.drawImage(snapIm.getBufferedImage(), 0, 0, this);

    drawRect(g2);
    writeStats(g2);
    writeName(g2);
  } // end of paintComponent()



  private void drawRect(Graphics2D g2)
  /* use the face rectangle to draw a yellow rectangle around the face, with 
     crosshairs at its center.
     The drawing of faceRect is in a synchronized block since it may be being
     updated or used for image saving at the same time in other threads.
  */
  {  
    synchronized(faceRect) {
      if (faceRect.width == 0)
        return;

      // draw a thick yellow rectangle
      g2.setColor(Color.YELLOW);
      g2.setStroke(new BasicStroke(6)); 
      g2.drawRect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);

      int xCenter = faceRect.x + faceRect.width/2;
      int yCenter = faceRect.y + faceRect.height/2;
      drawCrosshairs(g2, xCenter, yCenter);
    }
  }  // end of drawRect()



  private void drawCrosshairs(Graphics2D g2, int xCenter, int yCenter)
  // draw crosshairs graphic or a red circle
  {
    if (crosshairs != null)
      g2.drawImage(crosshairs, xCenter - crosshairs.getWidth()/2, 
                               yCenter - crosshairs.getHeight()/2, this);
    else {    
      g2.setColor(Color.RED);
      g2.fillOval(xCenter-10, yCenter-10, 20, 20);
    }
  }  // end of drawCrosshairs()



  private void writeName(Graphics2D g2)
  /* write the currently recognized face name */
  {
    g2.setColor(Color.YELLOW);
    g2.setFont(msgFont);
    if (faceName != null)     // draw at bottom middle
      g2.drawString("Recognized: " + faceName, WIDTH/2, HEIGHT-10);  
  }  // end of writeName()




  private void writeStats(Graphics2D g2)
  /* write statistics in bottom-left corner, or
     "Loading" at start time */
  {
    g2.setColor(Color.BLUE);
    g2.setFont(msgFont);
    if (imageCount > 0) {
      String statsMsg = String.format("Snap Avg. Time:  %.1f ms",
                                        ((double) totalTime / imageCount));
      g2.drawString(statsMsg, 5, HEIGHT-10);  
                        // write statistics in bottom-left corner
    }
    else  // no image yet
      g2.drawString("Loading...", 5, HEIGHT-10);
  }  // end of writeStats()



  public void closeDown()
  {  isRunning = false;   } 



  // ------------------------- face tracking ----------------------------


  private void trackFace(final IplImage img)
  /* Create a separate thread for the time-consuming detection and recognition tasks:
     find a face in the current image, store its coordinates in faceRect, then
     recognize the face, and place the person's name in the top-level name TextField 
  */ 
  {
    grayIm = scaleGray(img);
    numTasks.getAndIncrement();     // increment no. of tasks before entering queue
    executor.execute(new Runnable() {
      public void run()
      { 
        detectStartTime = System.currentTimeMillis();
        CvRect rect = findFace(grayIm);
        if (rect != null) {
          setRectangle(rect);
          if (recognizeFace) {
            recogFace(img);
            recognizeFace = false;
          }
        }
        long detectDuration = System.currentTimeMillis() - detectStartTime;
        System.out.println(" detection/recognition duration: " + detectDuration + "ms");
        numTasks.getAndDecrement();  // decrement no. of tasks since finished
      }
    });
  }  // end of trackFace()



  private IplImage scaleGray(IplImage img)
  /* Scale the image and convert it to grayscale. Scaling makes
     the image smaller and so faster to process, and Haar detection
     requires a grayscale image as input
  */
  {
    // convert to grayscale
    IplImage grayImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
    cvCvtColor(img, grayImg, CV_BGR2GRAY);  

    // scale the grayscale (to speed up face detection)
    IplImage smallImg = IplImage.create(grayImg.width()/IM_SCALE, 
                                        grayImg.height()/IM_SCALE, IPL_DEPTH_8U, 1);
    cvResize(grayImg, smallImg, CV_INTER_LINEAR);

    // equalize the small grayscale
	cvEqualizeHist(smallImg, smallImg);
    return smallImg;
  }  // end of scaleGray()



  private CvRect findFace(IplImage grayIm)
  /* The Haar detector is a JavaCV function, so requires an IplImage object.
     Also, use JavaCV's grayscale equalizer to improve the image.
  */
  {
/*
     // show the greyscale image to check on image processing steps
     debugCanvas.showImage(grayIm);
	 debugCanvas.waitKey(0);
*/
    // System.out.println("Detecting largest face...");   // cvImage
    CvSeq faces = cvHaarDetectObjects(grayIm, classifier, storage, 1.1, 1,  // 3
                              // CV_HAAR_SCALE_IMAGE |
                              CV_HAAR_DO_ROUGH_SEARCH | CV_HAAR_FIND_BIGGEST_OBJECT); 
          // speed things up by searching for only a single, largest face subimage

    int total = faces.total();
    if (total == 0) {
      // System.out.println("No faces found");
      return null;
    }
    else if (total > 1)   // this case should not happen, but included for safety
      System.out.println("Multiple faces detected (" + total + "); using the first");
    // else
    //  System.out.println("Face detected");

    CvRect rect = new CvRect(cvGetSeqElem(faces, 0));

    cvClearMemStorage(storage);
    return rect;
  }  // end of findface()



  private void setRectangle(CvRect r)
  /* Extract the (x, y, width, height) values of the highlighted image from
     the JavaCV rectangle data structure, and store them in a Java rectangle.
     In the process, undo the scaling which was applied to the image before face 
     detection was carried out.
     Report any movement of the new rectangle compared to the previous one.
     The updating of faceRect is in a synchronized block since it may be used 
     for drawing or image saving at the same time in other threads.
  */
  {  
    synchronized(faceRect) {
      int xNew = r.x()*IM_SCALE;
      int yNew = r.y()*IM_SCALE;
      int widthNew = r.width()*IM_SCALE;
      int heightNew = r.height()*IM_SCALE;

      // calculate movement of the new rectangle compared to the previous one
      int xMove = (xNew + widthNew/2) - (faceRect.x + faceRect.width/2);
      int yMove = (yNew + heightNew/2) - (faceRect.y + faceRect.height/2);

      // report movement only if it is 'significant'
      // if ((Math.abs(xMove)> SMALL_MOVE) || (Math.abs(yMove) > SMALL_MOVE))
      //  System.out.println("Movement (x,y): (" + xMove + "," + yMove + ")" );

      faceRect.setRect( xNew, yNew, widthNew, heightNew);
      // System.out.println("Rectangle: " + faceRect);
    }
  }  // end of setRectangle()




  // ---------------- face recognition -------------------------

  public void setRecog()
  {  recognizeFace = true;  }



  private void recogFace(IplImage img)
  /* clip the image using the current face rectangle, then try to recognize it.
     The use of faceRect is in a synchronized block since it may be being
     updated or used for drawing at the same time in other threads.
  */
  {
    BufferedImage clipIm = null;
    synchronized(faceRect) {
      if (faceRect.width == 0) {
        System.out.println("No face selected");
        return;
      }
      clipIm = ImageUtils.clipToRectangle(img.getBufferedImage(), 
                   faceRect.x, faceRect.y, faceRect.width, faceRect.height);
    }
    if (clipIm != null) 
      matchClip(clipIm);
  }  // end of recogFace()




  private void matchClip(BufferedImage clipIm)
  // resize, convert to grayscale, clip to FACE_WIDTH*FACE_HEIGHT, recognize
  {
    long startTime = System.currentTimeMillis();

    System.out.println("Matching clip...");
    BufferedImage faceIm = clipToFace( resizeImage(clipIm) );
    // FileUtils.saveImage(faceIm, FACE_FNM);
    MatchResult result = faceRecog.match(faceIm);
    if (result == null)
      System.out.println("No match found");
    else {
      faceName = result.getName();
      String distStr = String.format("%.4f", result.getMatchDistance());
      System.out.println("  Matches " + result.getMatchFileName() +
                                     "; distance = " + distStr);
      System.out.println("  Matched name: " + faceName);
      top.setRecogName(faceName, distStr);
    }
    System.out.println("Match time: " + (System.currentTimeMillis() - startTime) + " ms");
  }  // end of matchClip()




  private BufferedImage resizeImage(BufferedImage im)
  /* resize so *at least* FACE_WIDTH*FACE_HEIGHT size
     and convert to grayscale */
  {
    double widthScale = FACE_WIDTH / ((double) im.getWidth());
    double heightScale = FACE_HEIGHT / ((double) im.getHeight());
    double scale = (widthScale > heightScale) ? widthScale : heightScale;
    return ImageUtils.toScaledGray(im, scale);
  }  // end of resizeImage()



  private BufferedImage clipToFace(BufferedImage im)
  // clip image to FACE_WIDTH*FACE_HEIGHT size
  // I assume the input image is face size or bigger
  {
    int xOffset = (im.getWidth() - FACE_WIDTH)/2;
    int yOffset = (im.getHeight() - FACE_HEIGHT)/2;
    return ImageUtils.clipToRectangle(im, xOffset, yOffset, FACE_WIDTH, FACE_HEIGHT);
  }  // end of clipToFace()



} // end of FaceRecogPanel class

