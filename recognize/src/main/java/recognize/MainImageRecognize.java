package recognize;

import com.google.common.collect.HashMultiset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainImageRecognize {
    public static void main(String[] args) throws Exception {
        new MainImageRecognize().run();
    }

    BufferedImage image;
    Map<Point, Point> aroundRed = new HashMap<>(); // Point -> shifted Point, where red shift is most pronounced
    Set<Point> aroundRedAvg = new HashSet<>();
    Map<Point, List<Point>> index = new HashMap<>();

    void run() throws Exception {
        image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("numbers-crop1.png"));
        System.out.println(image.getWidth() + " x " + image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double maxStepToRed = 32; // lesser steps ignored
                Point bestStep = null;
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 6) {
                    int rgb = image.getRGB(x, y);
                    double fromRed = distance(Color.red.getRGB(), rgb);
                    Point step = new Point(x, y).shiftBy(5, phi);
                    if (step != null) {
                        int rgbStep = image.getRGB(step.x, step.y);
                        if (rgbStep != rgb) {
                            double stepFromRed = distance(Color.red.getRGB(), rgbStep);
                            double cosine = cosineColorVectors(rgb, rgbStep, Color.red.getRGB());
                            double stepToRed = fromRed - stepFromRed;
                            if (cosine > 0.8) {
                                if (maxStepToRed < stepToRed) {
                                    maxStepToRed = stepToRed;
                                    bestStep = step;
                                }
                            }
                        }
                    }
                }
                if (bestStep != null) {
                    aroundRed.put(new Point(x, y), bestStep);
                }
            }
        }
        buildIndex();
        computeAvg();

        aroundRed.keySet().forEach(p -> image.setRGB(p.x, p.y, Color.blue.getRGB()));
        aroundRedAvg.forEach(p -> image.setRGB(p.x, p.y, Color.green.getRGB()));
        ImageIO.write(image, "png", new File("/home/denny/proj/bee/recognize/out5.png"));

        //stepsToRed(image);
    }

    void computeAvg() {
        for (Point p : aroundRed.keySet()) {
            List<Point> near = index.get(new Point(p.x / 10 * 10, p.y / 10 * 10)).stream()
                    .filter(ps -> Math.pow(p.x - ps.x, 2) + Math.pow(p.y - ps.y, 2) < 10 * 10)
                    .filter(ps -> Math.abs(cosineVectors(p, aroundRed.get(p), ps)) > 0.8)
                    .collect(Collectors.toList());
            if (near.size() > 0) {
                Point psum = new Point(0, 0);
                near.forEach(psum::add);
                Point pavg = new Point(psum.x / near.size(), psum.y / near.size());
                if( Math.pow(pavg.x-p.x,2)+Math.pow(pavg.y-p.y,2)<2*2) {
                    aroundRedAvg.add(pavg);
                }
            }
        }
    }

    void buildIndex() {
        for (Point p : aroundRed.keySet()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    index.computeIfAbsent(new Point(p.x / 10 * 10 + i * 10, p.y / 10 * 10 + j * 10), pp -> new ArrayList<>()).add(p);
                }
            }
        }
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
        double norm1sq = vector1.scalarMult(vector1);
        double norm2sq = vector2.scalarMult(vector2);
        double scalar = vector1.scalarMult(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
    }

    double cosineVectors(Point from, Point to1, Point to2) {
        Point vector1 = new Point(to1.x - from.x, to1.y - from.y);
        Point vector2 = new Point(to2.x - from.x, to2.y - from.y);
        double norm1sq = vector1.scalarMult(vector1);
        double norm2sq = vector2.scalarMult(vector2);
        double scalar = vector1.scalarMult(vector2);
        double ret = scalar / Math.sqrt(norm1sq * norm2sq);
        return ret;
    }

    class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void add(Point p) {
            x += p.x;
            y += p.y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        double scalarMult(Point v) {
            return x * v.x + y * v.y;
        }

        Point shiftBy(int step, double phi) {
            Point p = new Point(x + (int) (step * Math.cos(phi)), y + (int) (step * Math.sin(phi)));
            if (p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight()) return p;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
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
