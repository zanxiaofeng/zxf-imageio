package zxf.pdfbox;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import zxf.utils.TIFFUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TiffConverter {

    public byte[] convertFromPdf(String pdfPath, Float dpi) throws IOException {
        File pdfFile = Paths.get(pdfPath).toFile();

        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

            List<BufferedImage> images = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
                images.add(pdfRenderer.renderImageWithDPI(pageIndex, dpi));
            }
            System.out.printf("%d images\n", images.size());

            // Write to temp file then read bytes, since TIFFUtils accepts File output
            File tempFile = File.createTempFile("tiff-conversion-", ".tiff");
            try {
                TIFFUtils.writeTIFF(images, "LZW", tempFile, null, null, dpi.intValue());
                return Files.readAllBytes(tempFile.toPath());
            } finally {
                tempFile.delete();
            }
        }
    }
}
