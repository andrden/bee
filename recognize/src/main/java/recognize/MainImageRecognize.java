package recognize;

import com.google.common.collect.HashMultiset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainImageRecognize {
    public static void main(String[] args) throws Exception {
        new MainImageRecognize().run();
    }

    Random rand = new Random(1);
    BufferedImage image;
    Map<XY, XY> aroundRed = new HashMap<>(); // XY -> shifted XY, where red shift is most pronounced
    //Set<XY> aroundRedAvg = new HashSet<>();
    Index index;

    void run() throws Exception {
        image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("cards-crop1.png"));
        String outFilesPrefix = "/home/denny/proj/bee/recognize/card";
        System.out.println(image.getWidth() + " x " + image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double maxStepToRed = 32; // lesser steps ignored
                XY bestStep = null;
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 6) {
                    int rgb = image.getRGB(x, y);
                    double fromRed = distance(Color.red.getRGB(), rgb);
                    XY step = checkBounds(new XY(x, y).shiftBy(5, phi));
                    if (step != null) {
                        int rgbStep = image.getRGB(step.x, step.y);
                        if (rgbStep != rgb) {
                            double stepFromRed = distance(Color.red.getRGB(), rgbStep);
                            double cosine = cosineColorVectors(rgb, rgbStep, Color.red.getRGB());
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

        aroundRed.keySet().forEach(p -> {
            image.setRGB(p.x, p.y, Color.blue.getRGB());
        });
//        aroundRed.keySet().forEach(p -> {
//            if(rand.nextDouble()<0.04) line(image, p, aroundRed.get(p), Color.green);
//        });
        Set<XY> excludedByCurves = new HashSet<>();
        List<List<XY>> curves = new ArrayList<>();
        for (; ; ) {
            List<XY> curve = computeEdges(excludedByCurves);
            if (curve == null) break;
            System.out.println("======================");
            curves.add(curve);
            //curve.forEach(p -> excludedByCurves.addAll(index.around(p)));
            curve.forEach(p -> image.setRGB(p.x, p.y, Color.green.getRGB()));
        }
//        aroundRedAvg.forEach(p -> image.setRGB(p.x, p.y, Color.green.getRGB()));
        //excludedByCurves.forEach(p -> image.setRGB(p.x, p.y, Color.yellow.getRGB()));
        ImageIO.write(image, "png", new File(outFilesPrefix + ".png"));

        for (int ci = 0; ci < curves.size(); ci++) {
            var curve = curves.get(ci);
            //curve = new Inertia(curve).alignedCurve();
            XY curveMin = XY.min(curve);
            XY curveMax = XY.max(curve);
            if (curveMax.x - curveMin.x < 20) continue;

            curve = XY.rescaleHeight(curve, 100);
            curveMin = XY.min(curve);
            curveMax = XY.max(curve);

            int width = curveMax.x - curveMin.x + 1;
            BufferedImage sub = new BufferedImage(2 * width, curveMax.y - curveMin.y + 1, image.getType());
            polygon(sub, curve, Color.red);
            for (int line = 0; line < 100; line++) {
                int count = 0;
                for (int x = 0; x < width; x++) {
                    if (sub.getRGB(x, line) == Color.red.getRGB()) count++;
                }
                line(sub, new XY(width, line), new XY(width + count, line), Color.lightGray);
            }
            //            for (int i = 1; i < curve.size(); i++) {
//                line(sub, curve.get(i - 1).subtract(curveMin), curve.get(i).subtract(curveMin), Color.red);
//            }
            for (int i = 0; i < curve.size(); i++) {
                sub.setRGB(curve.get(i).x - curveMin.x, curve.get(i).y - curveMin.y, Color.blue.getRGB());
            }
            ImageIO.write(sub, "png", new File(outFilesPrefix + "sub" + ci + ".png"));
        }

        //displayRedScaledSteps(image);
    }

    void polygon(BufferedImage image, List<XY> curve, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.setPaint(color);
        Polygon p = new Polygon();
        curve.forEach(i -> p.addPoint(i.x, i.y));
        g.fillPolygon(p);
    }

    void line(BufferedImage image, XY from, XY to, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        BasicStroke bs = new BasicStroke(1);
        g.setStroke(bs);
        g.drawLine(from.x, from.y, to.x, to.y);
        //        for( int i=0; i<=points; i++){
//
//        }
    }

    List<XY> computeEdges(Set<XY> excluded) {
        //XY start = aroundRed.keySet().iterator().next();
        //XY start = new ArrayList<>(aroundRed.keySet()).get(10000);
        curve:
        for (; ; ) {
            XY start = aroundRed.keySet().stream().filter(p -> !excluded.contains(p)).findFirst().orElse(null);
            if (start == null) return null;
            var used = new HashMap<XY, Integer>();
            var ret = new ArrayList<XY>();
            for (int i = 0; ; i++) {
                System.out.println(i + " " + start);
                ret.add(start);
                used.put(start, i);
                excluded.addAll(index.around(start));
                XY oldStart = start;

                var ii = i;
                Optional<XY> end = index.around(start).stream()
                        .filter(ps -> used.containsKey(ps) && used.get(ps) < ii - 30)
                        .filter(ps -> oldStart.distanceSq(ps) < 5 * 5)
                        .sorted(Comparator.comparingDouble(ps -> ps.distanceSq(oldStart)))
                        .findFirst();
                if (end.isPresent()) {
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
                start = index.around(start).stream()
                        .filter(ps -> !used.containsKey(ps))
                        .filter(ps -> oldStart.distanceSq(ps) < 5 * 5)
//                    .filter(ps -> cosineVectors(oldStart, shiftPoint, ps) > 0.8)
//                    .sorted(Comparator.comparingDouble(ps -> ps.distanceSq(oldStart)))
                        .sorted(Comparator.comparingDouble(ps -> -cosineVectors(oldStart, shiftPoint, ps)))
                        .findFirst()
                        .orElse(null);
                if (start == null) continue curve;
            }
        }
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

    double cosineColorVectors(int from, int to1, int to2) {
        Color cfrom = new Color(from);
        Color cto1 = new Color(to1);
        Color cto2 = new Color(to2);
        Point3 vector1 = new Point3(cto1.getRed() - cfrom.getRed(),
                cto1.getGreen() - cfrom.getGreen(),
                cto1.getBlue() - cfrom.getBlue());
        Point3 vector2 = new Point3(cto2.getRed() - cfrom.getRed(),
                cto2.getGreen() - cfrom.getGreen(),
                cto2.getBlue() - cfrom.getBlue());
        double norm1sq = vector1.scalarMult(vector1);
        double norm2sq = vector2.scalarMult(vector2);
        double scalar = vector1.scalarMult(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
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

    XY checkBounds(XY p) {
        if (p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight()) return p;
        return null;
    }


    private void displayRedScaledSteps(BufferedImage image) throws IOException {
        var multiset = HashMultiset.create();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double fromRed = distance(Color.red.getRGB(), image.getRGB(x, y));
                int code = (int) ((int) fromRed / 80 * 25);
                multiset.add(code);
                try {
                    image.setRGB(x, y, new Color(code, code, code).getRGB());
                } catch (Exception e) {
                    System.nanoTime();
                }
            }
        }
        ImageIO.write(image, "png", new File("/home/denny/proj/bee/recognize/out1.png"));
        System.out.println(multiset);
    }

    double distance(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
        int dred = c1.getRed() - c2.getRed();
        int dgreen = c1.getGreen() - c2.getGreen();
        int dblue = c1.getBlue() - c2.getBlue();
        return Math.sqrt(Math.pow(dred, 2) +
                Math.pow(dgreen, 2) +
                Math.pow(dblue, 2));
    }
}
