package recognize;

import org.junit.jupiter.api.Disabled;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRedShape {

    //@Disabled
    @TestFactory
    Collection<DynamicTest> dynamicTestsWithCollection() {
        return asList(
                DynamicTest.dynamicTest("9-blurry3.png", () -> testImage("9-blurry3.png", 2)),
                DynamicTest.dynamicTest("9-blurry2.png", () -> testImage("9-blurry2.png", 1)),
                DynamicTest.dynamicTest("9-blurry.png", () -> testImage("9-blurry.png", 1)),
                DynamicTest.dynamicTest("hearts-blurry.png", () -> testImage("hearts-blurry.png", 1)),
                DynamicTest.dynamicTest("hearts-blurry2.png", () -> testImage("hearts-blurry2.png", 1)),
                DynamicTest.dynamicTest("diamonds-blurry.png", () -> testImage("diamonds-blurry.png", 2)),
                DynamicTest.dynamicTest("diamonds-blurry2.png", () -> testImage("diamonds-blurry2.png", 1))
        );
    }

    Histogram histo(Map<XY, Histogram> histograms) {
        assertEquals(1, histograms.size());
        return histograms.values().iterator().next();
    }

    void testImage(String f, int expectedCountCurves) throws Exception {
        File dir = new File("/tmp/imgDebug");
        dir.mkdirs();

        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(f));
        var c = new CurvesExtractor("", img);
        Histogram histogram = histo(c.histograms);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                final int rgb = img.getRGB(x, y);
                final double fromRed = Colors.distance(Color.red.getRGB(), rgb);
                int idx = histogram.getIdx(fromRed);
                long pos = 2 * histogram.separators.stream().filter(s -> s < idx).count()
                        + histogram.separators.stream().filter(s -> s == idx).count();
                int gray = (int) pos * 255 / (histogram.separators.size() * 2 + 1);
                img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
                if(idx>=25 && idx<=28) img.setRGB(x, y, Color.GREEN.getRGB());
//                if(histogram.getIdx(fromRed)<histogram.minPos) img.setRGB(x,y,Color.WHITE.getRGB());
//                if(histogram.getIdx(fromRed)>histogram.minPos) img.setRGB(x,y,Color.BLACK.getRGB());
//                if(histogram.getIdx(fromRed)==histogram.minPos) img.setRGB(x,y,Color.RED.getRGB());
            }
        }

//        c.aroundRed.keySet().forEach(p -> {
//            img.setRGB(p.x, p.y, Color.blue.getRGB());
//        });
//        for (Curve curve : c.finalCurves) {
//            Images.drawPolygon(img, curve.curveLocation, Color.yellow);
//        }
//        img.setRGB(200, 128, Color.GREEN.getRGB());

        ImageIO.write(img, "png", new File(dir, "shape-" + f));

        int i = 0;
        for (Curve curve : c.finalCurves) {
            BufferedImage im = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            Images.drawPolygon(im, curve.curveLocation, Color.yellow);
            ImageIO.write(im, "png", new File(dir, "shape-" + f + "." + i + ".png"));
            i++;
        }

        assertEquals(expectedCountCurves, c.finalCurves.size());
    }

    @TestFactory
    Collection<DynamicTest> dynamicTestsBlur() {
        return asList(
                DynamicTest.dynamicTest("hearts-blurry2.png 185", () -> testBlur(185, 250, 265)),
                DynamicTest.dynamicTest("hearts-blurry2.png 235", () -> testBlur(235, 260, 280))
        );
    }

    void testBlur(final int xline, int y1, int y2) throws Exception {
        File dir = new File("/tmp/imgDebug/testBlur");
        dir.mkdirs();

        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("hearts-blurry2.png"));

        BufferedImage colors = new BufferedImage(256 * 3 + (int) (256 * Math.sqrt(3)), img.getHeight(), img.getType());
        Graphics gcolors = colors.getGraphics();
        gcolors.fillRect(0, 0, colors.getWidth(), colors.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            int rgb = img.getRGB(xline, y);
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
        ImageIO.write(colors, "png", new File(dir, xline + "hearts-blurry2-colors.png"));

        var c = new CurvesExtractor("", img);
        for (int y = 0; y < img.getHeight(); y++) {
            img.setRGB(xline, y, Color.gray.getRGB());
        }
        c.aroundRed.keySet().forEach(p -> {
            img.setRGB(p.x, p.y, Color.blue.getRGB());
        });
        ImageIO.write(img, "png", new File(dir, xline + "hearts-blurry2.png"));

        long count = c.aroundRed.keySet().stream().filter(p -> p.x == xline && (p.y >= y1 && p.y <= y2)).count();
        assertNotEquals(0L, count);
    }

}