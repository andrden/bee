package recognize;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRedShape {
    @Test
    void test1() throws Exception {
        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("hearts-blurry.png"));
        var c = new CurvesExtractor("", img);
        c.aroundRed.keySet().forEach(p -> {
            img.setRGB(p.x, p.y, Color.blue.getRGB());
        });
        File dir = new File("/tmp/imgDebug");
        dir.mkdirs();
        ImageIO.write(img, "png", new File(dir, "hearts-blurry-shape.png"));
        assertEquals(1, c.finalCurves.size());
    }
}