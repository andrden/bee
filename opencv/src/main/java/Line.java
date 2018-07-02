import org.opencv.core.Point;

class Line {
    Point p1;
    Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line reverse() {
        return new Line(p2, p1);
    }

    public double len2() {
        return Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2);
    }

    public double direction() {
        double a = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        if (a < 0) a += Math.PI;
        return a;
    }

    public double directionDiff(Line other) {
        double a = direction();
        double b = other.direction();
        double diff = Math.abs(a - b);
        if (diff > Math.PI / 2) diff = Math.PI - diff;
        return diff;
    }

    public double touchingDistance(Line other) {
        return Math.sqrt(Math.min(
                Math.min(dist2(p1, other.p1), dist2(p2, other.p1)),
                Math.min(dist2(p1, other.p2), dist2(p2, other.p2))));
    }

    public double mulScalar(Line other) {
        return mulScalar(vector(), other.vector());
    }

    public double side(Point p) {
        return mulVector(vector(), new Point(p.x - p1.x, p.y - p1.y));
    }

    Point vector() {
        return new Point(p2.x - p1.x, p2.y - p1.y);
    }

    static double mulScalar(Point vector1, Point vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    static double mulVector(Point vector1, Point vector2) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    static double dist2(Point a, Point b) {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
    }

    static boolean sameSign(double... args) {
        if (args[0] > 0) {
            for (double v : args) {
                if (v <= 0) return false;
            }
        } else if (args[0] < 0) {
            for (double v : args) {
                if (v >= 0) return false;
            }
        } else return false;
        return true;
    }

    @Override
    public String toString() {
        return "Line{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                '}';
    }
}
