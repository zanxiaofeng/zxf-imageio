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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

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
                for (String format : reader.getImageMetadata(i).getMetadataFormatNames()) {
                    IIOMetadataNode metadataNode = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(format);
                    traverseMetadataNode(metadataNode, "");
                }
            }
        }
    }

    private static void traverseMetadataNode(IIOMetadataNode metadataNode, String indent) {
        String attrStr = IntStream.range(0, metadataNode.getAttributes().getLength())
                .mapToObj(metadataNode.getAttributes()::item)
                .map(item -> format("%s=%s", item.getNodeName(), item.getNodeValue()))
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.printf("%s%s%s [OPEN]\n", indent, metadataNode.getNodeName(), attrStr.length() == 2 ? "" : attrStr);

        for (int j = 0; j < metadataNode.getChildNodes().getLength(); j++) {
            IIOMetadataNode currentNode = (IIOMetadataNode) metadataNode.getChildNodes().item(j);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                traverseMetadataNode(currentNode, indent + "  ");
            }
        }
    }
}
