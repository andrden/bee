package recognize.other;

import recognize.util.XY;

import java.util.List;
import java.util.stream.Collectors;

public class Inertia {
    List<XY> curve;
    XY center;
    public double alpha;

    public Inertia(List<XY> curve) {
        this.curve = curve;
        center = centerMass();

        alpha = Math.atan2(2*inertiaAroundXY(), inertiaAroundY()-inertiaAroundX())/2;
    }

    public List<XY> alignedCurve(){
        return curve.stream()
                .map(p -> p.subtract(center))
                .map(p -> p.rotateRight(alpha))
                .collect(Collectors.toList());
    }

    double inertiaAroundX() {
        return curve.stream().mapToDouble(p -> Math.pow(p.y - center.y, 2)).sum();
    }

    double inertiaAroundY() {
        return curve.stream().mapToDouble(p -> Math.pow(p.x - center.x, 2)).sum();
    }

    double inertiaAroundXY() {
        return curve.stream().mapToDouble(p -> (p.x - center.x) * (p.y - center.y)).sum();
    }

    XY centerMass() {
        XY c = new XY(0, 0);
        for (XY p : curve) {
            c.add(p);
        }
        return new XY(c.x / curve.size(), c.y / curve.size());
    }
}
