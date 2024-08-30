package zxf.metadataextractor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.xmp.XmpDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class MetadataExtractorTests {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        extractMetadata(Paths.get("./output/developer-mozilla-org-CORS-zh.tiff").toFile());
        extractMetadata(Paths.get("./output/IMG_20240723_081450-output.jpg").toFile());
        extractMetadata(Paths.get("./output/signed.png").toFile());
        extractMetadata(Paths.get("./input/IMG_20240830_121530.jpg").toFile());
        extractMetadata(Paths.get("./input/IMG_20240830_121622.jpg").toFile());
        extractMetadata(Paths.get("./input/IMG_20240830_121636.jpg").toFile());
        extractMetadata(Paths.get("./input/IMG_20240830_121652.jpg").toFile());
    }

    private static void extractMetadata(File file) throws ImageProcessingException, IOException {
        System.out.println("##: " + file.toPath());

        Metadata metadata = ImageMetadataReader.readMetadata(file);
        for (Directory directory : metadata.getDirectories()) {
            System.out.println("* " + directory);

            for (Tag tag : directory.getTags()) {
                System.out.printf("\t ## [%s - %s] %s = %s%n", directory.getName(), tag.getTagTypeHex(), tag.getTagName(), tag.getDescription());
            }

            if (directory instanceof XmpDirectory) {
                for (Map.Entry<String, String> property : ((XmpDirectory) directory).getXmpProperties().entrySet()) {
                    System.out.printf("\t @@ [%s] %s = %s%n", directory.getName(), property.getKey(), property.getValue());
                }
            }

            for (String error : directory.getErrors()) {
                System.err.println("\t $$ ERROR: " + error);
            }
        }
    }
}
