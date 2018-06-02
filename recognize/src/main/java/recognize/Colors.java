package recognize;

import java.awt.*;

public class Colors {
    public static double distance(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
        int dred = c1.getRed() - c2.getRed();
        int dgreen = c1.getGreen() - c2.getGreen();
        int dblue = c1.getBlue() - c2.getBlue();
        return Math.sqrt(Math.pow(dred, 2) +
                Math.pow(dgreen, 2) +
                Math.pow(dblue, 2));
    }

    public static double cosineColorVectors(int from, int to1, int to2) {
        Color cfrom = new Color(from);
        Color cto1 = new Color(to1);
        Color cto2 = new Color(to2);
        Point3 vector1 = new Point3(cto1.getRed() - cfrom.getRed(),
                cto1.getGreen() - cfrom.getGreen(),
                cto1.getBlue() - cfrom.getBlue());
        Point3 vector2 = new Point3(cto2.getRed() - cfrom.getRed(),
                cto2.getGreen() - cfrom.getGreen(),
                cto2.getBlue() - cfrom.getBlue());
        double norm1sq = vector1.scalarMult(vector1);
        double norm2sq = vector2.scalarMult(vector2);
        double scalar = vector1.scalarMult(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
    }

}
