package recognize;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRedShape {

    @TestFactory
    Collection<DynamicTest> dynamicTestsWithCollection() {
        return asList(
                DynamicTest.dynamicTest("hearts-blurry.png", () -> testImage("hearts-blurry.png", 1)),
                DynamicTest.dynamicTest("hearts-blurry2.png", () -> testImage("hearts-blurry2.png", 1)),
                DynamicTest.dynamicTest("diamonds-blurry.png", () -> testImage("diamonds-blurry.png", 2)),
                DynamicTest.dynamicTest("diamonds-blurry2.png", () -> testImage("diamonds-blurry2.png", 1))
        );
    }

    void testImage(String f, int expectedCountCurves) throws Exception {
        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(f));
        var c = new CurvesExtractor("", img);
        c.aroundRed.keySet().forEach(p -> {
            img.setRGB(p.x, p.y, Color.blue.getRGB());
        });
        for (Curve curve : c.finalCurves) {
            Images.drawPolygon(img, curve.curveLocation, Color.yellow);
        }

        File dir = new File("/tmp/imgDebug");
        dir.mkdirs();
        ImageIO.write(img, "png", new File(dir, "shape-" + f));

        int i = 0;
        for (Curve curve : c.finalCurves) {
            BufferedImage im = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            Images.drawPolygon(im, curve.curveLocation, Color.yellow);
            ImageIO.write(im, "png", new File(dir, "shape" + i + "-" + f));
            i++;
        }

        assertEquals(expectedCountCurves, c.finalCurves.size());
    }

    @Test
    void testBlur() throws Exception {
        File dir = new File("/tmp/imgDebug/testBlue");
        dir.mkdirs();

        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("hearts-blurry2.png"));

        BufferedImage colors = new BufferedImage(256 * 3 + (int) (256 * Math.sqrt(3)), img.getHeight(), img.getType());
        Graphics gcolors = colors.getGraphics();
        gcolors.fillRect(0, 0, colors.getWidth(), colors.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            int rgb = img.getRGB(185, y);
            var color = new Color(rgb);
            int fromRed = (int) Colors.distance(Color.red.getRGB(), rgb);
            colors.setRGB(color.getRed(), y, Color.red.getRGB());
            colors.setRGB(256 + color.getGreen(), y, Color.green.getRGB());
            colors.setRGB(256 * 2 + color.getBlue(), y, Color.blue.getRGB());
            colors.setRGB(256 * 3 + fromRed, y, Color.blue.getRGB());

            colors.setRGB(256, y, Color.black.getRGB());
            colors.setRGB(256 * 2, y, Color.black.getRGB());
            colors.setRGB(256 * 3, y, Color.black.getRGB());
        }
        ImageIO.write(colors, "png", new File(dir, "hearts-blurry2-colors.png"));

        var c = new CurvesExtractor("", img);
        for (int y = 0; y < img.getHeight(); y++) {
            img.setRGB(185, y, Color.gray.getRGB());
        }
        c.aroundRed.keySet().forEach(p -> {
            img.setRGB(p.x, p.y, Color.blue.getRGB());
        });
        ImageIO.write(img, "png", new File(dir, "hearts-blurry2.png"));

        long count = c.aroundRed.keySet().stream().filter(p -> p.x == 185 && (p.y >= 250 && p.y <= 265)).count();
        assertNotEquals(0L, count);
    }

}