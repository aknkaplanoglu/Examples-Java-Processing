
// FileUtils.java
// Sajan Joseph, sajanjoseph@gmail.com
// http://code.google.com/p/javafaces/
// Modified by Andrew Davison, April 2011, ad@fivedots.coe.psu.ac.th


import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;


public class FileUtils
{
  private static final String FILE_EXT = ".png";

  private static final String TRAINING_DIR = "trainingImages";

  private static final String EF_CACHE = "eigen.cache";

  private static final String EIGENFACES_DIR = "eigenfaces";
  private static final String EIGENFACES_PREFIX = "eigen_";

  private static final String RECON_DIR = "reconstructed";
  private static final String RECON_PREFIX = "recon_";



  public static ArrayList<String> getTrainingFnms()
  // return all the names of the training image files + their paths
  {
    File dirF = new File(TRAINING_DIR);
    String[] fnms = dirF.list( new FilenameFilter() {
      public boolean accept(File f, String name)
      {  return name.endsWith(FILE_EXT); }
    });

    if (fnms == null) {
      System.out.println(TRAINING_DIR + " not found");
      return null;
    }
    else if (fnms.length == 0) {
      System.out.println(TRAINING_DIR + " contains no " + " " + FILE_EXT + " files");
      return null;
    }
    else
      return getPathNms(fnms);
  }  // end of getTrainingFnms()



  private static ArrayList<String> getPathNms(String[] fnms)
  {
    ArrayList<String> imFnms = new ArrayList<String>();
    for (String fnm : fnms)
      imFnms.add(TRAINING_DIR + File.separator + fnm);

    Collections.sort(imFnms);
    return imFnms;
  }  // end of getPathNms()



  // -------------------- image files input -------------------------------


  public static BufferedImage[] loadTrainingIms(ArrayList<String> fnms)
  /* load all the specified images, making sure they are grayscale and the 
     same dimensions */
  {
    BufferedImage[] ims = new BufferedImage[fnms.size()];
    BufferedImage im = null;
    int i = 0;
    System.out.println("Loading grayscale images from " + TRAINING_DIR + "...");
    for (String fnm : fnms) {
      try {
        im = ImageIO.read( new File(fnm) );
        System.out.println("  " + fnm);  // reading
        ims[i++] = ImageUtils.toScaledGray(im, 1.0);    // make sure the images are gray
      }
      catch (Exception e) {
        System.out.println("Could not read image from " + fnm);
      }
    }
    System.out.println("Loading done\n");

    ImageUtils.checkImSizes(fnms, ims);
    return ims;
  }  // end of loadTrainingIms()



  public static BufferedImage loadImage(String imFnm)
  // return an image
  {
    BufferedImage image = null;
    try {
      image = ImageIO.read( new File(imFnm) );   // read in as an image
       System.out.println("Reading image " + imFnm);
    }
    catch (Exception e) {
      System.out.println("Could not read image from " + imFnm);
    }
    return image;
  }  // end of loadImage()




  public static void saveImage(BufferedImage im, String fnm)
  // save image in fnm
  {
    try {
      ImageIO.write(im, "png", new File(fnm));
      // System.out.println("Saved image to " + fnm);
    } 
    catch (IOException e) {
      System.out.println("Could not save image to " + fnm);
    }
  }  // end of saveImage()


  // ------------------ FaceBundle I/O --------------------------


  public static FaceBundle readCache()
  // read the FaceBundle object from a file called EF_CACHE
  {
    FaceBundle bundle = null;
    try {
      ObjectInputStream ois = new ObjectInputStream(
                                    new FileInputStream(EF_CACHE));
      bundle = (FaceBundle) ois.readObject();
      ois.close();
      System.out.println("Using cache: " + EF_CACHE);
      return bundle;
    }
    catch (FileNotFoundException e) {
      System.out.println("Missing cache: " + EF_CACHE);
    }
    catch (IOException e) {
      System.out.println("Read error for cache: " + EF_CACHE);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e);
    }
    return bundle;
  }	 // end of readCache()



  public static void writeCache(FaceBundle bundle)
  // save the FaceBundle object in a file called EF_CACHE
  {
    System.out.println("Saving eigenfaces to: " + EF_CACHE + " ...");
    try {
      ObjectOutputStream oos = new ObjectOutputStream( 
                                        new FileOutputStream(EF_CACHE) );
      oos.writeObject(bundle);
      System.out.println("Cache save succeeded");
      oos.close();
    }
    catch (Exception e) {
      System.out.println("Cache save failed");
      System.out.println(e);
    }
  } // end of writeCache()


  // ------------------ save EigenFaces as images -------------------


  public static void saveEFIms(Matrix2D egfaces, int imWidth)
  /* save each row of the eigenfaces matrix as an image in EIGENFACES_DIR, 
     whose pixel width is imWidth */
  {
    double[][] egFacesArr = egfaces.toArray();
    makeDirectory(EIGENFACES_DIR);

    for (int row = 0; row < egFacesArr.length; row++) {
      String fnm = EIGENFACES_DIR + File.separator + EIGENFACES_PREFIX + row + FILE_EXT;
      saveArrAsImage(fnm, egFacesArr[row], imWidth);
    }
  }  // end of saveEFIms()



  private static void makeDirectory(String dir)
  // create a new directory or delete the contents of an existing one
  {
    File dirF = new File(dir);
    if (dirF.isDirectory()) {
      System.out.println("Directory: " + dir + " already exists; deleting its contents");
      for (File f : dirF.listFiles())
        deleteFile(f);
    }
    else {
      dirF.mkdir();
      System.out.println("Created new directory: " + dir);
    }
  }  // end of makeDirectory()



  private static void deleteFile(File f)
  {
    if (f.isFile()) {
      boolean deleted = f.delete();
   /* if(deleted)
        System.out.println("  deleted: "+ f.getName() );
   */
    }
  }	 // end of deleteFile()



  private static void saveArrAsImage(String fnm, double[] imData, int width)
  // save a ID array as an image
  {
    BufferedImage im = ImageUtils.createImFromArr(imData, width);
    if (im != null) {
      try {
        ImageIO.write(im, "png", new File(fnm));
        System.out.println("  " + fnm);    // saving 
      }
      catch (Exception e) {
        System.out.println("Could not save image to " + fnm);
      }
    }
  }  // end of saveArrAsImage()


  // -------------------- save reconstructed images ------------------------------


  public static void saveReconIms2(double[][] ims, int imWidth)
  /* save each row of the images array as a separate image in RECON_DIR, 
     whose pixel width is imWidth */
  {
    makeDirectory(RECON_DIR);
    for (int i = 0; i < ims.length; i++) {
      String fnm = RECON_DIR + File.separator + RECON_PREFIX + i + FILE_EXT;
      saveArrAsImage(fnm, ims[i], imWidth);
    }
  }  // end of saveReconIms()



}  // end of FileUtils class