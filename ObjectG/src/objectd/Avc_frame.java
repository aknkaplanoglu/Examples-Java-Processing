/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

/**
     * @author Jovi Dsilva
     */
public class Avc_frame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JCodecException {


long time = System.currentTimeMillis();

for (int i = 50; i < 57; i++) { 

BufferedImage frame = FrameGrab.getFrame(new File("hello.mp4"), i);

ImageIO.write(frame, "jpeg", new File("cctv/image"+i+".jpeg"));

}

System.out.println("Time Used:" + (System.currentTimeMillis() - time)+" Milliseconds");


    }
}
