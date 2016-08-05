/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectt;

import JMyron.JMyron;
import processing.core.PApplet;
import processing.core.PImage.*;

public class Scan2 extends PApplet{
JMyron theMov;

@Override
public void setup() {
  size(1280, 720);

  theMov = new JMyron();
  theMov.start(width, height);
  theMov.findGlobs(0);
}

@Override
public void draw() {
  theMov.update();
  int[] currFrame = theMov.image();

  // draw each pixel to the screen
  loadPixels();
  for (int i = 0; i < width*height; i++) {
    float r = red(255-currFrame[i]);
    float g = green(255-currFrame[i]);
    float b = blue(255-currFrame[i]);

    pixels[i] = color(r, g, b);
  }
  updatePixels();
}

@Override
public void stop() {
  theMov.stop();
  super.stop();
}
    public static void main(String[] args) {
        PApplet.main(new String[]{objectt.Scan2.class.getName()});
    }
}
