package recognize.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class Curve {
    public List<XY> curveLocation;
    public List<XY> curveRescaled;
    public int[] lines;

    public Curve(List<XY> curveLocation, List<XY> curveRescaled, int[] lines) {
        this.curveLocation = curveLocation;
        this.curveRescaled = curveRescaled;
        // normalize lines descriptor
        int sum = Arrays.stream(lines).sum();
        int target = 100 * lines.length;
        this.lines = Arrays.stream(lines).map(v -> v * target / sum).toArray();
    }

    public double profileDistance(Curve other) {
        double sum = 0;
        for (int i = 0; i < lines.length; i++) {
            sum += Math.pow(lines[i] - other.lines[i], 2);
        }
        return Math.sqrt(sum / lines.length);
    }

    public void drawDescriptorLines(BufferedImage img, XY origin) {
        for (int line = 0; line < 100; line++) {
            Images.line(img, new XY(origin.x, origin.y + line), new XY(origin.x + lines[line], origin.y + line), Color.lightGray);
        }
    }
}
