/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import JMyron.JMyron;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import processing.core.PApplet;


public class Scan1 extends PApplet{
  int NUM_SQUARES = 20;

JMyron theMov;
int sampleWidth, sampleHeight;
int numSamplePixels;
int[] imageIntArray;
int width=1280;
int height=720;
BufferedImage bu = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
 
  @Override
  public void setup() {
 
  size(1280, 720);

  theMov = new JMyron();
  theMov.start(width, height);
  theMov.findGlobs(0);

  sampleWidth = width/NUM_SQUARES;
  sampleHeight = height/NUM_SQUARES;
  numSamplePixels = sampleWidth*sampleHeight;
  
   
}

public void draw() {
  theMov.update();
  imageIntArray= theMov.image();
  bu.setRGB(0, 0, width, height, imageIntArray, 0, width);
  int[] currFrame = theMov.image();

  // go through all the cells
  for (int y=0; y < height; y += sampleHeight) {
    for (int x=0; x < width; x += sampleWidth) {
      // reset the averages
      float r = 0;
      float g = 0;
      float b = 0;

      // go through all the Pixels in the current cell
      for (int yIndex = 0; yIndex < sampleHeight; yIndex++) {
        for (int xIndex = 0; xIndex < sampleWidth; xIndex++) {
          // add each pixel in the current cell's RGB values to the total
          // we have to multiply the y values by the width since we are 
          // using a one-dimensional array
          r += red(currFrame[x+y*width+xIndex+yIndex*width]);
          g += green(currFrame[x+y*width+xIndex+yIndex*width]);
          b += blue(currFrame[x+y*width+xIndex+yIndex*width]); 
        }
      }

      r /= numSamplePixels;
      g /= numSamplePixels;
      b /= numSamplePixels;

      fill(r, g, b);
      rect(x, y, sampleWidth, sampleHeight);
      
    }
  }
  
   File file = new File("gona"+".png");  
                        try{
                           
                            ImageIO.write(bu.getSubimage(300, 0, 700, 650),"png",file);
                            
                            
                           // imageconvert();
                            //lines();
                        } catch (Exception e) {
                            
                        }
}

public void stop() {
  theMov.stop();
  
  super.stop();
}
    public static void main(String[] args) {
        PApplet.main(new String[]{objectd.Scan1.class.getName()});
    }
}
