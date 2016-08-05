/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcam;

import JMyron.JMyron;
import processing.core.PApplet;


public class Scan1 extends PApplet{
  int NUM_SQUARES = 20;

JMyron theMov;
int sampleWidth, sampleHeight;
int numSamplePixels;
int []rt;

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
   rt= theMov.retinaImage();
        theMov.adapt();
        theMov.adaptivity(1);
        int t=theMov.average(155, 470, 60, 20);
        int[] img = theMov.differenceImage(); //get the normal image of the camera
        loadPixels();
       for(int i=0;i<width*height;i++){ //loop through all the pixels
          if (img[i] > 3)
           pixels[i] = img[i]; //draw each pixel to the screen
       }
  
}

public void stop() {
  theMov.stop();
  super.stop();
}
    public static void main(String[] args) {
        PApplet.main(new String[]{webcam.Scan1.class.getName()});
    }
}
