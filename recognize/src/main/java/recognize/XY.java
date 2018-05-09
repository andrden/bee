package recognize;

import java.util.List;
import java.util.Objects;

public class XY {
    int x;
    int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void add(XY p) {
        x += p.x;
        y += p.y;
    }

    XY copy() {
        return new XY(x, y);
    }

    XY subtract(XY p) {
        return new XY(x - p.x, y - p.y);
    }

    XY rotateRight(double alpha) {
        return new XY((int) (x * Math.cos(alpha) + y * Math.sin(alpha)),
                (int) (-x * Math.sin(alpha) + y * Math.cos(alpha)));
    }

    static XY min(List<XY> list) {
        XY ret = list.get(0).copy();
        for (XY p : list) {
            ret.x = Math.min(ret.x, p.x);
            ret.y = Math.min(ret.y, p.y);
        }
        return ret;
    }

    static XY max(List<XY> list) {
        XY ret = list.get(0).copy();
        for (XY p : list) {
            ret.x = Math.max(ret.x, p.x);
            ret.y = Math.max(ret.y, p.y);
        }
        return ret;
    }

    static XY average(List<XY> list) {
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

    XY vectorTurnLeft90() {
        return new XY(-y, x);
    }

    XY vectorTurnLeft90(XY direction) {
        return new XY(x - (direction.y - y), y + (direction.x - x));
    }

    double distanceSq(XY p) {
        return Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
    }

    double scalarMult(XY v) {
        return x * v.x + y * v.y;
    }

    XY shiftBy(int step, double phi) {
        XY p = new XY(x + (int) (step * Math.cos(phi)), y + (int) (step * Math.sin(phi)));
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XY XY = (recognize.XY) o;
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
