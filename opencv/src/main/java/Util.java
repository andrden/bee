import org.opencv.core.Point;

public class Util {
    static double mulScalar(Point vector1, Point vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    static double mulVector(Point vector1, Point vector2) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    static double dist2(Point a, Point b) {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
    }

    static double dist2(Point a, Line line){
        return pDistance2(a.x, a.y, line.p1.x, line.p1.y, line.p2.x, line.p2.y);
    }

    public static double pDistance2(double x, double y, double x1, double y1, double x2, double y2) {

        double A = x - x1; // position of point rel one end of line
        double B = y - y1;
        double C = x2 - x1; // vector along line
        double D = y2 - y1;
        double E = -D; // orthogonal vector
        double F = C;

        double dot = A * E + B * F;
        double len_sq = E * E + F * F;

        return dot*dot / len_sq;
    }

    static Point sub(Point a, Point b){
        return new Point(a.x-b.x, a.y-b.y);
    }

    static Point add(Point a, Point b){
        return new Point(a.x+b.x, a.y+b.y);
    }

    static Point mul(Point a, double t){
        return new Point(a.x*t, a.y*t);
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

    static boolean isBetween(Line l, Line line1, Line line2) {
        return line1.side(l.p1) * line2.side(l.p1) < 0 && line1.side(l.p2) * line2.side(l.p2) < 0;
    }

    static boolean isBetween(Point p, Line line1, Line line2) {
        return line1.side(p) * line2.side(p) < 0;
    }

    static Point intersection(Line l1, Line l2) {
        return intersection(l1.p1, l1.p2, l2.p1, l2.p2);
    }

    // Finds the intersection of two lines, or returns false.
// The lines are defined by (o1, p1) and (o2, p2).
    static Point intersection(Point o1, Point p1, Point o2, Point p2) {
        Point x = sub(o2, o1);
        Point d1 = sub(p1, o1);
        Point d2 = sub(p2, o2);

        double cross = d1.x * d2.y - d1.y * d2.x;
        if (Math.abs(cross) < /*EPS*/1e-8)
            return null;

        double t1 = (x.x * d2.y - x.y * d2.x) / cross;
        return add(o1, mul(d1, t1));
    }
}
