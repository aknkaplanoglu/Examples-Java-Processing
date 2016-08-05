/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectf;

import JMyron.JMyron;
import processing.core.PApplet;

/**
 *
 * @author Dell
 */
public class Globarray extends PApplet{
   JMyron theMov;
int[] currFrame;
int[] prevFrame;
int tolerance;
boolean revealing;

public void setup() {
  size(1280, 720);

  theMov = new JMyron();
  theMov.start(width, height);
  theMov.findGlobs(0);

  // initialize the pixel arrays to avoid a NullPointerException
  loadPixels();
  currFrame = prevFrame = pixels;

  tolerance = 15;  
  revealing = true;
}

public void draw() {
  // erase the previous image
  background(255);

  theMov.update();
  // save the last frame before updating it
  prevFrame = currFrame;
  currFrame = theMov.image();

  // draw each pixel to the screen only if its change factor is 
  // higher than the tolerance value
  loadPixels();
  for (int i=0; i < width*height; i++) {
    if (comparePixels(i)) {
      pixels[i] = currFrame[i];
    }
  }
  updatePixels();
}

boolean comparePixels(int index) {
  if (Math.abs(red(currFrame[index])-red(prevFrame[index])) < tolerance) 
    if (Math.abs(green(currFrame[index])-green(prevFrame[index])) < tolerance)
      if (Math.abs(blue(currFrame[index])-blue(prevFrame[index])) < tolerance)
        return !revealing;

  return revealing;
}

public void keyReleased() {
  if (key == '.' || key == '>') {
    // increase tolerance
    tolerance += 2;
  } else if (key == ',' || key == '<') {
    // decrease tolerance
    tolerance -= 2;

  // toggle the revealing mode
  } else if (key == ' ') {
    revealing = !revealing;
  }
}

public void stop() {
  theMov.stop();
  super.stop();
} 
    public static void main(String[] args) {
        PApplet.main(new String[]{objectf.Globarray.class.getName()});
    }
}
