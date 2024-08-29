package zxf.metadataextractor;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTypeDetectorTests {
    public static void main(String[] args) throws IOException {
        detectFileType(Paths.get("./output/developer-mozilla-org-CORS-zh.tiff"));
        detectFileType(Paths.get("./output/IMG_20240723_081450-output.jpg"));
        detectFileType(Paths.get("./output/signed.png"));
    }

    private static void detectFileType(Path file) throws IOException {
        FileType fileType = FileTypeDetector.detectFileType(new BufferedInputStream(Files.newInputStream(file)));
        System.out.println("FILE: " + file + ", TYPE: " + fileType);
    }
}
