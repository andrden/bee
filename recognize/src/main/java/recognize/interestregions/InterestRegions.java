package recognize.interestregions;

import recognize.CurvesExtractor;
import recognize.KnownCurves;
import recognize.util.Colors;
import recognize.util.Images;
import recognize.util.Point3;
import recognize.util.XY;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class InterestRegions {
    static Random rnd = new Random(0);

    public static void main(String[] args) throws Exception {
        KnownCurves knownCurves = new KnownCurves();

        String photoFile = "cards-angle45-5pct.png";
        //String photoFile = "cards-angle45.png";
        //String photoFile = "cards-angle45-5pct.png";
        BufferedImage imgMini = ImageIO.read(InterestRegions.class.getClassLoader().getResourceAsStream(photoFile));
        InterestRegions iregs = new InterestRegions(imgMini);

        BufferedImage imgFull = ImageIO.read(InterestRegions.class.getClassLoader().getResourceAsStream("cards-angle45.png"));
        for (Region r : iregs.regions) {
            Set<XY> enclosure = r.findEnclosure(imgMini.getWidth(), imgMini.getHeight());
            r.extractCurves(imgFull);
            //r.drawCurves(imgFull, Color.YELLOW);
            //drawRegion(enclosure, imgMini, imgFull);

//            Set<XY> all = Sets.union(r.a, r.b);
//            drawRegion(all, imgMini, imgFull);
        }

        for (Region r : iregs.regions) {
            CurvesExtractor curvesExtractor = r.getCurvesExtractor();
            for (int ci = 0; ci < curvesExtractor.finalCurves.size(); ci++) {
                var curve = curvesExtractor.finalCurves.get(ci);
                String type = knownCurves.recognize("ci=" + ci, curve).get(0).name;

                if ("diamonds".equals(type)) {
                    Images.fillPolygon(imgFull, curve.curveLocation, Color.orange);
                } else if ("hearts".equals(type)) {
                    Images.fillPolygon(imgFull, curve.curveLocation, Color.yellow);
                } else if ("9".equals(type)) {
                    Images.fillPolygon(imgFull, curve.curveLocation, Color.green);
                } else if ("8".equals(type)) {
                    Images.fillPolygon(imgFull, curve.curveLocation, Color.darkGray);
                }
            }
        }
        String outFilesPrefix = "/home/denny/proj/bee/recognize/interest-";
        ImageIO.write(imgFull, "png", new File(outFilesPrefix + "5F.png"));
    }

    static void drawRegion(Set<XY> all, BufferedImage imgMini, BufferedImage imgFull) {
        Graphics2D g = (Graphics2D) imgFull.getGraphics();
        Color[] colors = {Color.red, Color.green, Color.blue, Color.black,
                Color.white, Color.magenta, Color.YELLOW,
                Color.cyan};
        g.setColor(colors[(int) (rnd.nextDouble() * colors.length)]);
        g.setStroke(new BasicStroke(6));
        for (XY xy : all) {
            int left = (xy.x + 0) * imgFull.getWidth() / imgMini.getWidth();
            int right = (xy.x + 1) * imgFull.getWidth() / imgMini.getWidth();
            int top = (xy.y + 0) * imgFull.getHeight() / imgMini.getHeight();
            int bottom = (xy.y + 1) * imgFull.getHeight() / imgMini.getHeight();
            if (!all.contains(new XY(xy.x + 1, xy.y))) g.drawLine(right, top, right, bottom);
            if (!all.contains(new XY(xy.x - 1, xy.y))) g.drawLine(left, top, left, bottom);
            if (!all.contains(new XY(xy.x, xy.y + 1))) g.drawLine(left, bottom, right, bottom);
            if (!all.contains(new XY(xy.x, xy.y - 1))) g.drawLine(left, top, right, top);
        }
    }

    BufferedImage image;
    Set<XY> processed = new HashSet<>();
    List<Pair> contrastPairs = new ArrayList<>();

    List<Region> regions = new ArrayList<>();

    Color averageColor(Set<XY> set) {
        Point3 sum = new Point3(0, 0, 0);
        for (XY i : set) {
            var c = new Color(image.getRGB(i.x, i.y));
            sum.add(c.getRed(), c.getGreen(), c.getBlue());
        }
        return new Color(sum.x / set.size(), sum.y / set.size(), sum.z / set.size());
    }

    Boolean closestPart(int step, Color averageA, Color averageB, XY newPoint) {
        int rgbNew = image.getRGB(newPoint.x, newPoint.y);
        double distanceA = Colors.distance(averageA.getRGB(), rgbNew);
        double distanceB = Colors.distance(averageB.getRGB(), rgbNew);
        System.out.printf("step %s total dist=%s  maxAB=%s minAB=%s  a=%s b=%s\n",
                step, (int) Colors.distance(averageA.getRGB(), averageB.getRGB()),
                (int) Math.max(distanceA, distanceB),
                (int) Math.min(distanceA, distanceB),
                distanceA, distanceB);
        if (Math.abs(distanceA - distanceB) < (distanceA + distanceB) / 6) return null; // can't decide definitely
        return distanceA < distanceB;
    }

    XY checkBounds(XY p) {
        if (p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight()) return p;
        return null;
    }

    Map<XY, XY[]> checkList(Region region, XY newPoint) {
        boolean newA = region.a.contains(newPoint);
        var ret = new HashMap<XY, XY[]>();
        for (XY xy : around(newPoint)) {
            if (checkBounds(xy) == null) continue;
            if (processed.contains(xy)) continue;
            if (region.a.contains(xy) || region.b.contains(xy)) continue;
            for (XY i : around(xy)) {
                if (newA) {
                    if (region.b.contains(i)) {
                        ret.put(xy, new XY[]{newPoint, i});
                        break;
                    }
                } else {
                    if (region.a.contains(i)) {
                        ret.put(xy, new XY[]{i, newPoint});
                        break;
                    }
                }
            }
        }
        return ret;
    }

    XY[] around(XY newPoint) {
        XY[] around = {
                new XY(newPoint.x - 1, newPoint.y - 1),
                new XY(newPoint.x, newPoint.y - 1),
                new XY(newPoint.x + 1, newPoint.y - 1),
                new XY(newPoint.x - 1, newPoint.y),

                new XY(newPoint.x + 1, newPoint.y),
                new XY(newPoint.x - 1, newPoint.y + 1),
                new XY(newPoint.x, newPoint.y + 1),
                new XY(newPoint.x + 1, newPoint.y + 1),
        };
        return around;
    }

//    void computeContrastPairs() {
//        for (int x = 0; x < image.getWidth(); x++) {
//            for (int y = 0; y < image.getHeight(); y++) {
//                XY xy = new XY(x, y);
//                Pair[] pairs = {
//                        new Pair(xy, new XY(x + 1, y)),
//                        new Pair(xy, new XY(x + 1, y + 1)),
//                        new Pair(xy, new XY(x, y + 1)),
//                        new Pair(xy, new XY(x - 1, y + 1)),
//                };
//                for (var pair : pairs) {
//                    double d = colorDistance(pair);
//                    if (d >= 0) { // if in image bounds
//                        pair.distance = d;
//                        contrastPairs.add(pair);
//                    }
//                }
//            }
//        }
//        contrastPairs.sort(Comparator.comparingDouble(p -> -p.distance));
//    }

    public InterestRegions(BufferedImage image) throws Exception {
        this.image = image;
        String outFilesPrefix = "/home/denny/proj/bee/recognize/interest-";

//        computeContrastPairs();
//        for (var p : contrastPairs.subList(0, 3000)) {
//            image.setRGB(p.a.x, p.a.y, Color.RED.getRGB());
//            image.setRGB(p.b.x, p.b.y, Color.GREEN.getRGB());
//        }
//        ImageIO.write(image, "png", new File(outFilesPrefix + "c3.png"));


        for (int j = 0; j < 140; j++) {
            System.out.println("===================== region " + j);
            double maxDistance = 0;
            Pair best = null;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    XY xy = new XY(x, y);
                    Pair[] pairs = {
                            new Pair(xy, new XY(x + 1, y)),
                            new Pair(xy, new XY(x + 1, y + 1)),
                            new Pair(xy, new XY(x, y + 1)),
                            new Pair(xy, new XY(x - 1, y + 1)),
                    };
                    for (var pair : pairs) {
                        if (processed.contains(pair.a) || processed.contains(pair.b)) {
                            continue;
                        }
                        double d = colorDistance(pair);
                        if (d > maxDistance) {
                            maxDistance = d;
                            best = pair;
                        }
                    }
                }
            }

            Region region = getRegion(best, Integer.MAX_VALUE);
            processed.addAll(region.a);
            processed.addAll(region.b);
            regions.add(region);

            //for (Region regioni : regions) {
            for (XY i : region.a) {
                image.setRGB(i.x, i.y, Color.RED.getRGB());
            }
            for (XY i : region.b) {
                image.setRGB(i.x, i.y, Color.GREEN.getRGB());
            }
            //}

            ImageIO.write(image, "png", new File(outFilesPrefix + "5.png"));
        }

    }

    private Region getRegion(Pair best, int steps) {
        Region region = new Region();
        region.a.add(best.a);
        region.b.add(best.b);
        Map<XY, XY[]> checkList = checkList(region, best.a);
        Map<XY, XY[]> newCheckList = null;
        for (int j = 0; j < steps; j++) {
            var averageA = averageColor(region.a);
            var averageB = averageColor(region.b);
            double regionSeparation = Colors.distance(averageA.getRGB(), averageB.getRGB());

            Set<XY> plusA = new HashSet<>();
            for (XY xy : checkList.keySet()) {
                Boolean closest = closestPart(j, averageA, averageB, xy);
                if (closest != null) { // if decision made
                    if (closest) plusA.add(xy);
                }
            }
            List<XY> added = new ArrayList<>();
            for (XY i : checkList.keySet()) {
                if (plusA.contains(i)) {
                    XY bPoint = checkList.get(i)[1];
                    double edgeDistance = Colors.distance(image.getRGB(i.x, i.y), image.getRGB(bPoint.x, bPoint.y));
                    if (edgeDistance > regionSeparation / 20) {
                        region.a.add(i);
                        added.add(i);
                        System.out.println("add-a dist-b=" + edgeDistance);
                    }
                } else {
                    XY aPoint = checkList.get(i)[0];
                    double edgeDistance = Colors.distance(image.getRGB(i.x, i.y), image.getRGB(aPoint.x, aPoint.y));
                    if (edgeDistance > regionSeparation / 10) {
                        region.b.add(i);
                        added.add(i);
                        System.out.println("add-b dist-a=" + edgeDistance);
                    }
                }
            }
            newCheckList = new HashMap<>();
            for (XY i : added) {
                newCheckList.putAll(checkList(region, i));
            }
            if (newCheckList.isEmpty()) break;
            checkList = newCheckList;
        }
//        if (steps > 0) {
//            for (XY i : newCheckList.keySet()) {
//                image.setRGB(i.x, i.y, Color.YELLOW.getRGB());
//            }
//        }
        return region;
    }

    double colorDistance(Pair pair) {
        var b = pair.b;
        if (b.x <= 0 || b.y <= 0 || b.x >= image.getWidth() || b.y >= image.getHeight()) return -1;
        return Colors.distance(image.getRGB(pair.a.x, pair.a.y), image.getRGB(b.x, b.y));
    }
}
