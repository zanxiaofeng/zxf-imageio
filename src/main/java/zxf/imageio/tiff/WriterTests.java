package zxf.imageio.tiff;

import zxf.imageio.ImageIOWriterTests;
import zxf.utils.TIFFUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class WriterTests {
    public static void main(String[] args) throws IOException {
        TIFFUtils.writeTIFF(binaryImage(extractImage()), "CCITT T.6", Paths.get("output/IMG_20240723_081450-output.tiff").toFile(), null, null, 300);
    }

    private static BufferedImage extractImage() throws IOException {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream("/IMG_20240723_081450.jpg")) {
            BufferedImage sourceImage = ImageIO.read(sourceInputStream);
            return sourceImage.getSubimage(0, sourceImage.getHeight() / 3, sourceImage.getWidth(), sourceImage.getHeight() / 3);
        }
    }

    private static BufferedImage binaryImage(BufferedImage image) {
        BufferedImage binary = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = binary.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return binary;
    }
}
