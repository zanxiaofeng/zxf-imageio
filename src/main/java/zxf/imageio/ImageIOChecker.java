package zxf.imageio;

import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ImageIOChecker {
    public static void main(String[] args) throws IOException {
        Path tiffFilePath = Paths.get("output/developer-mozilla-org-CORS-zh.tiff");
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(tiffFilePath.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            boolean isImage = readers.hasNext();
            System.out.println("Image type: " + (isImage ? readers.next().getFormatName() : "None"));
        }
    }
}
