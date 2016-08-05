/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brighter;

import java.io.File;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.imageio.ImageIO;


public class Brighten extends JPanel{
   @Override
   public void paintComponent(Graphics g){
      Graphics2D g2d=(Graphics2D)g;
      try{
          //reading image data from file
          BufferedImage src=ImageIO.read(getClass().getResource("/images/face.jpg"));
          /* passing source image and brightening by 50%-value of 1.0f means original brightness */ 
          BufferedImage dest=changeBrightness(src,5.5f);
          //drawing new image on panel
          g2d.drawImage(dest,0,0,this);
          //writing new image to a file in jpeg format
          ImageIO.write(dest,"jpeg",new File("dest.jpg"));
      }catch(Exception e){
            e.printStackTrace();
      }
   }

   public BufferedImage changeBrightness(BufferedImage src,float val){
       RescaleOp brighterOp = new RescaleOp(val, 0, null);
       return brighterOp.filter(src,null); //filtering
   }
    
   public static void main (String[] args) {
       JFrame jf=new JFrame("BRIGHTEN");
       Brighten obj=new Brighten();
       jf.getContentPane().add(obj);
       jf.setVisible(true);
       jf.setSize(800,700);
       jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
