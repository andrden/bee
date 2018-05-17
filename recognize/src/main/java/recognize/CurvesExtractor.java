package recognize;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CurvesExtractor {
    String name;
    BufferedImage image;

    Map<XY, XY> aroundRed = new HashMap<>(); // XY -> shifted XY, where red shift is most pronounced
    //Set<XY> aroundRedAvg = new HashSet<>();
    Index index;

    Set<XY> excludedByCurves = new HashSet<>();
    List<Curve> finalCurves = new ArrayList<>();

    public CurvesExtractor(String name, BufferedImage image) {
        this.name = name;
        this.image = image;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double maxStepToRed = 16; // lesser steps ignored
                XY bestStep = null;
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 6) {
                    int rgb = image.getRGB(x, y);
                    double fromRed = Colors.distance(Color.red.getRGB(), rgb);
                    XY step = checkBounds(new XY(x, y).shiftBy(5, phi));
                    if (step != null) {
                        int rgbStep = image.getRGB(step.x, step.y);
                        if (rgbStep != rgb) {
                            double stepFromRed = Colors.distance(Color.red.getRGB(), rgbStep);
                            double cosine = Colors.cosineColorVectors(rgb, rgbStep, Color.red.getRGB());
                            double stepToRed = fromRed - stepFromRed;
                            if (cosine > 0.8) {
                                if (maxStepToRed < stepToRed) {
                                    maxStepToRed = stepToRed;
                                    bestStep = step;
                                }
                            }
                        }
                    }
                }
                if (bestStep != null) {
                    aroundRed.put(new XY(x, y), bestStep);
                }
            }
        }
        index = new Index(aroundRed.keySet());
//        computeAvg();
        List<List<XY>> curves = new ArrayList<>();
        for (; ; ) {
            List<XY> curve = computeEdges(excludedByCurves);
            if (curve == null) break;
            System.out.println("======================");
            curves.add(curve);
            //curve.forEach(p -> excludedByCurves.addAll(index.around(p)));
            curve.forEach(p -> image.setRGB(p.x, p.y, Color.green.getRGB()));
        }

        for (int ci = 0; ci < curves.size(); ci++) {
            var curve = curves.get(ci);
            var curveLocation = curve;
            //curve = new Inertia(curve).alignedCurve();
            XY curveMin = XY.min(curve);
            XY curveMax = XY.max(curve);
            if (curveMax.x - curveMin.x < 20) continue;

            curve = XY.rescaleHeight(curve, 100);
            curveMin = XY.min(curve);
            curveMax = XY.max(curve);

            int width = curveMax.x - curveMin.x + 1;
            BufferedImage sub = new BufferedImage(2 * width, curveMax.y - curveMin.y + 1, image.getType());
            Images.fillPolygon(sub, curve, Color.red);
            int[] lines = new int[100];
            for (int line = 0; line < 100; line++) {
                int count = 0;
                for (int x = 0; x < width; x++) {
                    if (sub.getRGB(x, line) == Color.red.getRGB()) count++;
                }
                lines[line] = count;
            }
            finalCurves.add(new Curve(curveLocation, curve, lines));
        }

    }

    XY checkBounds(XY p) {
        if (p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight()) return p;
        return null;
    }

    List<XY> computeEdges(Set<XY> excluded) {
        //XY start = aroundRed.keySet().iterator().next();
        //XY start = new ArrayList<>(aroundRed.keySet()).get(10000);
        for (; ; ) {
            XY start = aroundRed.keySet().stream().filter(p -> !excluded.contains(p)).findFirst().orElse(null);
            if (start == null) return null;
            List<XY> curve = traceCurve(excluded, start);
            if (curve != null) return curve;
        }
    }

    private List<XY> traceCurve(Set<XY> excluded, XY start) {
        var used = new HashMap<XY, Integer>();
        var ret = new ArrayList<XY>();
        Set<XY> localExcluded = new HashSet<>();
        for (int i = 0; ; i++) {
            System.out.println(i + " " + start);
            ret.add(start);
            used.put(start, i);
            localExcluded.addAll(index.around(start));
            XY oldStart = start;

            var ii = i;
            Optional<XY> end = index.around(start).stream()
                    .filter(ps -> used.containsKey(ps) && used.get(ps) < ii - 30)
                    .filter(ps -> oldStart.distanceSq(ps) < 5 * 5)
                    .min(Comparator.comparingDouble(ps -> ps.distanceSq(oldStart)));
            if (end.isPresent()) {
                excluded.addAll(localExcluded);
                return ret.subList(used.get(end.get()), ret.size());
            }

            XY vectorToRed = XY.average(index.around(start).stream()
                    .filter(ps -> oldStart.distanceSq(ps) < 5 * 5)
                    .map(ps -> aroundRed.get(ps).subtract(ps))
                    .collect(Collectors.toList()));
            XY shiftPoint = start.copy();
            shiftPoint.add(vectorToRed.vectorTurnLeft90());

            //XY stepToRed = aroundRed.get(start);
            //XY leftPoint = start.vectorTurnLeft90(stepToRed);
            //                    .filter(ps -> cosineVectors(oldStart, shiftPoint, ps) > 0.8)
            //                    .sorted(Comparator.comparingDouble(ps -> ps.distanceSq(oldStart)))
            start = index.around(start).stream()
                    .filter(ps -> !used.containsKey(ps))
                    .filter(p -> !excluded.contains(p))
                    .filter(ps -> oldStart.distanceSq(ps) < 5 * 5)
                    .min(Comparator.comparingDouble(ps -> -cosineVectors(oldStart, shiftPoint, ps)))
                    .orElse(null);
            if (start == null){
                excluded.addAll(localExcluded);
                return null;
            }
        }
    }

    double cosineVectors(XY from, XY to1, XY to2) {
        XY vector1 = new XY(to1.x - from.x, to1.y - from.y);
        XY vector2 = new XY(to2.x - from.x, to2.y - from.y);
        double norm1sq = vector1.scalarMult(vector1);
        double norm2sq = vector2.scalarMult(vector2);
        double scalar = vector1.scalarMult(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
    }


//    void computeAvg() {
//        for (XY p : aroundRed.keySet()) {
//            List<XY> near = index.get(new XY(p.x / 10 * 10, p.y / 10 * 10)).stream()
//                    .filter(ps -> Math.pow(p.x - ps.x, 2) + Math.pow(p.y - ps.y, 2) < 10 * 10)
//                    .filter(ps -> Math.abs(cosineVectors(p, aroundRed.get(p), ps)) > 0.8)
//                    .collect(Collectors.toList());
//            if (near.size() > 0) {
//                XY psum = new XY(0, 0);
//                near.forEach(psum::add);
//                XY pavg = new XY(psum.x / near.size(), psum.y / near.size());
//                if( Math.pow(pavg.x-p.x,2)+Math.pow(pavg.y-p.y,2)<2*2) {
//                    aroundRedAvg.add(pavg);
//                }
//            }
//        }
//    }

}
