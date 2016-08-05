/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// NumberPos.java
// Andrew Davison, Jan 2013, ad@fivedots.coe.psu.ac.th

/*  Convert the attributes in a XML box expression into values
    in a NumberPos object. The box format is:
       <box x="76" y="89" dx="5" dy="10" value="_" />
    or
       <box x="84" y="81" dx="23" dy="41" value="8" numac="1" weights="99" />

    (x,y) is the position of the value, and (dx,dy) its size. The image
    is known to be IM_SIZE*IM_SIZE, and represent a Sudoku grid, and so
    it is possible to convert the box coordinates into Sudoku grid indicies.

    If the value is "_", or not a number (1-9), then this OCR value is invalid.
*/

import java.util.*;
import org.w3c.dom.*;



public class NumberPos
{
  // 600x600 grid of 9x9 sqs
  private static final int IM_SIZE = 600;  // size of final processed image
  private static final int GRID_SIZE = 9;

  private boolean isValid = true;
  private int value;
  private int xCenter, yCenter;     // image coords of center of value
  private int xIdx, yIdx;           // sudoku indicies for this position


  public NumberPos(NamedNodeMap nodeMap)
  {
     value = getNodeIntValue(nodeMap, "value");
     if ((value < 1) || (value > 9))
       isValid = false;

     int x = getNodeIntValue(nodeMap, "x");     // location of box
     int y = getNodeIntValue(nodeMap, "y");
     int width = getNodeIntValue(nodeMap, "dx");   // size of box
     int height = getNodeIntValue(nodeMap, "dy");

     xCenter = x + width/2;   // center of value in the image box
     yCenter = y + height/2;

     double digitSpacing = ((double) IM_SIZE)/GRID_SIZE;  // space between digits
     double digitOffset = digitSpacing/2.0;    // space from edge to first digit

     xIdx = (int)Math.round((xCenter-digitOffset)/digitSpacing);  
     yIdx = (int)Math.round((yCenter-digitOffset)/digitSpacing);

     // System.out.println("Value = " + value + " at (" +
     //                                   xCenter + ", " + yCenter + ")"); 
  }  // end of NumberPos()


  private int getNodeIntValue(NamedNodeMap nodeMap, String itemName)
  {
    String valStr = null;
    try {
      valStr = nodeMap.getNamedItem(itemName).getNodeValue();
      return Integer.parseInt(valStr);
    }
    catch(DOMException e)
    {  System.out.println("Parsing error");  
       isValid = false;
    }
    catch (NumberFormatException ex){ 
      // System.out.println("\"" + valStr + "\" not an integer");   // a "_"
      isValid = false;
    }
    return -1;
  }  // end of getNodeIntValue()


  public int getValue()
  {  return value;  }

  public int getX()
  {  return xCenter;  }

  public int getY()
  {  return yCenter;  }

  public boolean isValid()
  {  return isValid;  }

  public int getXIndex()
  {  return xIdx;  }

  public int getYIndex()
  {  return yIdx;  }


  public String toString()
  { String validStr = (isValid) ? "" : " - invalid";
    return "" + value + " at (" + xCenter + ", " + yCenter + ") = [" + 
                        xIdx + ", " + yIdx + "] " + validStr;  
  }


}  // end of NumberPos class
