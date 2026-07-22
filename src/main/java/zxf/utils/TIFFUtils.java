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
                IIOMetadata metadata = buildMetadata(multiPageTiffWriter, image, param, copyright, description, dpi);
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


    private static TIFFImageWriteParam buildWriteParam(TIFFImageWriter writer, String compressionType) {
        TIFFImageWriteParam param = (TIFFImageWriteParam) writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(compressionType);
        param.setCompressionQuality(1.0f);
        return param;
    }

    /**
     * Builds TIFF metadata for the given image, including copyright, description, and resolution (DPI).
     * Bits per sample, photometric interpretation, and other pixel-format fields are left to the writer,
     * which derives them from the image type (setting them manually is ignored / overridden).
     *
     * @param writer      the TIFF image writer
     * @param image       the image to generate metadata for
     * @param param       the write parameters (used to produce default metadata)
     * @param copyright   optional copyright string (skipped if null)
     * @param description optional image description (skipped if null)
     * @param dpi         resolution in dots per inch (applied to both X and Y)
     * @return merged IIOMetadata ready to be attached to the image
     */
    private static IIOMetadata buildMetadata(TIFFImageWriter writer, BufferedImage image, TIFFImageWriteParam param, String copyright, String description, int dpi) throws IIOInvalidTreeException {
        // Get default metadata from writer based on image type and compression settings
        IIOMetadata baseMetadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
        // Wrap into TIFFDirectory for easy field manipulation
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(baseMetadata);

        // Optional: Copyright tag (TIFF tag 33432)
        if (copyright != null) {
            dir.addTIFFField(new TIFFField(
                    BASELINE.getTag(BaselineTIFFTagSet.TAG_COPYRIGHT),
                    TIFFTag.TIFF_ASCII, 1, new String[]{copyright}
            ));
        }

        // Optional: Image description tag (TIFF tag 270)
        if (description != null) {
            dir.addTIFFField(new TIFFField(
                    BASELINE.getTag(BaselineTIFFTagSet.TAG_IMAGE_DESCRIPTION),
                    TIFFTag.TIFF_ASCII, 1, new String[]{description}
            ));
        }

        // X/Y resolution as RATIONAL (numerator/denominator), e.g. 300/1 = 300 DPI
        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
                TIFFTag.TIFF_RATIONAL, 1, new long[][]{{dpi, 1}}
        ));
        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
                TIFFTag.TIFF_RATIONAL, 1, new long[][]{{dpi, 1}}
        ));
        // Resolution unit: 2 = inch (for DPI)
        dir.addTIFFField(new TIFFField(
                BASELINE.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                TIFFTag.TIFF_SHORT, 1, new char[]{2}
        ));
        // Bits per sample, photometric interpretation, and other pixel-format fields are left to the
        // writer, which derives them from the image type (setting them manually is ignored / overridden).

        // Merge TIFF fields back into the base metadata tree
        baseMetadata.mergeTree(FORMAT_NAME, dir.getAsMetadata().getAsTree(FORMAT_NAME));
        return baseMetadata;
    }
}
