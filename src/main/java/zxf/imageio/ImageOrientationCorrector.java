package zxf.imageio;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageOrientationCorrector {
    public static void main(String[] args) throws Exception {
        correctImage("/IMG_20240723_081450.jpg","./output/IMG_20240723_081450.corrected.jpg");
        correctImage("/IMG_20240830_121622.jpg","./output/IMG_20240830_121622.corrected.jpg");
        correctImage("/IMG_20240830_121636.jpg","./output/IMG_20240830_121636.corrected.jpg");
        correctImage("/IMG_20240830_121652.jpg","./output/IMG_20240830_121652.corrected.jpg");
        correctImage("/IMG_20240904_135326.jpg","./output/IMG_20240904_135326.corrected.jpg");
    }

    public static void correctImage(String inputFile, String outputFile) throws Exception {
        try (InputStream sourceInputStream = ImageIOWriterTests.class.getResourceAsStream(inputFile);
             OutputStream jpgOutputStream = Files.newOutputStream(Paths.get(outputFile));
             ImageOutputStream jpgImageOutputStream = ImageIO.createImageOutputStream(jpgOutputStream)) {

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            jpgWriter.setOutput(jpgImageOutputStream);

            ImageWriteParam params = jpgWriter.getDefaultWriteParam();
            //MODE_DEFAULT without Type and Quality
            //params.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
            //Or MODE_EXPLICIT with Type and Quality
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.8f);

            BufferedImage sourceImage = correctImage(new BufferedInputStream(sourceInputStream));
            jpgWriter.write(null, new IIOImage(sourceImage, null, null), params);
            jpgWriter.dispose();
        }
    }

    public static BufferedImage correctImage(BufferedInputStream sourceInputStream) throws Exception {
        sourceInputStream.mark(2000000000);
        BufferedImage sourceImage = ImageIO.read(sourceInputStream);

        sourceInputStream.reset();
        Metadata metadata = ImageMetadataReader.readMetadata(sourceInputStream);

        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        if (exifIFD0Directory == null || jpegDirectory == null || !exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
            System.out.println("No need correct.");
            return sourceImage;
        }

        int orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        int width = jpegDirectory.getImageWidth();
        int height = jpegDirectory.getImageHeight();

        return transformImage(sourceImage, orientation, width, height);
    }

    private static BufferedImage transformImage(BufferedImage srcImage, int orientation, int width, int height) throws Exception {
        System.out.println("Source Image: " + srcImage);
        AffineTransform transform = getExifTransformation(orientation, width, height);
        AffineTransformOp affineTransformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        Rectangle bounds = affineTransformOp.getBounds2D(srcImage).getBounds();
        BufferedImage destinationImage = new BufferedImage(bounds.x + bounds.width, bounds.y + bounds.height, srcImage.getType());
        destinationImage = affineTransformOp.filter(srcImage, destinationImage);
        System.out.println("Dest Image:" + destinationImage);
        return destinationImage;
    }

    private static AffineTransform getExifTransformation(int orientation, int width, int height) {
        AffineTransform transform = new AffineTransform();

        switch (orientation) {
            case 1:
                System.out.println("*1*:Horizontal(normal)");
                break;
            case 2:
                System.out.println("*2*:Mirror horizontal");
                transform.scale(-1.0, 1.0);
                transform.translate(-width, 0);
                break;
            case 3:
                System.out.println("*3*:Rotate 180");
                transform.translate(width, height);
                transform.rotate(Math.PI);
                break;
            case 4:
                System.out.println("*4*:Mirror vertical");
                transform.scale(1.0, -1.0);
                transform.translate(0, -height);
                break;
            case 5:
                System.out.println("*5*:Mirror horizontal and rotate 270 CW");
                transform.rotate(-Math.PI / 2);
                transform.scale(-1.0, 1.0);
                break;
            case 6:
                System.out.println("*6*:Rotate 90 CW");
                transform.translate(height, 0);
                transform.rotate(Math.PI / 2);
                break;
            case 7:
                System.out.println("*7*:Mirror horizontal and rotate 90 CW");
                transform.scale(-1.0, 1.0);
                transform.translate(-height, 0);
                transform.translate(0, width);
                transform.rotate(3 * Math.PI / 2);
                break;
            case 8:
                System.out.println("*8*:Rotate 270 CW");
                transform.translate(0, width);
                transform.rotate(3 * Math.PI / 2);
                break;
            default:
                break;
        }

        return transform;
    }
}
