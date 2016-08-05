/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker;


// eye.java
// Andrew Davison, July 2013, ad@fivedots.psu.ac.th
// Chonmaphat Roonnapak, February 2013, cocasmile.chon@gmail.com

/* Show a sequence of images snapped from a webcam in a picture panel (EyePanel).
   The eye is highlighted with a yellow rectangle, which is updated as the eye
   moves. The eye's pupil/iris is highlighed with a red rectangle.

   As the pupil/iris moves, a target image in a separate window moves as well.

   This webcam window is positioned at the bottom right of the screen to
   give more room to the target window.

   Usage:
      > java eye
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacpp.Loader;


public class eye extends JFrame
{
  // dimensions of target window
  private static final int TARGET_WIDTH = 800;  
  private static final int TARGET_HEIGHT = 650;


  // GUI components
  private EyePanel eyePanel;

  public eye()
  {
    super("Eye Tracker");
    Container c = getContentPane();
    c.setLayout( new BorderLayout() );  

    // Preload the opencv_objdetect module to work around a known bug.
    Loader.load(opencv_objdetect.class);

    TargetMover tm = new TargetMover(TARGET_WIDTH, TARGET_HEIGHT);
        // a window showing a target that is moved by the user's pupil movement
   
    eyePanel = new EyePanel(tm); // the sequence of webcam pics appear here
    c.add(eyePanel, BorderLayout.CENTER);

    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      { eyePanel.closeDown();    // stop snapping pics
        System.exit(0);
      }
    });

    setResizable(false);
    pack();  

    // position at bottom right of screen
    Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = scrDim.width - getWidth();
    int y = scrDim.height - getHeight();
    setLocation(x,y);

    setVisible(true);
  } // end of eye()


  // -------------------------------------------------------

  public static void main( String args[] )
  {  new eye();  }

} // end of eye class

