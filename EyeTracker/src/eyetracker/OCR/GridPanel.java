/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// GridPanel.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, Jan 2013

/* The grid shown in this panel, looks like the following:

       0 1 2   3 4 5   6 7 8
     -------------------------
   0 |   8   | 4   2 |   6   |
   1 |   3 4 |       | 9 1   |
   2 | 9 6   |       |   8 4 |
     -------------------------
   3 |       | 2 1 6 |       |
   4 |       |       |       |
   5 |       | 3 5 7 |       |
     -------------------------
   6 | 8 4   |       |   7 5 |
   7 |   2 6 |       | 1 3   |
   8 |   9   | 7   1 |   4   |
     -------------------------

   The grid is represented by GRID_SIZE (9) x GRID_SIZE JSpinner
   components which can have the value 1-9 or ' ' (as defined in SUD_VALS). 
   The indicies along
   the top and down the left are drawn as images, and the entire thing
   is laid out using TableLayout (http://java.net/projects/tablelayout/)

   The panel for the grid is made up of NUM_BOXES (3) * NUM_BOXES box
   panels, each containing 9 JSpinners.

   The data for the spinners (i.e. the sudoku numbers) are stored in a 
   GRID_SIZE x GRID_SIZE 2D integer array called table[][].
   Blank squares are represented by 0's in the array.

   
   When build() is called (due to the user pressing "Extract Grid"),
   OCR recognition is applied to the Sudoku grid
   image, using the command tool, gocr (http://jocr.sourceforge.net/). The
   command is called using my SaferExec class, and the results are stored in
   an XML file. That data is read in and used to update table[][] and the
   spinners grid.

   Usually the OCR has missed numbers or incorrectly read numbers, and so
   the user will need to manually adjust the spinner values. This will cause
   table[][] to be updated.

   When the user is happy, he can press the "Solve" button in the application,
   which will trigger a call to solve() here, which uses Solver.isSolvable()
   to attempt to complete the sudoku. If it is successful, then the spinners
   grid is updated with the table[][] values.
*/

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.util.*;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import info.clearthought.layout.TableLayout;
    // I assume the TableLayout JAR is in Java's classpath


public class GridPanel extends JPanel implements ChangeListener
{
  private static final int GRID_SIZE = 9;
  private static final int NUM_BOXES = 3;

  private static final int ELEM_SIZE = 50;

  private static final String SUD_VALS[] = 
                         new String[] { " ", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
       // possible values for each JSpinner

  // for reading in the gocr XML results 
  private static final String XML_FNM = "gocrOut.xml";
  private static final String XPATH_EXPR = "//box";


  private JSpinner[][] spinners;   // each spinner represents one Sudoku number square
  private int[][] table;    // each box contains a Sudoku number (or 0 meaning no value)
  private boolean drawingGrid = false;



  public GridPanel() 
  {
    setBackground(Color.white);

    double b = 5;    // border space
    double p = TableLayout.PREFERRED;
    double size[][] =  {{b, p, p, b},      // rows
                        {b, p, p, b}};     // columns
    TableLayout layout = new TableLayout(size);
    this.setLayout(layout);
        /* a non-standard layout manager, available from
           http://java.net/projects/tablelayout/ */

    // create a spinner for each sudoku number square
    spinners = new JSpinner[GRID_SIZE][GRID_SIZE];
    for (int row = 0; row < GRID_SIZE; row++)
      for (int col = 0; col < GRID_SIZE; col++)
        spinners[row][col] = makeSpinner(row, col, 0);

    // the sudoku grid has index labels on its left and above
    this.add( new JLabel(new ImageIcon("horiz.png")), "2 1");
    this.add( new JLabel(new ImageIcon("vertical.png")), "1, 2");
    this.add( makeGrid(spinners), "2, 2");

    table = new int[GRID_SIZE][GRID_SIZE];
  }  // end of GridPanel()



  // ------------------------ build panels ----------------------


  private JSpinner makeSpinner(int row, int col, int val)
  /* create the spinner for position [row][col], containing the value val.
     If val == 0, then the spinner will display ' '.

     Each spinner is asigned a unique name: "s" + row + col, which is used
     to identify it when a change triggers a call to stateChanged(0 below.
  */
  {
    JSpinner spinner = new JSpinner(new SpinnerCircularListModel(SUD_VALS));
    spinner.setName("s" + row + col);
    spinner.setPreferredSize(new Dimension(ELEM_SIZE, ELEM_SIZE));

    // set spinner's value
    if (val == 0)
      spinner.setValue(" ");
    else if ((val >= 1) && (val <= 9))
      spinner.setValue("" + val);
    else {
      System.out.println("Unknown value at (" + (row+1) + ", " + (col+1) + ")");
      spinner.setValue(" ");
    }

    // modify the spinner's JTextField  
    JSpinner.ListEditor editor = new JSpinner.ListEditor(spinner);  
    JTextField tf = editor.getTextField();  
    tf.setBackground(Color.WHITE);
    tf.setHorizontalAlignment(JTextField.CENTER);  
    tf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));  
    tf.setEditable(false);
    spinner.setEditor(editor);  

    spinner.addChangeListener(this);

    return spinner;
  }  // end of makeSpinner()



  private JPanel makeGrid(JSpinner[][] spinners)
  // The grid panel is made from NUM_BOXES*NUM_BOXES box panels
  {
    JPanel gridPanel = new JPanel();
    gridPanel.setLayout( new GridLayout(NUM_BOXES, NUM_BOXES));

    for (int row = 0; row < NUM_BOXES; row++)
      for (int col = 0; col < NUM_BOXES; col++) {
        JSpinner[] boxSpins = getBoxSpins(row*NUM_BOXES, col*NUM_BOXES, spinners);
        gridPanel.add( makeBox(boxSpins) );
      }
    return gridPanel;
  } // end of makeGrid()



  private JSpinner[] getBoxSpins(int bRow, int bCol, JSpinner[][] spinners)
  /* Each box panel contains 3x3 JSpinners, which must match the indicies in
     the spinners[][] array, so these are collected here into their own array.
  */
  {
    JSpinner[] boxSpins = new JSpinner[NUM_BOXES*NUM_BOXES];
    int pos = 0;
    for (int y = 0; y < NUM_BOXES; y++)
      for (int x = 0; x < NUM_BOXES; x++) {
        boxSpins[pos] = spinners[bRow + y][bCol + x];
        pos++;
      }
    return boxSpins;
  }  // end of getBoxSpins()



  private JPanel makeBox(JSpinner[] boxSpins)
  // the 9 JSpinners for this box are grouped into a panel 
  {
    JPanel boxPanel = new JPanel();
    boxPanel.setLayout( new GridLayout(3,3));
    boxPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    for (int i=0; i < boxSpins.length; i++)
      boxPanel.add(boxSpins[i]);
    return boxPanel;
  } // end of makeBox()


  // ------------- load OCR data -----------------------------


  public void build(String fnm)
  /* apply OCR to the image stored in fnm, and then read in the results
     from an XML file (XML_FNM). Create a list of NumberPos objects from the XML,
     convert them into integers in table[][], and then update the 
     spinners grid.
  */
  {
    applyOCR(fnm, XML_FNM);
    ArrayList<NumberPos> sudoNumbers = readNumbersXML(XML_FNM);
    fillTable(sudoNumbers);
    redrawGrid(table);
  }  // end of build()



  private void applyOCR(String inFnm, String xmlFnm)
  /* call gocr (http://jocr.sourceforge.net/)
     via a batch file "xmlGocr.bat" in the local directory.
     It reads in the sudoku grid image (stored in inFnm), and stores
     the results in XML format in xmlFnm */
  {
    System.out.println("Applying OCR to " + inFnm + " using gocr...");
    System.out.println("Saving XML results to " + xmlFnm);

    long startTime = System.currentTimeMillis();

    SaferExec se = new SaferExec(10);   // timeout is 10 secs
    se.exec("xmlGocr.bat", inFnm, xmlFnm);
       /* gocr is a command line tool, and is executed via a batch file
          which is called using my SaferExec class. If the call doesn't finish
          within 10 seconds then it is aborted. */

    long duration = System.currentTimeMillis() - startTime;
    System.out.println("OCR processing took " + Math.round(duration) + "ms");
    System.out.println();
  }  // end of applyOCR()



  private ArrayList<NumberPos> readNumbersXML(String xmlFnm)
  /* Read in the gocr XML results from xmlFnm, and create a list
     of NumberPos objects based on the XML's box values. 
     A typical box looks like:
         <box x="219" y="79" dx="24" dy="40" value="6" numac="1" weights="97" />
     A box shows the location of an OCR result in the image.
  */
  {
    System.out.println("Reading in XML results from " + xmlFnm);

    ArrayList<NumberPos> sudoNumbers = new ArrayList<NumberPos>();

    try {
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); 
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document doc = builder.parse(xmlFnm);
    
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr  = xpath.compile(XPATH_EXPR);
             /* all the "box" nodes in the XML are collected, since each one
                represents an OCR result */
    
      Object result = expr.evaluate(doc, XPathConstants.NODESET);
      NodeList nodes = (NodeList) result;
      NumberPos np;
      for (int i = 0; i < nodes.getLength(); i++) {
        np = new NumberPos( nodes.item(i).getAttributes() );
                // each NumberPos object is built from a box node's attributes
        if (np.isValid())
          sudoNumbers.add(np);
      }
    }
    catch(Exception e)
    {  System.out.println(e);  }

    return sudoNumbers;
  }  // end of readNumbersXML()



  private void fillTable(ArrayList<NumberPos> sudoNumbers)
  /* Each NumberPos obejct is used to update a table[][] entry based
     on the NumberPos (x,y) indicies and its value. */
  {
    for (int row = 0; row < GRID_SIZE; row++)
      for (int col = 0; col < GRID_SIZE; col++)
        table[row][col] = 0;      // reset table[][]

    // System.out.println("No. of Sudoku values found: " + sudoNumbers.size());
    for(NumberPos np : sudoNumbers)
      table[np.getYIndex()][np.getXIndex()] = np.getValue();
  }  // end of fillTable()




  private void redrawGrid(int[][] table)
  /* Each table[][] value is used to update the corresponding spinner
     in the spinners[][] array, which is then redrawn.
  */
  {
    drawingGrid = true;     // used to disable stateChanged() processing

    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        if (table[row][col] == 0)
          spinners[row][col].setValue(" ");
        else
          spinners[row][col].setValue( ""+table[row][col] );
      }
    }
    repaint();
    drawingGrid = false;
  }  // end of redrawGrid()


  // ------------------ reset grid ---------------------------


  public void reset()
  /* reset the table [][] array to all 0's, and then update the
     grid accordingly */
  {
    for (int row = 0; row < GRID_SIZE; row++)
      for (int col = 0; col < GRID_SIZE; col++)
        table[row][col] = 0;

    redrawGrid(table);
  }  // end of reset()


  // ------------------ user modifications ---------------------------


  public void stateChanged(ChangeEvent evt) 
  /* This method is called when a JSpinner changes. This can happen either
     when the spinner is being updated by redrawGrid(), or when the user changes
     a spinner. Only the user case needs to be checked here.
  */
  {
    if (drawingGrid)    // change triggered by redrawing of grid -- ignore
      return;

    // identify the spinner and get its value
    JSpinner spinner = (JSpinner) evt.getSource();
    String spinVal = (String) spinner.getValue();
    String spinName = spinner.getName();
    // System.out.println(spinName + " Value: \"" + spinVal + "\"");
    modifyTable(spinName, spinVal);
  }  // end of stateChanged()




  private void modifyTable(String spinName, String spinVal)
  /* Examine the spinner name, which has the format 
     s[0-8][0-8]] for row, col to decide which table[][] value should
     be updated. spinVal will be ' ' or 1-9, but ' ' is stored
     as a 0.
  */
  {
    int row = getInt(spinName.charAt(1));
    if ((row < 0) || (row > 8)) {
      System.out.println("row index (" + row + ") out of range");
      return;
    }

    int col = getInt(spinName.charAt(2));
    if ((col < 0) || (col > 8)) {
      System.out.println("column index (" + col + ") out of range");
      return;
    }

    int val = 0;
    if (!spinVal.equals(" ")) {
      val = getInt(spinVal.charAt(0));
      if ((val < 1) || (val > 9)) {
        System.out.println("value (" + val + ") out of range");
        val = 0;
      }
    }

    table[row][col] = val;
    // System.out.println("table modified ["+ row + "][" + col + "] = " + val);
    repaint(); 
  }  // end of modifyTable()



  private int getInt(char ch)
  // convert ch into an integer (or -1)
  {
    int value = -1;
    try {
      value = Integer.parseInt(""+ch);
    }
    catch (NumberFormatException e){}
    return value;
  }  // end of getInt()



  // --------------------------- solve table --------------------------------


  public void solve()
  // Calculate the complete Sudoku table, and fill in the grid
  {
    boolean isSolved = Solver.isSolvable(table);   // changes table
    if (isSolved)
      redrawGrid(table);
  }  // end of solve()


}  // end of GridPanel class

