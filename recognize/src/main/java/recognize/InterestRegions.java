package recognize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterestRegions {
    public static void main(String[] args) throws Exception {
        //String photoFile = "cards-angle45-3pct.png";
        //String photoFile = "cards-angle45.png";
        String photoFile = "cards-angle45-5pct.png";
        new InterestRegions(ImageIO.read(InterestRegions.class.getClassLoader().getResourceAsStream(photoFile)));

    }

    BufferedImage image;
    Set<XY> processed = new HashSet<>();

    static class Pair {
        XY a;
        XY b;

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

    boolean closestPart(Region region, XY newPoint) {
        var ca = averageColor(region.a);
        var cb = averageColor(region.b);
        int rgbNew = image.getRGB(newPoint.x, newPoint.y);
        return Colors.distance(ca.getRGB(), rgbNew) < Colors.distance(cb.getRGB(), rgbNew);
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

    public InterestRegions(BufferedImage image) throws Exception {
        this.image = image;

        List<Region> regions = new ArrayList<>();
        for (int j = 0; j < 40; j++) {
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

            String outFilesPrefix = "/home/denny/proj/bee/recognize/interest-";
            ImageIO.write(image, "png", new File(outFilesPrefix + "5.png"));
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
            for (XY i : checkList) {
                if (closestPart(region, i)) plusA.add(i);
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
