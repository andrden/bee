package recognize.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class XY {
    public int x;
    public int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(XY p) {
        x += p.x;
        y += p.y;
    }

    public XY copy() {
        return new XY(x, y);
    }

    public XY subtract(XY p) {
        return new XY(x - p.x, y - p.y);
    }

    public XY rotateRight(double alpha) {
        return new XY((int) (x * Math.cos(alpha) + y * Math.sin(alpha)),
                (int) (-x * Math.sin(alpha) + y * Math.cos(alpha)));
    }

    public static List<XY> shift(List<XY> curve, XY by) {
        return curve.stream()
                .map(p -> new XY(p.x + by.x, p.y + by.y))
                .collect(Collectors.toList());
    }

    public static List<XY> rescaleHeight(List<XY> curve, int newH) {
        XY min = min(curve);
        XY max = max(curve);
        return curve.stream()
                .map(p -> new XY((p.x - min.x) * newH / (max.y - min.y), (p.y - min.y) * newH / (max.y - min.y)))
                .collect(Collectors.toList());
    }

    public static XY min(Collection<XY> list) {
        XY ret = list.iterator().next().copy();
        for (XY p : list) {
            ret.x = Math.min(ret.x, p.x);
            ret.y = Math.min(ret.y, p.y);
        }
        return ret;
    }

    public static XY max(Collection<XY> list) {
        XY ret = list.iterator().next().copy();
        for (XY p : list) {
            ret.x = Math.max(ret.x, p.x);
            ret.y = Math.max(ret.y, p.y);
        }
        return ret;
    }

    public static XY average(List<XY> list) {
        XY psum = new XY(0, 0);
        list.forEach(psum::add);
        return new XY(psum.x / list.size(), psum.y / list.size());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public XY vectorTurnLeft90() {
        return new XY(-y, x);
    }

    public XY vectorTurnLeft90(XY direction) {
        return new XY(x - (direction.y - y), y + (direction.x - x));
    }

    public double distanceSq(XY p) {
        return Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
    }

    public double scalarMult(XY v) {
        return x * v.x + y * v.y;
    }

    public XY shiftBy(int step, double phi) {
        XY p = new XY(x + (int) (step * Math.cos(phi)), y + (int) (step * Math.sin(phi)));
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XY XY = (recognize.util.XY) o;
        return x == XY.x &&
                y == XY.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
