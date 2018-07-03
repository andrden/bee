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
}
