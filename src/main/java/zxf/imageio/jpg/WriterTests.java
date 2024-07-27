package zxf.imageio.jpg;

import zxf.imageio.ImageIOWriterTests;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WriterTests {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
        try (ImageOutputStream jpgImageOutputStream = ImageIO.createImageOutputStream(jpgOutputStream)) {
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            jpgWriter.setOutput(jpgImageOutputStream);

            ImageWriteParam params = jpgWriter.getDefaultWriteParam();
            //MODE_DEFAULT without Type and Quality
            //params.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
            //Or MODE_EXPLICIT with Type and Quality
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.8f);

            BufferedImage sourceImage = extractImage();
            jpgWriter.write(null, new IIOImage(sourceImage, null, null), params);
            jpgWriter.dispose();

        }
        Files.write(Paths.get("output/IMG_20240723_081450-output.jpg"), jpgOutputStream.toByteArray());
    }

    private static BufferedImage extractImage() throws IOException {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream("/IMG_20240723_081450.jpg")) {
            BufferedImage sourceImage = ImageIO.read(sourceInputStream);
            return sourceImage.getSubimage(0, sourceImage.getHeight() / 3, sourceImage.getWidth(), sourceImage.getHeight() / 3);
        }
    }
}
