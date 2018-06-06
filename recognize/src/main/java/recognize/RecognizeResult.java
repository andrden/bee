package recognize;

import recognize.util.Curve;

import java.util.Map;

public class RecognizeResult {
    public String name;
    int distance;
    public Curve curve;

    public RecognizeResult(String name, int distance, Curve curve) {
        this.name = name;
        this.distance = distance;
        this.curve = curve;
    }

    @Override
    public String toString() {
        return "RecognizeResult{" +
                "name='" + name + '\'' +
                ", distance=" + distance +
                '}';
    }
}
