package recognize;

import org.apache.commons.lang3.tuple.Pair;
import recognize.util.Curve;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class KnownCurves {
    List<CurvesExtractor> known = new ArrayList<>();

    public KnownCurves() {
        for (var n : asList("diamonds", "hearts", "9", "8")) {
            try {
                CurvesExtractor e = new CurvesExtractor(n, ImageIO.read(getClass().getClassLoader().getResourceAsStream(
                        "cards/" + n + ".png")));
                e.extract(true);
                known.add(e);
            } catch (IOException e) {
                throw new RuntimeException("" + n, e);
            }
        }
    }

    public List<RecognizeResult> recognize(String id, Curve curve) {
        List<RecognizeResult> ret = new ArrayList<>();
        for (CurvesExtractor ce : known) {
            var best = ce.finalCurves.stream()
                    .map(c -> Pair.of(c,c.profileDistance(curve)))
                    .sorted(Comparator.comparingDouble(p -> p.getRight()))
                    .findFirst()
                    .get();
            System.out.println(id + " distance " + ce.name + " " + best.getRight().intValue());
            ret.add(new RecognizeResult(ce.name, best.getRight().intValue(), best.getLeft()));
        }
//        var knownDistances = known.stream().collect(Collectors.toMap(k -> k.name, k -> k.finalCurves.get(0).profileDistance(curve)));
//        known.forEach(k -> System.out.println(id + " distance " + k.name + " " + knownDistances.get(k.name).intValue()));
        ret.sort(Comparator.comparingInt(rr -> rr.distance));
        return ret;
    }
}
