package zxf.imageio;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageIOWriterTests {
    public static void main(String[] args) throws IOException {
        try (InputStream inputStream = ImageIOWriterTests.class.getResourceAsStream("/test.png")) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BufferedImage starImage = bufferedImage.getSubimage(2, 2, 196, 196);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(starImage, 2, 202, 196, 196, null);
            graphics2D.drawImage(starImage, 202, 2, 196, 196, null);
            graphics2D.drawImage(starImage, 202, 202, 196, 196, null);
            graphics2D.dispose();
            try (OutputStream pngOutputStream = Files.newOutputStream(Paths.get("output/star.png"))) {
                ImageIO.write(bufferedImage, "png", pngOutputStream);
            }
            //JDK8 don't support tiff output(Need add external libraries); JDK11 is OK.
            try (OutputStream tiffOutputStream = Files.newOutputStream(Paths.get("output/star.tiff"))) {
                ImageIO.write(bufferedImage, "tiff", tiffOutputStream);
            }
        }
    }
}
