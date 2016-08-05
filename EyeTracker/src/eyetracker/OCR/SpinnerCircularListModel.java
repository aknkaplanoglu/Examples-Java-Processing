/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// SpinnerCircularListModel.java
// Andrew Davison, January 2013, ad@fivedots.coe.psu.ac.th

/* This model allows a spinner to cycle through its values without
   stopping at a beginning or end value.
*/


import java.awt.*;
import javax.swing.*;



public class SpinnerCircularListModel extends SpinnerListModel 

{
  public SpinnerCircularListModel(Object[] items) 
  {  super(items); }


  public Object getNextValue() 
  {
    java.util.List list = getList();
    int index = list.indexOf(getValue());
    index = (index >= list.size() - 1) ? 0 : index + 1;
    return list.get(index);
  }  // end of getNextValue()


  public Object getPreviousValue() 
  {
    java.util.List list = getList();
    int index = list.indexOf(getValue());
    index = (index <= 0) ? list.size() - 1 : index - 1;
    return list.get(index);
  }  // end of getPreviousValue()

}  // end of SpinnerCircularListModel class
