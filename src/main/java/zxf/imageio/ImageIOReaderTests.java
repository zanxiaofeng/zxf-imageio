package zxf.imageio;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageIOReaderTests {
    public static void main(String[] args) throws IOException {
        Path tiffFilePath = Paths.get("output/developer-mozilla-org-CORS-zh.tiff");


        try(ImageInputStream imageInputStream = ImageIO.createImageInputStream(tiffFilePath.toFile())) {
            ImageReader multiPageTiffReader = ImageIO.getImageReadersByFormatName("tiff").next();
            multiPageTiffReader.setInput(imageInputStream);

            for (int i = 0; i < multiPageTiffReader.getNumImages(true); i++) {
                BufferedImage bufferedImage = multiPageTiffReader.read(i);
                System.out.println(i);
            }
        }
    }
}
