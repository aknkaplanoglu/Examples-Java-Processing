/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

class ImagePixelComparison {

    private static BufferedImage convertImageToFormat(
            BufferedImage img, String type) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, type, baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        BufferedImage encodedImage = ImageIO.read(bais);
        return encodedImage;
    }

    private static void compareImagesByPixel(BufferedImage bi1, BufferedImage bi2) {
        for (int ii=0; ii<bi1.getHeight(); ii++) {
            for (int jj=0; jj<bi1.getWidth(); jj++) {
                boolean b = bi1.getRGB(jj, ii)==bi2.getRGB(jj, ii);
                String s = (b ? "0" : "-");
                System.out.print(s);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = 
                new BufferedImage(20,6,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = originalImage.createGraphics();
        g.setColor(Color.RED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(0, 0, 20, 6);
        g.dispose();

        System.out.println("JPG");
        BufferedImage jpegImage = convertImageToFormat(originalImage, "jpg");
        compareImagesByPixel(originalImage,jpegImage);
        System.out.println("PNG");
        BufferedImage pngImage = convertImageToFormat(originalImage, "png");
        compareImagesByPixel(originalImage,pngImage);
    }
}