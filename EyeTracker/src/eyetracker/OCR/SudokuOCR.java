/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// SudokuOCR.java
// Andrew Davison, January 2013, ad@fivedots.coe.psu.ac.th

/* Process a webcam image looking for a sudoku grid. Display the
   grid numbers inside the application, and allow the user to 
   regenerate the grid or modify individual values before getting Java
   to complete the Sudoku.

   The GUI consists of two main panels and three buttons.
   The left-hand panel (ScanPanel) shows the webcam image and an
   outline of the detected Sudoku grid. The right-hand panel (GridPanel)
   shows a grid of spinner GUI components, each one representing a 
   Sudoku number square. 

   The three buttons are "Scan", "Extract Grid", and "Solve".
   "Scan" makes the application search for a grid outline inside the current webcam
   image. If the user likes the resulting outline, they can press "Extract Grid" 
   to fill in the grid of spinners with numbers extracted from the Sudoku image.
   After further adjustment, "Solve" causes the grid to be completed.

   The processing stages require that the Sudoku image be saved in a file before
   OCR is carried out by the command line gocr tool (http://jocr.sourceforge.net/). 
   The name of the file is SUD_FNM.

   See http://en.wikipedia.org/wiki/Sudoku for more information on Sudoku.

   Usage:
      > java SudokuOCR
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacpp.Loader;


public class SudokuOCR extends JFrame 
                       implements ActionListener
{
  private static final String SUD_FNM = "sudoGrid.pnm";  // sudoku numbers image filename

  // GUI components
  private ScanPanel scanPanel;    // to display the webcam image and Sudoku outline
  private GridPanel gridPanel;    /* to display the grid of spinner components representing
                                     the Sudoku grid */

  private JButton scanBut, extractBut, solveBut;



  public SudokuOCR()
  {
    super("OCR Sudoku");

    Container c = getContentPane();
    c.setLayout( new BorderLayout() );   

    // Preload the opencv_objdetect module to work around a known bug.
    Loader.load(opencv_objdetect.class);

    scanPanel = new ScanPanel(); // the wencam pictures and outline appears here
    c.add( scanPanel, BorderLayout.CENTER);

    gridPanel = new GridPanel();   // the Sudoku grid appears here
    c.add(gridPanel, BorderLayout.EAST);


    // buttons for Sudoku processing
    JPanel p = new JPanel();

    scanBut = new JButton("Scan");
    scanBut.addActionListener(this);
    p.add(scanBut);

    extractBut = new JButton("Extract Grid");
    extractBut.addActionListener(this);
    p.add(extractBut);

    solveBut = new JButton("Solve");
    solveBut.addActionListener(this);
    p.add(solveBut);

    c.add(p, BorderLayout.SOUTH);


    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      { scanPanel.closeDown();    // stop snapping pics
        System.exit(0);
      }
    });

    pack();  
    setResizable(false);
    setLocationRelativeTo(null);
    setVisible(true);
  } // end of SudokuOCR()



  public void actionPerformed(ActionEvent e)
  // deal with three control buttons
  {
    if (e.getSource() == scanBut) {
      gridPanel.reset();
      scanPanel.scan(); 
    }
    else if (e.getSource() == extractBut) {
      boolean isExtracted = scanPanel.extractGrid(SUD_FNM);  
      if (isExtracted)
        gridPanel.build(SUD_FNM);
    }
    else if (e.getSource() == solveBut)
      gridPanel.solve();
  }  // end of actionPerformed()



  // -------------------------------------------------------

  public static void main( String args[] )
  {  new SudokuOCR();  }

} // end of SudokuOCR class

