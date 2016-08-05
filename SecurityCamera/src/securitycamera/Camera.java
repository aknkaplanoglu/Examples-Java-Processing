/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package securitycamera;

import JMyron.JMyron;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Dell
 */
public class Camera extends javax.swing.JFrame {

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
    public int detect=0;
    private Point start_drag;
    private Point start_loc;
   
    
      
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
       
        
   public Camera() {
        initComponents();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
        startCapture();
        
    }
    private void configureMycron() {
        
        
        m = new JMyron();//make a new instance of the object
        m.start(width, height);//start a capture at 320x240        
        //m.findGlobs(0);//disable the intelligence to speed up frame rate
        m.findGlobs(1);
        //change color to track
       
        //m.trackColor(r, g, b, tol);
        //set labels
        jLabel1.setBounds(new Rectangle(width, height));
        m.adaptivity(90); 
        m.adapt();
    }
    private void getCameraImage() {
        
        
        
        
        bi = new BufferedImage(width, height, BufferedImage.OPAQUE);
     
        m.update();//update the camera view
        imageIntArray = m.image(); //get the normal image of the camera
     
        bi.setRGB(0, 0, width, height, imageIntArray, 0, width);
      
        
    }
    private void startCapture() {
        
        configureMycron();
        captureOn = true;
        Runnable capture = new Runnable() {

            @Override
            public void run() {
                while (captureOn) {
                    try {
                        getCameraImage();
                        jLabel1.setIcon(new ImageIcon(bi));
                        test();
                        Thread.sleep(frameRate);
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
                    }       
                   
                    
                }
               
            }
        };
        Thread t = new Thread(capture);
        t.start();
       
        
        
        
    }

    private void test() {
        
      
        Graphics no = jLabel1.getGraphics();
       
       
        rt= m.retinaImage();//this returns the retina image (the image slowly adapting to the camera, used for motion)
        
        di=m.differenceImage(); //returns the difference image - an image useful in determining motion
  
        //int [][][]pi=m.globPixels(); //returns a list of actual pixel positions for each glob. This is a list of lists of points.
        int[][] globBoxes = m.globBoxes();
        
        //globi = m.globsImage();
            for (int i = 0; i <globBoxes.length ; i++) {
                di = globBoxes[i];

           
                
                no.setColor(Color.YELLOW);
                
             
                       
                if(i>3){
             
                Sound hk=new Sound();
                hk.close();
                detect =1;
                
                }else
                {
                    Sound hk=new Sound();
                    hk.close();
                    detect=0;
                    
                    
                }
              
        }
           
            
            if(detect==1){
            
                Sound mp=new Sound("dogo.wav");
                mp.play();
                
                }else{
               
                }
        jLabel1.paintComponents(no);
        jLabel1.validate();
        this.validate();
        
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setUndecorated(true);

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(1300, 707));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel3MousePressed(evt);
            }
        });
        jPanel3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel3MouseDragged(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Take");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setDoubleBuffered(true);
        jLabel1.setFocusable(false);
        jLabel1.setOpaque(true);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        jLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel1MouseDragged(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 153, 255));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Close");
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton4.setBorderPainted(false);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusPainted(false);
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton4MouseExited(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 255));
        jLabel3.setText("Aeromba Motion Security Camera Version 1.0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 597, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        jButton1.setBackground(Color.WHITE);
        jButton1.setForeground(Color.BLACK);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        jButton1.setBackground(new java.awt.Color(0,153,255));
        jButton1.setForeground(Color.WHITE);
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //save image
        captureOn=false;
        Thread.interrupted();
        m.stop();
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
            (int) (target_location.getY() + cursor.getY()));
      }
    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        this.start_drag = this.getScreenLocation(evt);
        this.start_loc = this.getLocation();
    }//GEN-LAST:event_jLabel1MousePressed

    private void jLabel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseDragged
        Point current = this.getScreenLocation(evt);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
            (int) current.getY() - (int) start_drag.getY());

        Point new_location = new Point(
            (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        this.setLocation(new_location);
    }//GEN-LAST:event_jLabel1MouseDragged

    private void jButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseEntered
        jButton4.setBackground(Color.WHITE);
        jButton4.setForeground(Color.BLACK);
    }//GEN-LAST:event_jButton4MouseEntered

    private void jButton4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseExited
        jButton4.setBackground(new java.awt.Color(0,153,255));
        jButton4.setForeground(Color.WHITE);
    }//GEN-LAST:event_jButton4MouseExited

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        System.exit(1);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jPanel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MousePressed
         this.start_drag = this.getScreenLocation(evt);
        this.start_loc = this.getLocation();
    }//GEN-LAST:event_jPanel3MousePressed

    private void jPanel3MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseDragged
        Point current = this.getScreenLocation(evt);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
            (int) current.getY() - (int) start_drag.getY());

        Point new_location = new Point(
            (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        this.setLocation(new_location);
    }//GEN-LAST:event_jPanel3MouseDragged
    
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
            java.util.logging.Logger.getLogger(Camera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
      
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Camera().setVisible(true);
                
               
                
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

   
        
      
   
}
