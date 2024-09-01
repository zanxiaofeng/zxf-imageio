package zxf.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import zxf.imageio.ImageIOWriterTests;
import zxf.imageio.ImageOrientationCorrector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignatureMergeTests {
    //In Pdf, One point equates to 1/72 of an inch.
    public static Rectangle signatureBox = new Rectangle((int) (1.12f * 72), (int) (1.40f * 72), (int) (3.8f * 72), (int) (0.73f * 72));

    public static void main(String[] args) throws Exception {
        //System.setProperty("java.awt.headless=", "true");
        generate(Paths.get("./input/pdf-conversion-services-filled.pdf"), Paths.get("./output/pdf-conversion-services-signed-1.pdf"), "/IMG_20240723_081450.jpg");
        generate(Paths.get("./input/pdf-conversion-services-filled.pdf"), Paths.get("./output/pdf-conversion-services-signed-2.pdf"), "/IMG_20240830_121622.jpg");
        generate(Paths.get("./input/pdf-conversion-services-filled.pdf"), Paths.get("./output/pdf-conversion-services-signed-3.pdf"), "/IMG_20240830_121636.jpg");
        generate(Paths.get("./input/pdf-conversion-services-filled.pdf"), Paths.get("./output/pdf-conversion-services-signed-4.pdf"), "/IMG_20240830_121652.jpg");
    }

    private static void generate(Path pdfPath, Path pdfOutput, String imageFile) throws Exception {
        byte[] signature = extractSignatureToJPG(imageFile);
        try (PDDocument pdDocument = PDDocument.load(pdfPath.toFile())) {
            PDPage page = pdDocument.getPage(0);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdDocument, signature, "signature.jpg");
            try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.drawImage(pdImage, (float) signatureBox.getX(), (float) signatureBox.getY(), (float) signatureBox.getWidth(), (float) signatureBox.getHeight());
            }
            pdDocument.save(pdfOutput.toFile());
        }
    }

    private static byte[] extractSignatureToJPG(String imageFile) throws Exception {
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

    private static BufferedImage extractImage(String imageFile) throws Exception {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream(imageFile)) {
            BufferedImage sourceImage = ImageOrientationCorrector.correctImage(new BufferedInputStream(sourceInputStream));
            return sourceImage.getSubimage(0, sourceImage.getHeight() / 3, sourceImage.getWidth(), sourceImage.getHeight() / 3);
        }
    }
}
