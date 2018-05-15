package recognize;

import java.util.Arrays;
import java.util.List;

public class Curve {
    List<XY> curveLocation;
    List<XY> curveRescaled;
    int[] lines;

    public Curve(List<XY> curveLocation, List<XY> curveRescaled, int[] lines) {
        this.curveLocation = curveLocation;
        this.curveRescaled = curveRescaled;
        // normalize lines descriptor
        int sum = Arrays.stream(lines).sum();
        int target = 100 * lines.length;
        this.lines = Arrays.stream(lines).map(v -> v * target / sum).toArray();
    }

    double profileDistance(Curve other) {
        double sum = 0;
        for (int i = 0; i < lines.length; i++) {
            sum += Math.pow(lines[i] - other.lines[i], 2);
        }
        return Math.sqrt(sum / lines.length);
    }
}
