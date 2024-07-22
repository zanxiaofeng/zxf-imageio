package zxf.imageio;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SignatureMergeTests {
    public static Rectangle signatureBox = new Rectangle(426, 824, 957, 496);

    public static void main(String[] args) throws IOException {
        try (InputStream signatureStream = SignatureMergeTests.class.getResourceAsStream("/sign-3.png")) {
            BufferedImage signatureImage = ImageIO.read(signatureStream);

            try (InputStream templateStream = SignatureMergeTests.class.getResourceAsStream("/template.jpg")) {
                BufferedImage templateImage = ImageIO.read(templateStream);

                Graphics2D graphics2D = templateImage.createGraphics();
                Rectangle drawPos = calculateDrawPosition(signatureImage, signatureBox);
                graphics2D.drawImage(signatureImage, drawPos.x, drawPos.y, drawPos.width, drawPos.height, null);
                graphics2D.dispose();

                try (OutputStream tiffOutputStream = Files.newOutputStream(Paths.get("output/signed.png"))) {
                    ImageIO.write(templateImage, "png", tiffOutputStream);
                }
            }
        }
    }

    private static Rectangle calculateDrawPosition(BufferedImage signatureImage, Rectangle signatureBox) {
        double scale = Math.min(signatureBox.getWidth() / signatureImage.getWidth(), signatureBox.getHeight() / signatureImage.getHeight());
        if (signatureImage.getWidth() < signatureBox.getWidth() && signatureImage.getHeight() < signatureBox.getHeight()) {
            scale = 1.0f;
        }

        System.out.printf("(%f/%d, %f/%d), scale=%f", signatureBox.getWidth(),signatureImage.getWidth(),
                signatureBox.getHeight(), signatureImage.getHeight(), scale);

        double dstWidth = signatureImage.getWidth() * scale;
        double dstHeight = signatureImage.getHeight() * scale;
        double startX = signatureBox.getX() + (signatureBox.getWidth() - dstWidth) / 2;
        double startY = signatureBox.getY() + (signatureBox.getHeight() - dstHeight) / 2;

        return new Rectangle((int) startX, (int) startY, (int) dstWidth, (int) dstHeight);
    }
}
