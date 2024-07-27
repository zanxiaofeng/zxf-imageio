package zxf.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import zxf.imageio.ImageIOWriterTests;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignatureMergeTests {
    public static Rectangle signatureBox = new Rectangle((int)(1.12f * 72), (int)(1.42f * 72), (int)(3.8f * 72), (int)(0.73f * 72));

    public static void main(String[] args) throws IOException {
        generate(Paths.get("./input/pdf-conversion-services-filled.pdf"), Paths.get("./output/pdf-conversion-services-signed.pdf"), "/IMG_20240723_081450.jpg");
    }

    private static void generate(Path pdfPath, Path pdfOutput, String imageFile) throws IOException {
        byte[] signature = extractSignatureToJPG(imageFile);
        try (PDDocument pdDocument = PDDocument.load(pdfPath.toFile())) {
            PDPage page = pdDocument.getPage(0);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdDocument, signature, "signature.jpg");
            try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.drawImage(pdImage, (float) signatureBox.getX(), (float) signatureBox.getX(), (float) signatureBox.getWidth(), (float) signatureBox.getHeight());
            }
            pdDocument.save(pdfOutput.toFile());
        }
    }

    private static byte[] extractSignatureToJPG(String imageFile) throws IOException {
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

            BufferedImage sourceImage = extractImage(imageFile);
            jpgWriter.write(null, new IIOImage(sourceImage, null, null), params);
            jpgWriter.dispose();
        }
        return jpgOutputStream.toByteArray();
    }

    private static BufferedImage extractImage(String imageFile) throws IOException {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream(imageFile)) {
            BufferedImage sourceImage = ImageIO.read(sourceInputStream);
            return sourceImage.getSubimage(0, sourceImage.getHeight() / 3, sourceImage.getWidth(), sourceImage.getHeight() / 3);
        }
    }
}
