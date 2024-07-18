package zxf.imageio;

import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageIOChecker {
    public static void main(String[] args) throws IOException {
        Path tiffFilePath = Paths.get("output/developer-mozilla-org-CORS-zh.tiff");
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(tiffFilePath.toFile())) {
            ImageReader reader = ImageIO.getImageReaders(imageInputStream).next();
            reader.setInput(imageInputStream);
            System.out.println("Format: " + reader.getFormatName());
            System.out.println("Pages : " + reader.getNumImages(true));

            for (int i = 0; i < reader.getNumImages(true); i++) {
                System.out.println(i + ", " + reader.getHeight(i) + " * " + reader.getWidth(i));
                System.out.println(i + ", " + reader.getImageMetadata(i).getNativeMetadataFormatName());
                System.out.println(i + ", " + String.join(", ", reader.getImageMetadata(i).getMetadataFormatNames()));
                if (reader.getImageMetadata(i).getExtraMetadataFormatNames() != null) {
                    System.out.println(i + ", " + String.join(", ", reader.getImageMetadata(i).getExtraMetadataFormatNames()));
                }

                IIOMetadataNode metadataNode = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
                traverseMetadataNode(metadataNode, "");
            }
        }
    }

    private static void traverseMetadataNode(IIOMetadataNode metadataNode, String indent) {
        System.out.println(indent + "Node name = " + metadataNode.getNodeName() + " [OPEN]");

        for (int i = 0; i < metadataNode.getAttributes().getLength(); i++) {
            Node attr = metadataNode.getAttributes().item(i);
            System.out.println(indent + "[" + attr.getNodeName() + " = " + attr.getNodeValue() + "]");
        }

        for (int j = 0; j < metadataNode.getChildNodes().getLength(); j++) {
            IIOMetadataNode currentNode = (IIOMetadataNode) metadataNode.getChildNodes().item(j);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                traverseMetadataNode(currentNode, indent + "  ");
            }
        }
    }
}
