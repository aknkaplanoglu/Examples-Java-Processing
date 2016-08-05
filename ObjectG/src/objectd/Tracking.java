/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectd;

import static objectd.LiveTrack.XML_FILE;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gihan Herath
 */
public class Tracking extends HttpServlet {
    static opencv_core.IplImage img;
    static opencv_core.CvMemStorage storage;
    public static final String XML_FILE = "haarcascade_frontalface_default.xml";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" href=\"css/track.css\" />");
            out.println("<meta http-equiv=\"refresh\" content=\"0.3;\" />");
            out.println("<title>Servlet Tracking</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"wrapper\">+"
                    + "<div class=\"header\"></div>"+
                    "<div class=\"maincontent\">"+
                    "<center>"+
                        "<img src=\"images/empboost.jpg\"></img>"+
                        "<br></br><br></br>"+
                            "<div >"+
                                "<img src=\"images/teja.jpg\" width=\"640\" height=\"480\" alt=\"teja\"/></img>"+
                            "</div> "+
                    "</center>"+
                    "</div><hr></hr>"+
                    "<div class=\"footer\">" +
                        "2012-2014 Coordinate21 Inc." +
                    "</div>" +
                    "</div>");
            
            out.println("</body>");
            out.println("</html>");
            startCam();
        }
  
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
       
        
    
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    public void startCam() {
        //cam
            Runnable rr=() -> {
            try {
                CanvasFrame frame = new CanvasFrame("Face Detection");
                FrameGrabber grabber = new OpenCVFrameGrabber(0);
                while (frame!=null) {
                    grabber.start();
                    opencv_core.IplImage grabbedImage = grabber.grab();
                    int width  = grabbedImage.width();
                    int height = grabbedImage.height();
                    System.out.println(width);
                    System.out.println(height);
                    opencv_core.IplImage grayImg    = opencv_core.IplImage.create(width, height, IPL_DEPTH_8U, 1);
                    opencv_core.CvMemStorage storage1 = opencv_core.CvMemStorage.create();
                    System.out.println("hello");
                    opencv_objdetect.CvHaarClassifierCascade cascade = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(XML_FILE));
                    cvCvtColor(grabbedImage, grayImg, CV_BGR2GRAY);
                    System.out.println("Detecting faces...");
                    opencv_core.CvSeq faces = cvHaarDetectObjects(grayImg, cascade, storage1, 3, 2, CV_HAAR_DO_CANNY_PRUNING);
                    cvClearMemStorage(storage1);
                    int total = faces.total();
                    for (int i = 0; i < total; i++) {
                        
                        opencv_core.CvRect r =new opencv_core.CvRect(cvGetSeqElem(faces, i));
                        //mark black rectangle over the face
                        cvRectangle(grabbedImage, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), opencv_core.CvScalar.BLACK, 2, CV_AA, 0);
                        cvLine(grabbedImage,  cvPoint(0, 0), cvPoint(r.x() + r.width(), r.y() + r.height()),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
                        cvLine(grabbedImage,  cvPoint(640, 0), cvPoint(r.x() , r.y()+r.height() ),opencv_core.CvScalar.YELLOW,2, CV_AA, 0);
                        
                        
                        if(total>0){
                            //frame.showImage(grabbedImage);//show mark
                        }
                    }         cvSaveImage("web/images/teja.jpg", grabbedImage);
                    //IplImage image = cvLoadImage("C:\\img.jpg");
                }
            }catch(FrameGrabber.Exception e) {
                System.out.println("!!! Exception");
            }
        };
    Thread tt=new Thread(rr);
    tt.start();
    }
}
