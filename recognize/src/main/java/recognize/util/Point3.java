package recognize.util;

public class Point3 {
    public int x;
    public int y;
    public int z;

    public Point3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public double scalarMult(Point3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
}
