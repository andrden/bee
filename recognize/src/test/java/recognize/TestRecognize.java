package recognize;

import org.junit.jupiter.api.Test;
import recognize.util.Curve;
import recognize.util.Images;
import recognize.util.Inertia;
import recognize.util.XY;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRecognize {

    @Test
    void test1() throws Exception {
        File dir = new File("/tmp/imgDebug/recognize");
        dir.mkdirs();

        KnownCurves knownCurves = new KnownCurves();
        BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("recognize/diamond1.png"));
        var curvesExtractor = new CurvesExtractor("", img);
        curvesExtractor.extract();
        var curve = curvesExtractor.finalCurves.get(0);
        List<RecognizeResult> recognizeResults = knownCurves.recognize("", curve);

        BufferedImage sub = new BufferedImage(500, 500, img.getType());
        Images.fillPolygon(sub, curve.curveRescaled, Color.red);
        curve.drawDescriptorLines(sub, new XY(300,0));
        for (int i = 0; i < recognizeResults.size(); i++) {
            System.out.println(recognizeResults.get(i));
            Curve curveI = recognizeResults.get(i).curve;
            Images.fillPolygon(sub,
                    XY.shift(curveI.curveRescaled, new XY(0, 100 * (i + 1))),
                    Color.red);
            curveI.drawDescriptorLines(sub, new XY(300,100 * (i + 1)));
        }
        ImageIO.write(sub, "png", new File(dir, "diamond1-shape.png"));

        assertEquals("diamonds", recognizeResults.get(0).name);
    }
}