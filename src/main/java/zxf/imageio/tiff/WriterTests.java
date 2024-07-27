package zxf.imageio.tiff;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import zxf.imageio.ImageIOWriterTests;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriterTests {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream tiffOutputStream = new ByteArrayOutputStream();
        try (ImageOutputStream tiffImageOutputStream = ImageIO.createImageOutputStream(tiffOutputStream)) {
            ImageWriter tiffWriter = ImageIO.getImageWritersByFormatName("tiff").next();
            tiffWriter.setOutput(tiffImageOutputStream);

            ImageWriteParam params = tiffWriter.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType("JPEG");
            params.setCompressionQuality(0.8f);

            BufferedImage image = extractImage();
            tiffWriter.write(null, new IIOImage(image, null, null), params);
            tiffWriter.dispose();
        }
        Files.write(Paths.get("output/IMG_20240723_081450-output.tiff"), tiffOutputStream.toByteArray());
    }

    private static BufferedImage extractImage() throws IOException {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream("/IMG_20240723_081450.jpg")) {
            BufferedImage sourceImage = ImageIO.read(sourceInputStream);
            return sourceImage.getSubimage(0, sourceImage.getHeight() / 3, sourceImage.getWidth(), sourceImage.getHeight() / 3);
        }
    }
}
