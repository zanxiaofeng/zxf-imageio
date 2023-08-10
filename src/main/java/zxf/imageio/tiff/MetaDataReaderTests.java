package zxf.imageio.tiff;

import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.IOException;
import java.nio.file.Paths;

public class MetaDataReaderTests {
    public static void main(String[] args) throws TiffProcessingException, IOException {
        Metadata metadata = TiffMetadataReader.readMetadata(Paths.get("./output/developer-mozilla-org-CORS-zh.tiff").toFile());

        Iterable<Directory> directories = metadata.getDirectories();
        for (Directory directory : directories) {
            System.out.println(directory);
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }
}
