package recognize.interestregions;

import com.google.common.collect.Sets;
import recognize.XY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Region {
    Set<XY> a = new HashSet<>();
    Set<XY> b = new HashSet<>();

    Set<XY> findEnclosure(int imgWidth, int imgHeight) {
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

        return Sets.difference(boundingBox, outer);
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
