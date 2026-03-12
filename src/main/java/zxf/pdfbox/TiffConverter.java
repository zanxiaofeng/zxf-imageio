package zxf.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class TiffConverter {

    public byte[] convertFromPdf(String pdfPath, Float dpi) throws IOException {
        ByteArrayOutputStream tiffOutputStream = new ByteArrayOutputStream();
        File pdfFile = Paths.get(pdfPath).toFile();
        // PDFBox 3.x: 使用 Loader.loadPDF(File) 替代 PDDocument.load(File)
        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile);
             ImageOutputStream tiffImageOutputStream = ImageIO.createImageOutputStream(tiffOutputStream)) {
            ImageWriter multiPageTiffWriter = ImageIO.getImageWritersByFormatName("tiff").next();
            multiPageTiffWriter.setOutput(tiffImageOutputStream);

            ImageWriteParam params = multiPageTiffWriter.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType("LZW");
            params.setCompressionQuality(0.8f);

            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            multiPageTiffWriter.prepareWriteSequence(null);
            for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
                //In Pdf, One point equates to 1/72 of an inch.
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, dpi);
                IIOMetadata metadata = multiPageTiffWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), params);
                multiPageTiffWriter.writeToSequence(new IIOImage(image, null, metadata), params);
            }
            multiPageTiffWriter.endWriteSequence();
        }
        return tiffOutputStream.toByteArray();
    }
}
