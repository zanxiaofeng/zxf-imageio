package zxf.utils;

import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriteParam;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriterSpi;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

/**
 * TIFF utility using TwelveMonkeys ImageIO library.
 * Provides the same functionality as TIFFUtils (which uses JAI ImageIO).
 */
public class TIFFUtils2 {

    public static void writeTIFF(List<BufferedImage> images, String compressionType, File output, String copyright, String description, int dpi) throws IOException {
        ImageWriter writer = new TIFFImageWriterSpi().createWriterInstance();
        TIFFImageWriteParam param = buildWriteParam(writer, compressionType);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            writer.setOutput(ios);

            writer.prepareWriteSequence(null);
            for (BufferedImage image : images) {
                IIOMetadata metadata = buildMetadata(writer, image, param, copyright, description, dpi);
                writer.writeToSequence(new IIOImage(image, null, metadata), param);
            }
            writer.endWriteSequence();
        } finally {
            writer.dispose();
        }
    }

    public static void writeTIFF(BufferedImage image, String compressionType, File output, String copyright, String description, int dpi) throws IOException {
        ImageWriter writer = new TIFFImageWriterSpi().createWriterInstance();
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

    private static TIFFImageWriteParam buildWriteParam(ImageWriter writer, String compressionType) {
        TIFFImageWriteParam param = (TIFFImageWriteParam) writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(compressionType);
        param.setCompressionQuality(1.0f);
        return param;
    }

    /**
     * Builds TIFF metadata using TwelveMonkeys API, including copyright, description, and resolution (DPI).
     * Bits per sample, photometric interpretation, and other pixel-format fields are left to the writer,
     * which derives them from the image type (setting them manually is ignored / overridden).
     *
     * @param writer      the ImageWriter for TIFF format
     * @param image       the image to generate metadata for
     * @param param       the write parameters (used to produce default metadata)
     * @param copyright   optional copyright string (skipped if null)
     * @param description optional image description (skipped if null)
     * @param dpi         resolution in dots per inch (applied to both X and Y)
     * @return TIFFImageMetadata ready to be attached to the image
     */
    private static IIOMetadata buildMetadata(ImageWriter writer, BufferedImage image, TIFFImageWriteParam param, String copyright, String description, int dpi) {
        // Collect all TIFF entries
        List<Entry> entries = new java.util.ArrayList<>();

        // Optional: Copyright tag (TIFF tag 33432)
        if (copyright != null) {
            entries.add(new TIFFEntry(TIFF.TAG_COPYRIGHT, copyright));
        }

        // Optional: Image description tag (TIFF tag 270)
        if (description != null) {
            entries.add(new TIFFEntry(TIFF.TAG_IMAGE_DESCRIPTION, description));
        }

        // X/Y resolution as RATIONAL (numerator/denominator), e.g. 300/1 = 300 DPI
        entries.add(new TIFFEntry(TIFF.TAG_X_RESOLUTION, new Rational(dpi, 1)));
        entries.add(new TIFFEntry(TIFF.TAG_Y_RESOLUTION, new Rational(dpi, 1)));
        // Resolution unit: 2 = inch (for DPI)
        entries.add(new TIFFEntry(TIFF.TAG_RESOLUTION_UNIT, (short) 2));

        return new TIFFImageMetadata(entries);
    }
}
