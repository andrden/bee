package recognize;

class Point3 {
    int x;
    int y;
    int z;

    public Point3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double scalarMult(Point3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
}
