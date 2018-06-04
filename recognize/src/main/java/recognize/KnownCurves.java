package recognize;

import recognize.util.Curve;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class KnownCurves {
    List<CurvesExtractor> known = new ArrayList<>();

    public KnownCurves() {
        for (var n : asList("diamonds", "hearts", "9")) {
            try {
                known.add(new CurvesExtractor(n, ImageIO.read(getClass().getClassLoader().getResourceAsStream(
                        "cards/" + n + ".png"))));
            } catch (IOException e) {
                throw new RuntimeException("" + n, e);
            }
        }
    }

    public void recognize(String id, Curve curve){
        var knownDistances = known.stream().collect(Collectors.toMap(k -> k.name, k -> k.finalCurves.get(0).profileDistance(curve)));
        known.forEach(k -> System.out.println(id + " distance " + k.name + " " + knownDistances.get(k.name).intValue()));
    }
}