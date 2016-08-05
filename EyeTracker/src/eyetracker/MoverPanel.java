/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker;


// MoverPanel.java
// Andrew Davison, March 2013, ad@fivedots.coe.psu.ac.th

/* A panel which displays a crosshairs image
   (the 'target'). Calls to setTarget() are used to move the target
   image inside the panel.

   The size of the panel are supplied as constructor arguments.
*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;



public class MoverPanel extends JPanel
{
  private static final String TARGET_FNM = "crosshairs.png";

  private int xCenter, yCenter;    // location of the center of the target image

  private BufferedImage targetIm;
  private int pWidth, pHeight, imWidth, imHeight;
      // dimensions of the panel (p) and of the target image (im)



  public MoverPanel(int w, int h)
  {
    pWidth = w; pHeight = h;
    setPreferredSize(new Dimension(pWidth, pHeight));
    // System.out.println("Panel (w,h) : (" + pWidth + ", " + pHeight + ")");

    // initialize target images
    targetIm = loadImage(TARGET_FNM);
    if (targetIm == null)
      System.exit(1);
    imWidth = targetIm.getWidth();
    imHeight = targetIm.getHeight();

    xCenter = pWidth/2;    // initially place the target in the center of the panel
    yCenter = pHeight/2;
  } // end of MoverPanel()




  private BufferedImage loadImage(String fnm)
  // load the image stored in fnm
  {
    BufferedImage image = null;
    try {
      image = ImageIO.read(new File(fnm));
      // System.out.println("Loaded " + fnm);
    }
    catch (IOException e) 
    {  System.out.println("Unable to load " + fnm);  }
    return image;
  }  // end of loadImage()



  public void paintComponent(Graphics g)
  // draw the center of the target at its current position
  { 
    super.paintComponent(g);
    g.drawImage(targetIm, xCenter-imWidth/2, yCenter-imHeight/2, null); 
  } // end of paintComponent()




  public void setTarget(double x, double y)
  /* update the target's position on the panel
     x and y are percentages, which because of scaling may be less
     than 0 or greater than 1 (100%).
  */
  {  
    xCenter = (int) Math.round(x*pWidth);    // convert to panel coords
    yCenter = (int) Math.round(y*pHeight);

    // keep the target visible on-screen
    if (xCenter < 0)
      xCenter = 0;
    else if (xCenter >= pWidth)
      xCenter = pWidth-1;

    // reverse xCenter so left-of-center <--> right-of-center
    xCenter = pWidth - xCenter;

    if (yCenter < 0)
      yCenter = 0;
    else if (yCenter >= pHeight)
      yCenter = pHeight-1;

    // System.out.println("(xCenter,yCenter): (" + xCenter + " - " + yCenter +")");

    repaint();
  }  // end of setTarget()  

} // end of MoverPanel class

