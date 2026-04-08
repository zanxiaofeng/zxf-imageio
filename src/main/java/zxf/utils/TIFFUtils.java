package zxf.utils;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;
import com.github.jaiimageio.plugins.tiff.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

public class TIFFUtils {
    private static final BaselineTIFFTagSet BASELINE = BaselineTIFFTagSet.getInstance();
    private static final String FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0";

    public static void writeTIFF(List<BufferedImage> images, String compressionType, File output, String copyright, String description, int dpi) throws IOException {
        TIFFImageWriter multiPageTiffWriter = new TIFFImageWriter(new TIFFImageWriterSpi());
        TIFFImageWriteParam param = buildWriteParam(multiPageTiffWriter, compressionType);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            multiPageTiffWriter.setOutput(ios);

            multiPageTiffWriter.prepareWriteSequence(null);
            for (BufferedImage image : images) {
                IIOMetadata metadata = buildMetadata(multiPageTiffWriter, images.getFirst(), param, copyright, description, dpi);
                multiPageTiffWriter.writeToSequence(new IIOImage(image, null, metadata), param);
            }
            multiPageTiffWriter.endWriteSequence();
        } finally {
            multiPageTiffWriter.dispose();
        }
    }

    public static void writeTIFF(BufferedImage image, String compressionType, File output, String copyright, String description, int dpi) throws IOException {
        TIFFImageWriter writer = new TIFFImageWriter(new TIFFImageWriterSpi());
        TIFFImageWriteParam param = buildWriteParam(writer, compressionType);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            writer.setOutput(ios);

            IIOMetadata metadata = buildMetadata(writer, image, param, copyright, description, dpi);
            writer.write(null, new IIOImage(image, null, metadata), param);
        } finally {
            writer.dispose();
        }
    }

    public static void check(File input) throws IOException {
        try (FileInputStream fis = new FileInputStream(input)) {
            byte[] header = new byte[2];
            fis.read(header);
            String order = new String(header, "ASCII");
            System.out.println("Byte order: " + order + (order.equals("II") ? " (Little Endian)" : order.equals("MM") ? " (Big Endian)" : " (Unknown)"));
        }
    }

    private static TIFFImageWriteParam buildWriteParam(TIFFImageWriter writer, String compressionType) {
        TIFFImageWriteParam param = (TIFFImageWriteParam) writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(compressionType);
        param.setCompressionQuality(1.0f);
        return param;
    }

    private static IIOMetadata buildMetadata(TIFFImageWriter writer, BufferedImage image, TIFFImageWriteParam param, String copyright, String description, int dpi) throws IIOInvalidTreeException {
        IIOMetadata baseMetadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(baseMetadata);

        if (copyright != null) {
            dir.addTIFFField(new TIFFField(
                    BASELINE.getTag(BaselineTIFFTagSet.TAG_COPYRIGHT),
                    TIFFTag.TIFF_ASCII, 1, new String[]{copyright}
            ));
        }

        if (description != null) {
            dir.addTIFFField(new TIFFField(
                    BASELINE.getTag(BaselineTIFFTagSet.TAG_IMAGE_DESCRIPTION),
                    TIFFTag.TIFF_ASCII, 1, new String[]{description}
            ));
        }

        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
                TIFFTag.TIFF_RATIONAL, 1, new long[][]{{dpi, 1}}
        ));
        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
                TIFFTag.TIFF_RATIONAL, 1, new long[][]{{dpi, 1}}
        ));
        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                TIFFTag.TIFF_SHORT, 1, new char[]{2}
        ));

        baseMetadata.mergeTree(FORMAT_NAME, dir.getAsMetadata().getAsTree(FORMAT_NAME));
        return baseMetadata;
    }
}
