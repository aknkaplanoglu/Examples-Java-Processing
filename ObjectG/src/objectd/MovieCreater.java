/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;



import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import javax.media.MediaLocator;

/**
 *
 * @author H.M.T.Gihan
 */
public class MovieCreater {
 
	public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public static int captureInterval = 90;
	public static String store = "cctv\\today";
	public static boolean record = false;

	
    private static final String SRC_FOLDER = "\\cctv\\today";
    static String workingDir = System.getProperty("user.dir");
    
  public MovieCreater(){
  
  }  
  public static void DelTmp(){
    File directory = new File(workingDir+SRC_FOLDER);
    System.out.println("Current working directory : " + workingDir);
    	//make sure directory exists
    	if(!directory.exists()){
 
           System.out.println("Directory does not exist.");
           System.exit(0);
 
        }else{
 
           try{
 
               //delete(directory);
 
           }catch(Exception e){
               e.printStackTrace();
               System.exit(0);
           }
        }
 
    	System.out.println("Done");
        
    }
 
    public static void delete(File file)throws IOException{
 
    	if(file.isDirectory()){
 
    		//directory is empty, then delete it
    		if(file.list().length==0){
 
    		   file.delete();
    		   System.out.println("Directory is deleted : " 
                                                 + file.getAbsolutePath());
 
    		}else{
 
    		   //list all the directory contents
        	   String files[] = file.list();
 
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
 
        	      //recursive delete
        	     delete(fileDelete);
        	   }
 
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     System.out.println("Directory is deleted : " 
                                                  + file.getAbsolutePath());
        	   }
    		}
 
    	}else{
    		//if file, then delete it
    		file.delete();
    		//System.out.println("File is deleted : " + file.getAbsolutePath());
    	}
    
  }
    public static void main(String[] args) {
    
    try{
    makeVideo("hello"+".mp4");
    }catch(Exception e){
        System.out.println("No Temp Folder");
    }
    DelTmp();
    
    
    }
    
    public static void makeVideo(String movFile) {
		
		JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
		Vector<String> imgLst = new Vector<String>();
		File f = new File(store);
		File[] fileLst = f.listFiles();
		for (int i = 0; i < fileLst.length; i++) {
			imgLst.add(fileLst[i].getAbsolutePath());
		}
		// Generate the output media locators.
		MediaLocator oml;
		if ((oml = imageToMovie.createMediaLocator(movFile)) == null) {
			System.err.println("Cannot build media locator from: " + movFile);
			System.exit(0);
		}
        try {
            imageToMovie.doIt(640, 480, (400 / 50),imgLst, oml);
        } catch (MalformedURLException ex) {
            System.out.println("No Temp Folder");
        }
        }
}
