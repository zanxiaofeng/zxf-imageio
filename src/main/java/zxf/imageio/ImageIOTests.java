package zxf.imageio;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import java.util.Arrays;
import java.util.Iterator;

public class ImageIOTests {
    public static void main(String[] args) {
        ImageIO.scanForPlugins();

        System.out.println("ReaderFormatNames:");
        Arrays.stream(ImageIO.getReaderFormatNames())
                .map(ImageIOTests::getReaderInfoByFormatName)
                .forEach(System.out::println);

        System.out.println("ReaderFileSuffixes:");
        Arrays.stream(ImageIO.getReaderFileSuffixes())
                .map(ImageIOTests::getReaderInfoBySuffix)
                .forEach(System.out::println);

        System.out.println("ReaderMIMETypes:");
        Arrays.stream(ImageIO.getReaderMIMETypes())
                .map(ImageIOTests::getReaderInfoByMIMEType)
                .forEach(System.out::println);


        System.out.println("WriterFormatNames:");
        Arrays.stream(ImageIO.getWriterFormatNames())
                .map(ImageIOTests::getWriterInfoByFormatName)
                .forEach(System.out::println);

        System.out.println("WriterFileSuffixes:");
        Arrays.stream(ImageIO.getWriterFileSuffixes())
                .map(ImageIOTests::getReaderInfoBySuffix)
                .forEach(System.out::println);

        System.out.println("WriterMIMETypes:");
        Arrays.stream(ImageIO.getWriterMIMETypes())
                .map(ImageIOTests::getWriterInfoByMIMEType)
                .forEach(System.out::println);
    }

    private static String getReaderInfoByFormatName(String formatName) {
        StringBuilder stringBuilder = new StringBuilder(formatName);
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName(formatName);
        while (imageReaderIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageReaderIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }

    private static String getReaderInfoBySuffix(String suffix) {
        StringBuilder stringBuilder = new StringBuilder(suffix);
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersBySuffix(suffix);
        while (imageReaderIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageReaderIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }

    private static String getReaderInfoByMIMEType(String mimeType) {
        StringBuilder stringBuilder = new StringBuilder(mimeType);
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByMIMEType(mimeType);
        while (imageReaderIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageReaderIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }

    private static String getWriterInfoByFormatName(String formatName) {
        StringBuilder stringBuilder = new StringBuilder(formatName);
        Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName(formatName);
        while (imageWriterIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageWriterIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }

    private static String getWriterInfoBySuffix(String suffix) {
        StringBuilder stringBuilder = new StringBuilder(suffix);
        Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersBySuffix(suffix);
        while (imageWriterIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageWriterIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }

    private static String getWriterInfoByMIMEType(String mimeType) {
        StringBuilder stringBuilder = new StringBuilder(mimeType);
        Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByMIMEType(mimeType);
        while (imageWriterIterator.hasNext()) {
            stringBuilder.append(", ");
            try {
                stringBuilder.append(imageWriterIterator.next().getClass().getName());
            } catch (Throwable ex) {
                stringBuilder.append(ex.toString());
            }
        }
        return stringBuilder.toString();
    }
}
