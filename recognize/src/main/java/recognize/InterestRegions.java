package recognize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class InterestRegions {
    public static void main(String[] args) throws Exception {
        String photoFile = "cards-angle45-3pct.png";
        //String photoFile = "cards-angle45.png";
        //String photoFile = "cards-angle45-5pct.png";
        new InterestRegions(ImageIO.read(InterestRegions.class.getClassLoader().getResourceAsStream(photoFile)));

    }

    BufferedImage image;
    Set<XY> processed = new HashSet<>();
    List<Pair> contrastPairs = new ArrayList<>();

    static class Pair {
        XY a;
        XY b;
        double distance;

        public Pair(XY a, XY b) {
            this.a = a;
            this.b = b;
        }
    }

    class Region {
        Set<XY> a = new HashSet<>();
        Set<XY> b = new HashSet<>();
    }

    Color averageColor(Set<XY> set) {
        Point3 sum = new Point3(0, 0, 0);
        for (XY i : set) {
            var c = new Color(image.getRGB(i.x, i.y));
            sum.add(c.getRed(), c.getGreen(), c.getBlue());
        }
        return new Color(sum.x / set.size(), sum.y / set.size(), sum.z / set.size());
    }

    Boolean closestPart(int step, Region region, XY newPoint) {
        var ca = averageColor(region.a);
        var cb = averageColor(region.b);
        int rgbNew = image.getRGB(newPoint.x, newPoint.y);
        double distanceA = Colors.distance(ca.getRGB(), rgbNew);
        double distanceB = Colors.distance(cb.getRGB(), rgbNew);
        System.out.printf("step %s total dist=%s  maxAB=%s minAB=%s  a=%s b=%s\n",
                step, (int) Colors.distance(ca.getRGB(), cb.getRGB()),
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

    Set<XY> checkList(Region region, XY newPoint) {
        boolean newA = region.a.contains(newPoint);
        var ret = new HashSet<XY>();
        for (XY xy : around(newPoint)) {
            if (checkBounds(xy) == null) continue;
            if (processed.contains(xy)) continue;
            if (region.a.contains(xy) || region.b.contains(xy)) continue;
            for (XY i : around(xy)) {
                if (newA) {
                    if (region.b.contains(i)) {
                        ret.add(xy);
                        break;
                    }
                } else {
                    if (region.a.contains(i)) {
                        ret.add(xy);
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

    void computeContrastPairs() {
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
                    double d = colorDistance(pair);
                    if (d >= 0) { // if in image bounds
                        pair.distance = d;
                        contrastPairs.add(pair);
                    }
                }
            }
        }
        contrastPairs.sort(Comparator.comparingDouble(p -> -p.distance));
    }

    public InterestRegions(BufferedImage image) throws Exception {
        this.image = image;
        String outFilesPrefix = "/home/denny/proj/bee/recognize/interest-";

//        computeContrastPairs();
//        for (var p : contrastPairs.subList(0, 3000)) {
//            image.setRGB(p.a.x, p.a.y, Color.RED.getRGB());
//            image.setRGB(p.b.x, p.b.y, Color.GREEN.getRGB());
//        }
//        ImageIO.write(image, "png", new File(outFilesPrefix + "c3.png"));


        List<Region> regions = new ArrayList<>();
        for (int j = 0; j < 6; j++) {
            System.out.println("region " + j);
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

            ImageIO.write(image, "png", new File(outFilesPrefix + "3.png"));
        }

    }

    private Region getRegion(Pair best, int steps) {
        Region region = new Region();
        region.a.add(best.a);
        region.b.add(best.b);
        Set<XY> checkList = checkList(region, best.a);
        Set<XY> newCheckList = null;
        for (int j = 0; j < steps; j++) {
            Set<XY> plusA = new HashSet<>();
            for (XY xy : checkList) {
                Boolean closest = closestPart(j, region, xy);
                if(closest!=null) { // if decision made
                    if (closest) plusA.add(xy);
                }
            }
            for (XY i : checkList) {
                if (plusA.contains(i)) {
                    region.a.add(i);
                } else {
                    region.b.add(i);
                }
            }
            newCheckList = new HashSet<>();
            for (XY i : checkList) {
                newCheckList.addAll(checkList(region, i));
            }
            if (newCheckList.isEmpty()) break;
            checkList = newCheckList;
        }
        if (steps > 0) {
            for (XY i : newCheckList) {
                image.setRGB(i.x, i.y, Color.YELLOW.getRGB());
            }
        }
        return region;
    }

    double colorDistance(Pair pair) {
        var b = pair.b;
        if (b.x <= 0 || b.y <= 0 || b.x >= image.getWidth() || b.y >= image.getHeight()) return -1;
        return Colors.distance(image.getRGB(pair.a.x, pair.a.y), image.getRGB(b.x, b.y));
    }
}
