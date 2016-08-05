/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objectd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTest
{
    public static void main(final String args[])
        throws IOException
    {
        final File file = new File("F:\\Netbeans Projects\\FaceRecognizer\\Scanned Persons\\face1.jpg");
        final BufferedImage image = ImageIO.read(file);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final int clr = image.getRGB(x, y);
                final int red = (clr & 0x00ff0000) >> 16;
                final int green = (clr & 0x0000ff00) >> 8;
                final int blue = clr & 0x000000ff;

                // Color Red get cordinates
                if (red == 255) {
                    System.out.println(String.format("Coordinate %d %d", x, y));
                } else {
                    System.out.println("Red Color value = " + red);
                    System.out.println("Green Color value = " + green);
                    System.out.println("Blue Color value = " + blue);
                }
            }
        }
    }
}
