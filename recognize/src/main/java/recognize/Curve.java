package recognize;

import java.util.List;

public class Curve {
    List<XY> curve;
    int[] lines;

    public Curve(List<XY> curve, int[] lines) {
        this.curve = curve;
        this.lines = lines; - need normalize lines descriptor?
    }

    double profileDistance(Curve other) {
        double sum = 0;
        for (int i = 0; i < lines.length; i++) {
            sum += Math.pow(lines[i] - other.lines[i], 2);
        }
        return Math.sqrt(sum / lines.length);
    }
}
