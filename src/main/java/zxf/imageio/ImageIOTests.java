package zxf.imageio;

import javax.imageio.ImageIO;
import java.util.Arrays;

public class ImageIOTests {
    public static void main(String[] args) {
        System.out.println("ReaderFormatNames:");
        Arrays.stream(ImageIO.getReaderFormatNames()).forEach(System.out::println);

        System.out.println("ReaderFileSuffixes:");
        Arrays.stream(ImageIO.getReaderFileSuffixes()).forEach(System.out::println);

        System.out.println("ReaderMIMETypes:");
        Arrays.stream(ImageIO.getReaderMIMETypes()).forEach(System.out::println);


        System.out.println("WriterFormatNames:");
        Arrays.stream(ImageIO.getWriterFormatNames()).forEach(System.out::println);

        System.out.println("WriterFileSuffixes:");
        Arrays.stream(ImageIO.getWriterFileSuffixes()).forEach(System.out::println);

        System.out.println("WriterMIMETypes:");
        Arrays.stream(ImageIO.getWriterMIMETypes()).forEach(System.out::println);
    }
}
