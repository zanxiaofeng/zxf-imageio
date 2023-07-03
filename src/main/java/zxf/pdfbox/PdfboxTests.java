package zxf.pdfbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfboxTests {
    public static void main(String[] args) throws IOException {
        TiffConverter tiffConverter = new TiffConverter();
        byte[] tiffBytes = tiffConverter.convertFromPdf("input/developer-mozilla-org-CORS-zh.pdf",200f);
        Files.write(Paths.get("output/developer-mozilla-org-CORS-zh.tiff"), tiffBytes);
    }
}
