/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// Solver.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, Jan 2013

/*  Use a brute-force search to fill in the gaps in a supplied Sudoku table.
    The core method is solve() which utilizes a
    backtracking recursive search over the board.

   This code is closely based on the Java Sudoku Solver by Bob Carpenter,
   available at http://www.colloquial.com/games/sudoku/java_sudoku.html

   The table is stored as an array of integers called table[][], as indicated:

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

   The blank squares are represented by 0's in the array.

   The table[][] is updated only if a solution is found, which is
   signalled by isSolvable() returning true or false.

*/

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;


public class Solver
{
  private static final int TABLE_SIZE = 9;
  private static final int NUM_BOXES = 3;


  public static boolean isSolvable(int[][] t)
  // can the table be filled in? If so, then t[][] is modified.
  {
    int[][] table = new int[TABLE_SIZE][TABLE_SIZE];
    for (int i=0; i < TABLE_SIZE; i++)   // make a copy of the table
      for(int j=0; j < TABLE_SIZE; j++)
        table[i][j] = t[i][j];

    if (!isTableValid(table)) {    
      // check the initial table's validity before trying to solve it
      System.out.println("Table is invalid");
      JOptionPane.showMessageDialog(null, "Table is invalid", 
                         "Solver Error", JOptionPane.ERROR_MESSAGE);

      return false;
    }

    long startTime = System.currentTimeMillis();
    if (!solve(0, 0, table)) {   // fill in the blanks (0's) in the table
      System.out.println("No solution");
      JOptionPane.showMessageDialog(null, "No solution", 
                         "Solver Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    System.out.println("\nCompleted table:");
    printTable(table);   

    for (int i=0; i < TABLE_SIZE; i++)    // copy result back into original array
      for(int j=0; j < TABLE_SIZE; j++)
        t[i][j] = table[i][j];
    
    System.out.println("Found a solution in " + 
                        (System.currentTimeMillis() - startTime) + "ms");

    return true;
  }  // end of isSolvable()



  private static boolean isTableValid(int[][] table)
  // check every non-blank element by calling isLegal()
  {
    for (int row = 0; row < TABLE_SIZE; row++) { // check all elements
      for (int col = 0; col < TABLE_SIZE; col++) { 
        int value = table[row][col];
        if (value != 0) {    // if has a value
          table[row][col] = 0;   // temporarily erase
          if (!isLegal(row, col, value, table)) {
            // System.out.println("Invalid at [" + row + "][" + col + 
            //                                  "]; cannot be " + value);
            return false;
          }
          table[row][col] = value;   // put value back (if ok)
        }
      }
    }
    return true;
  }  // end of isTableValid()



  private static boolean solve(int row, int col, int[][] table)
  /* fill in the blanks using a recursive backtracking search;
     process a column at a time */
  {
    if (row == TABLE_SIZE) {
      row = 0;  col++;    // move to top of next column
      if (col == TABLE_SIZE)
        return true;
    }

    if (table[row][col] != 0)   // skip table element that is filled
      return solve(row+1, col, table);

    for (int val = 1; val <= 9; val++) {     // try all data values 1-9
      if (isLegal(row, col, val, table)) {
        table[row][col] = val;
        if (solve(row+1, col, table))
          return true;
      }
    }
    table[row][col] = 0; // reset value when backtracking
    return false;
  }  // end of solve()




  private static boolean isLegal(int i, int j, int val, int[][] table)
  /* is val in table[i][j] allowed? There are three cases to check: 
      no duplicate val anywhere in the jth column, no duplicate val 
      anywhere in the ith row, and no duplicate val in the box containing
      this (i,j) element.
  */
  {
    if (val == 0)
      return true;

    for (int row = 0; row < TABLE_SIZE; row++) { // check elements in jth column
      if (val == table[row][j])
        return false;
    }

    for (int col = 0; col < TABLE_SIZE; col++) { // check elements in ith row
      if (val == table[i][col])
        return false;
    }

    // check box containing (i,j) element
    int boxRowOffset = (i/3)*3;
    int boxColOffset = (j/3)*3;
    for (int x = 0; x < NUM_BOXES; x++) {   // check elements in the box
      for (int y = 0; y < NUM_BOXES; y++) {
        if (val == table[boxRowOffset + y][boxColOffset + x])
          return false;
      }
    }

    return true; // no violations, so it's allowed
  }  // end of isLegal()



  private static void printTable(int[][] table)
  /* useful for debugging, and for creating a simple textual version
     of the finished Sudoku, which appears on the command line. */
  {
    System.out.println("   0 1 2   3 4 5   6 7 8 ");
    for (int i = 0; i < TABLE_SIZE; i++) {
      if (i%NUM_BOXES == 0)
        System.out.println("  -----------------------");
      System.out.print(""+i); 
      for (int j = 0; j < TABLE_SIZE; j++) {
        if (j%NUM_BOXES == 0)
          System.out.print("| ");
        System.out.print( (table[i][j] == 0) ? " " :
                                Integer.toString(table[i][j]));
        System.out.print(' ');
      }
      System.out.println("|");
    }
    System.out.println("  -----------------------");
    System.out.println();
  }  // end of printTable()



}  // end of Solver class

