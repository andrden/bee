package recognize;

import com.google.common.collect.HashMultiset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    BufferedImage image;

    void run() throws Exception {
        image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("numbers-crop1.png"));
        System.out.println(image.getWidth() + " x " + image.getHeight());

        Set<Point> aroundRed = new HashSet<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 6) {
                    int rgb = image.getRGB(x, y);
                    double fromRed = distance(Color.red.getRGB(), rgb);
                    Point step = new Point(x, y).shiftBy(5, phi);
                    if (step != null) {
                        int rgbStep = image.getRGB(step.x, step.y);
                        if (rgbStep != rgb) {
                            double stepFromRed = distance(Color.red.getRGB(), rgbStep);
                            double cosine = cosineColorVectors(rgb, rgbStep, Color.red.getRGB());
                            if (stepFromRed < fromRed - 32 && cosine > 0.8) {
                                aroundRed.add(new Point(x, y));
                            }
                        }
                    }
                }
            }
        }
        aroundRed.forEach(p -> image.setRGB(p.x, p.y, Color.blue.getRGB()));
        ImageIO.write(image, "png", new File("/home/denny/proj/bee/recognize/out3.png"));

        //stepsToRed(image);
    }

    double cosineColorVectors(int from, int to1, int to2) {
        Color cfrom = new Color(from);
        Color cto1 = new Color(to1);
        Color cto2 = new Color(to2);
        Point3 vector1 = new Point3(cto1.getRed() - cfrom.getRed(),
                cto1.getGreen() - cfrom.getGreen(),
                cto1.getBlue() - cfrom.getBlue());
        Point3 vector2 = new Point3(cto2.getRed() - cfrom.getRed(),
                cto2.getGreen() - cfrom.getGreen(),
                cto2.getBlue() - cfrom.getBlue());
        double norm1sq = vector1.scalar(vector1);
        double norm2sq = vector2.scalar(vector2);
        double scalar = vector1.scalar(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
    }

    class Point3 {
        int x;
        int y;
        int z;

        public Point3(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        double scalar(Point3 v) {
            return x * v.x + y * v.y + z * v.z;
        }
    }

    class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        Point shiftBy(int step, double phi) {
            Point p = new Point(x + (int) (step * Math.cos(phi)), y + (int) (step * Math.sin(phi)));
            if (p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight()) return p;
            return null;
        }
    }

    private void stepsToRed(BufferedImage image) throws IOException {
        var multiset = HashMultiset.create();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double fromRed = distance(Color.red.getRGB(), image.getRGB(x, y));
                int code = (int) ((int) fromRed / 80 * 25);
                multiset.add(code);
                try {
                    image.setRGB(x, y, new Color(code, code, code).getRGB());
                } catch (Exception e) {
                    System.nanoTime();
                }
            }
        }
        ImageIO.write(image, "png", new File("/home/denny/proj/bee/recognize/out1.png"));
        System.out.println(multiset);
    }

    double distance(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
        int dred = c1.getRed() - c2.getRed();
        int dgreen = c1.getGreen() - c2.getGreen();
        int dblue = c1.getBlue() - c2.getBlue();
        return Math.sqrt(Math.pow(dred, 2) +
                Math.pow(dgreen, 2) +
                Math.pow(dblue, 2));
    }
}
