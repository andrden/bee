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

public class TestRedShape {

    @TestFactory
    Collection<DynamicTest> dynamicTestsWithCollection() {
        return asList(
                DynamicTest.dynamicTest("hearts-blurry.png", () -> testImage("hearts-blurry.png", 1)),
                DynamicTest.dynamicTest("diamonds-blurry.png", () -> testImage("diamonds-blurry.png", 2))
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

}