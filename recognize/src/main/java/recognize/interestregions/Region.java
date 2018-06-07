package recognize.interestregions;

import com.google.common.collect.Sets;
import recognize.*;
import recognize.util.Colors;
import recognize.util.Curve;
import recognize.util.Images;
import recognize.util.XY;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

class Region {
    Set<XY> a = new HashSet<>();
    Set<XY> b = new HashSet<>();

    int imgMiniWidth;
    int imgMiniHeight;
    Set<XY> enclosure;

    CurvesExtractor curvesExtractor;

    void extractCurves(BufferedImage imgFull) {
        Histogram histogram = histogram(imgFull);
        curvesExtractor = new CurvesExtractor(imgFull);
        for (XY xy : enclosure) {
            int left = (xy.x + 0) * imgFull.getWidth() / imgMiniWidth;
            int right = (xy.x + 1) * imgFull.getWidth() / imgMiniWidth;
            int top = (xy.y + 0) * imgFull.getHeight() / imgMiniHeight;
            int bottom = (xy.y + 1) * imgFull.getHeight() / imgMiniHeight;
            for (int x = left; x < right; x++) {
                for (int y = top; y < bottom; y++) {
                    curvesExtractor.computeAroundRed(x, y, histogram);
                }
            }
        }
        curvesExtractor.extract();
//        curvesExtractor.aroundRed.keySet().forEach(p -> {
//            imgFull.setRGB(p.x, p.y, Color.blue.getRGB());
//        });
    }

    void drawCurves(BufferedImage imgFull, Color color) {
        for (int ci = 0; ci < curvesExtractor.finalCurves.size(); ci++) {
            var curve = curvesExtractor.finalCurves.get(ci);
            //Images.drawPolygon(imgFull, curve.curveLocation, Color.yellow);
            Images.fillPolygon(imgFull, curve.curveLocation, Color.yellow);
        }
    }

    public CurvesExtractor getCurvesExtractor() {
        return curvesExtractor;
    }

    Histogram histogram(BufferedImage imgFull) {
        Histogram h = new Histogram();
        for (XY xy : enclosure) {
            int left = (xy.x + 0) * imgFull.getWidth() / imgMiniWidth;
            int right = (xy.x + 1) * imgFull.getWidth() / imgMiniWidth;
            int top = (xy.y + 0) * imgFull.getHeight() / imgMiniHeight;
            int bottom = (xy.y + 1) * imgFull.getHeight() / imgMiniHeight;
            for (int x = left; x < right; x++) {
                for (int y = top; y < bottom; y++) {
                    double fromRed = Colors.distance(Color.red.getRGB(), imgFull.getRGB(x, y));
                    h.add(fromRed);
                }
            }
        }
        h.finish();
        return h;
    }

    Rectangle getBounds() {
        Set<XY> all = Sets.union(a, b);
        XY min = XY.min(all);
        XY max = XY.max(all);
        return new Rectangle(min.x, min.y, max.x - min.x + 1, max.y - min.y + 1);
    }

    BufferedImage getSubImage(int imgMiniWidth, int imgMiniHeight, BufferedImage imgFull) {
        Rectangle bounds = getBounds();
        return imgFull.getSubimage(
                bounds.x*imgFull.getWidth() / imgMiniWidth,
                bounds.y*imgFull.getHeight() / imgMiniHeight,
                bounds.width*imgFull.getWidth() / imgMiniWidth,
                bounds.height*imgFull.getHeight() / imgMiniHeight
                );
    }

    Set<XY> findEnclosure(int imgWidth, int imgHeight) {
        this.imgMiniWidth = imgWidth;
        this.imgMiniHeight = imgHeight;

        Set<XY> all = Sets.union(a, b);
        XY min = XY.min(all);
        XY max = XY.max(all);
        Set<XY> boundingBox = new HashSet<>();
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                boundingBox.add(new XY(x, y));
            }
        }

        Set<XY> outer = new HashSet<>();
        for (int x = min.x - 1; x <= max.x + 1; x++) {
            for (int y = min.y - 1; y <= max.y + 1; y++) {
                if (x == min.x - 1 || y == min.y - 1 || x == max.x + 1 || y == max.y + 1) {
                    if (x >= 0 && y >= 0 && x < imgWidth && y < imgHeight) {
                        outer.add(new XY(x, y));
                    }
                }
            }
        }
        Set<XY> scan = new HashSet<>(outer);
        for (; ; ) {
            Set<XY> newScan = new HashSet<>();
            for (XY p : scan) {
                if (p.x - 1 >= min.x) add(all, new XY(p.x - 1, p.y), newScan, outer);
                if (p.x + 1 <= max.x) add(all, new XY(p.x + 1, p.y), newScan, outer);
                if (p.y - 1 >= min.y) add(all, new XY(p.x, p.y - 1), newScan, outer);
                if (p.y + 1 <= max.y) add(all, new XY(p.x, p.y + 1), newScan, outer);
            }
            if (newScan.isEmpty()) break;
            outer.addAll(newScan);
            scan = newScan;
        }

        enclosure = Sets.difference(boundingBox, outer);
        return enclosure;
    }

    void add(Set<XY> all, XY p, Set<XY> scan, Set<XY> outer) {
        if (!outer.contains(p) && !all.contains(p)) scan.add(p);
    }

//    void outerBorder() {
//        for (XY xy : all) {
//
//        }
//    }
//
//    void borderPairs(){
//        List<Pair> list = new ArrayList<>();
//        Set<XY> all = Sets.union(a, b);
//        for (XY xy : all) {
//            int left = (xy.x + 0);
//            int right = (xy.x + 1);
//            int top = (xy.y + 0);
//            int bottom = (xy.y + 1);
//            if (!all.contains(new XY(xy.x + 1, xy.y))) list.add(new Pair(new XY(right,top), new XY(right, bottom)));
//            if (!all.contains(new XY(xy.x - 1, xy.y))) list.add(new Pair(new XY(left, top), new XY( left, bottom)));
//            if (!all.contains(new XY(xy.x, xy.y + 1))) list.add(new Pair(new XY(left, bottom), new XY( right, bottom)));
//            if (!all.contains(new XY(xy.x, xy.y - 1))) list.add(new Pair(new XY(left, top), new XY( right, top)));
//        }
//    }
}
