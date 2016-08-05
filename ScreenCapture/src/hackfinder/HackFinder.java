package hackfinder;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;


public class HackFinder{

	
	public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();	
	public static int captureInterval = 5;	
	public static String store = "tmp";	
	public static boolean record = false;

      
        
	public static void startRecord() {
            
		Thread recordThread = new Thread() {
			@Override
			public void run() {
				Robot rt;
				int cnt = 0;
				try {
					rt = new Robot();
					while (cnt == 0 || record) {
						BufferedImage img = rt.createScreenCapture(new Rectangle(screenWidth,screenHeight));
						ImageIO.write(img, "jpeg", new File("./"+store+"/"+ System.currentTimeMillis() + ".jpeg"));
						if (cnt == 0) {
							record = true;
                                                        
							cnt = 1;
						}
						// System.out.println(record);
						Thread.sleep(captureInterval);
                                                
					}
                                        
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		recordThread.start();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("######### Starting Easy Capture Recorder #######");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		for(int i=0;i<1;i++){
			Thread.sleep(1);
		}
		File f = new File(store);
		if(!f.exists()){
			f.mkdir();
		}
		startRecord();
		System.out.println("\nEasy Capture is recording now!!!!!!!");	
                
        }              
}
        
