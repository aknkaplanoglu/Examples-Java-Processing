/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import ddf.minim.*;

import processing.core.PApplet;
public class Scrubbing extends PApplet {


Minim minim;
AudioPlayer song;
//Play play;
//Rewind rewind;
//Forward ffwd;

@Override
public void setup()
{
  size(512, 200, P3D);
  minim = new Minim(this);
  // load a file from the data folder, use a sample buffer of 1024 samples
  song = minim.loadFile("got.mp3", 512);
  // buttons for control
 // play = new Play(width/2 - 50, 130, 20, 10);
  //rewind = new Rewind(width/2, 130, 20, 10);
  //ffwd = new Forward(width/2 + 50, 130, 20, 10);
}
@Override
public void keyReleased()
{
  if ( key == 'p' ) 
  {
    // to indicate that you want to start or stop capturing audio data, you must call
    // beginRecord() and endRecord() on the AudioRecorder object. You can start and stop
    // as many times as you like, the audio data will be appended to the end of the buffer 
    // (in the case of buffered recording) or to the end of the file (in the case of streamed recording). 
    if ( song.isPlaying() ) 
    {
      song.pause();
    }
    else 
    {
      song.play();
    }
  }
  if ( key == 's' )
  {
    // we've filled the file out buffer, 
    // now write it to the file we specified in createRecorder
    // in the case of buffered recording, if the buffer is large, 
    // this will appear to freeze the sketch for sometime
    // in the case of streamed recording, 
    // it will not freeze as the data is already in the file and all that is being done
    // is closing the file.
    // the method returns the recorded audio as an AudioRecording, 
    // see the example  AudioRecorder >> RecordAndPlayback for more about that
    song.rewind();
    fill(255,0,53);
    text("Done saving.",270,190);
  }
}
@Override
public void draw()
{
  background(0);
  // draw the wave form
  // this wav is MONO, so we only need the left channel, 
  // though we could have used the right channel and gotten the same values
  stroke(255);
  for (int i = 0; i < song.bufferSize() - 1;  i++)
  {
      stroke(0,153,255);
    line(i, 50 - song.left.get(i)*50, i+1, 50 - song.left.get(i+1)*10);
  }
  // draw the position in the song
  // the position is in milliseconds,
  // to get a meaningful graphic, we need to map the value to the range [0, width]
  float x = map(song.position(), 0, song.length(), 0, width);
  stroke(255, 255, 255);
  line(x, 50 - 20, x, 50 + 20);
  // do the controls
  //play.update();
  //play.draw();
  //rewind.update();
  //rewind.draw();
  //ffwd.update(); 
  //ffwd.draw();
}
/*
public void mousePressed()
{
  play.mousePressed();
  rewind.mousePressed();
  ffwd.mousePressed();
}

public void mouseReleased()
{
  play.mouseReleased();
  rewind.mouseReleased();
  ffwd.mouseReleased();
}*/
    public static void main(String[] args) {
         PApplet.main(new String[]{objectd.Scrubbing.class.getName()});
         
    }
}
