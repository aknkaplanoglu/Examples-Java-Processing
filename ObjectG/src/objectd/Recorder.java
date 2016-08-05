/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import processing.core.PApplet;
import ddf.minim.*;


public class Recorder extends PApplet{
Minim minim;
AudioInput in;
AudioRecorder recorder;

@Override
public void setup()
{
  size(512, 200, P3D);
  
  minim = new Minim(this);

  in = minim.getLineIn();
  // create a recorder that will record from the input to the filename specified, using buffered recording
  // buffered recording means that all captured audio will be written into a sample buffer
  // then when save() is called, the contents of the buffer will actually be written to a file
  // the file will be located in the sketch's root folder.
  recorder = minim.createRecorder(in, "myvoice.wav", true);
  
 

}

public void draw()
{
  background(0); 
  stroke(0,153,255);
  fill(255);
  textFont(createFont("Arial", 12));
  text("To Start Voice Recognition Press R", 5, 100);
  text("To Stop Voice Recognition Press R", 5, 120);
  text("To Save Voice Press S", 5, 140);
  // draw the waveforms
  // the values returned by left.get() and right.get() will be between -1 and 1,
  // so we need to scale them up to see the waveform
  for(int i = 0; i < in.bufferSize() - 1; i++)
  {
    line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
    //line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
  }
   textFont(createFont("Arial", 42));
  if ( recorder.isRecording() )
  {
      fill(0,153,255);
    text("Currently recording...", 5, 190);
  }
  else
  {
      fill(0,153,255);
    text("Not recording.", 5, 190);
  }
}

@Override
public void keyReleased()
{
  if ( key == 'r' ) 
  {
    // to indicate that you want to start or stop capturing audio data, you must call
    // beginRecord() and endRecord() on the AudioRecorder object. You can start and stop
    // as many times as you like, the audio data will be appended to the end of the buffer 
    // (in the case of buffered recording) or to the end of the file (in the case of streamed recording). 
    if ( recorder.isRecording() ) 
    {
      recorder.endRecord();
    }
    else 
    {
      recorder.beginRecord();
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
    recorder.save();
    fill(255,0,53);
    text("Done saving.",270,190);
  }
}
    public static void main(String[] args) {
        PApplet.main(new String[]{objectd.Recorder.class.getName()});
    }
}
