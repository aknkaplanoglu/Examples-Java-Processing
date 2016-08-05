/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcam;

import java.io.*;
import javax.imageio.ImageIO;
import JMyron.JMyron;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import processing.core.*;


public class Cam2 extends javax.swing.JFrame {

    JMyron m;//a camera object
    int width = 1280;
    int height = 720;
    int frameRate = 28; //fps
    boolean captureOn;
    BufferedImage captureImage;
    int[] imageIntArray;
    int cR = 255, cG = 255, cB = 255;
    int []di;
    int []rt;
    int[] globi;
    int[] im;
        
        BufferedImage bu = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
            
   public Cam2() {
        initComponents();
        
        
        
    }
    private void configureMycron() {
        

        m = new JMyron();//make a new instance of the object
        m.start(width, height);//start a capture at 320x240        
        
        //m.findGlobs(0);//disable the intelligence to speed up frame rate
        m.findGlobs(1);
        //change color to track
        int r=0;
        int g=0;
        int b=0;
        int tol=255;
        
        jLabel1.setBounds(new Rectangle(width, height));
        
        m.adaptivity(1); 
        m.adapt();
        
        
    }
    private void getCameraImage() {
            
        m.update();
        imageIntArray=m.image();
       
        bu.setRGB(0, 0, width, height, imageIntArray, 0, width);
        
        
    }
    
    private void startCapture() {
        
        configureMycron();
        //imageIntArray=m.image();
        captureOn = true;
        Runnable capture = new Runnable() {

            @Override
            public void run() {
                while (captureOn) {
                    try {
                        getCameraImage();
                        
                        jLabel1.setIcon(new ImageIcon(bu));
                       
                        test();
                       // Thread.sleep(frameRate);
                      
                    } catch (Exception ex) {
                        Logger.getLogger(Cam2.class.getName()).log(Level.SEVERE, null, ex);
                    }      
                                  
                }
                //m.update();
               
            }
            
        };
        
        Thread t = new Thread(capture);
        
        t.start();
        
    }
    
    
int q;
int b;
Graphics gg;
    private void test() {
        
      
        
        BufferedImage op = bu;//=bu.getSubimage(300, 400, 80, 50);
        
        
       gg=op.createGraphics();
       
       
       
        rt= m.retinaImage();
        m.adapt();
        m.adaptivity(1);
        
        di= m.differenceImage(); //get the normal image of the camera
      /* Runnable r=new Runnable(){
       public void run(){
           int r=m.average(155, 470, 200, 200);
       
        m.trackColor(r, r, r, 25);
        gg.fillRect(155,470,200,200);
        }};
       Thread t=new Thread(r);
       t.start();
       */
       /* //globi = m.globsImage();
            for (int i = 0; i <globBoxes.length ; i++) {
                di = globBoxes[i];
                
                int t=m.average(rt[0], rt[1], rt[2], rt[3]);
                int t1=m.average(di[0], di[1], di[2], di[3]);
        
              if(t!=t1){
              
                  System.out.println("r is"+r);
              }
                gg.setColor(Color.YELLOW);
            }
        */
        jLabel1.paintComponents(gg);
        jLabel1.validate();
        this.validate();
        
    }
    
     private int red(int pixel) {
        return (pixel & 0x00ff0000) >> 16;
    }

    private int green(int pixel) {
        return (pixel & 0x0000ff00) >> 8;
    }

    private int blue(int pixel) {
        return (pixel & 0x000000ff);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Take");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setDoubleBuffered(true);
        jLabel1.setFocusable(false);
        jLabel1.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(686, 686, 686))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1339, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
  
            //save image
                        
                       //File file = new File("F:\\Netbeans Projects\\Aeromba Human Scanner\\palml"+".png");  
                       File file = new File("palml"+".png");  
                        try{
                           
                            ImageIO.write(bu.getSubimage(300, 0, 700, 650),"png",file);    
                            
                        } catch (Exception e) {
                        
                        }
            
        String dir="F:\\Netbeans Projects\\Cam2\\";
             
        // Lists all files in folder
        File folder = new File(dir);
        File fList[] = folder.listFiles();
        // Searchs .log
        for (int i = 0; i < fList.length; i++) {
        String pes = String.valueOf(fList[i]);
        if (pes.endsWith(".log")) {
        // and deletes
        fList[i].delete();
    }
    
}
             captureOn=false;
             m.stop();
             this.dispose(); 
    }//GEN-LAST:event_jButton1ActionPerformed
BufferedImage bufferedImage;
    
   
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      
       
       Thread.yield();
        m.stop();
       this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
  startCapture();
     
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
    }//GEN-LAST:event_formWindowClosed
    
    public static void main(String args[]) {
        
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (    ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cam2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
      
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Cam2().setVisible(true);
               
                
               
                
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

   
        
      
   
}
