import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import tutorial1.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;

/*
To run:
-Djava.library.path=/home/denny/opencv-3.4.1/build/lib
*/
public class MainCV {
    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());

        new MainCV().run();
        //FXHelloCV.main(args);
    }

    void run() throws IOException {
        String imgFile = "/home/denny/Pictures/card-detect/train/13.jpg";
        Mat img = Imgcodecs.imread(imgFile);
        Mat edges = doCanny(img);

        // morphological operators
        // dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        // Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

//        Imgproc.erode(mask, morphOutput, erodeElement);
//        Imgproc.erode(morphOutput, morphOutput, erodeElement);

//        Imgproc.dilate(edges, edges, dilateElement,  new Point(-1,-1), 4);
//        Imgproc.erode(edges, edges, dilateElement,  new Point(-1,-1), 4);

        // using Canny's output as a mask, display the result
        Mat dest = new Mat();
        img.copyTo(dest, edges);


        // Standard Hough Line Transform
//        Mat lines = new Mat(); // will hold the results of the detection
//        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 150); // runs the actual detection
//        // Draw the lines
//        System.out.println("lines.rows()=" + lines.rows());
//        for (int x = 0; x < lines.rows(); x++) {
//            double rho = lines.get(x, 0)[0],
//                    theta = lines.get(x, 0)[1];
//            double a = Math.cos(theta), b = Math.sin(theta);
//            double x0 = a * rho, y0 = b * rho;
//            Point pt1 = new Point(Math.round(x0 + 1000 * (-b)), Math.round(y0 + 1000 * (a)));
//            Point pt2 = new Point(Math.round(x0 - 1000 * (-b)), Math.round(y0 - 1000 * (a)));
//            Imgproc.line(dest, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
//        }
//        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edges, linesP, 3, Math.PI / 180, 100, 100, 40); // runs the actual detection
        // Draw the lines
        System.out.println("linesP.rows()=" + linesP.rows());
        List<Line> lines = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(dest, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            lines.add(new Line(new Point(l[0], l[1]), new Point(l[2], l[3])));
        }
        lines.sort(Comparator.comparingDouble(Line::len2).reversed());
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Line line1 = lines.get(i);

            if (cards.stream().anyMatch(card -> card.intersects(line1))) {
                System.out.println(i + " line skipped");
                continue;
            }
            System.out.println(i + " line accepted");

            Line line2 = closestDirection(lines, line1);

            Imgproc.line(dest, line1.p1, line1.p2, new Scalar(0, 255, 0), 7, Imgproc.LINE_AA, 0);
            Imgproc.line(dest, line2.p1, line2.p2, new Scalar(255, 255, 0), 7, Imgproc.LINE_AA, 0);

            System.out.println(line1.vector() + " len=" + Math.sqrt(line1.len2()));
            System.out.println(line2.vector() + " len=" + Math.sqrt(line2.len2()));
            System.out.println(line1.directionDiff(line2));

            List<Line> linesAcross = lines.stream().filter(l ->
                    isBetween(l, line1, line2)
                            && l.len2() > Math.pow(300, 2)
                            && Util.sameSign(l.side(line1.p1), l.side(line1.p2), l.side(line2.p1), l.side(line2.p2)))
                    .collect(Collectors.toList());

//            for (Line l : linesAcross) {
//                Imgproc.line(dest, l.p1, l.p2, new Scalar(0, 255, 255), 7, Imgproc.LINE_AA, 0);
//            }
            if (linesAcross.size() > 0) {
                Line across1 = linesAcross.get(0); // longest
                Imgproc.line(dest, across1.p1, across1.p2, new Scalar(0, 255, 255), 7, Imgproc.LINE_AA, 0);

                Line across2 = closestDirection(linesAcross, across1);
                Imgproc.line(dest, across2.p1, across2.p2, new Scalar(0, 255, 255), 7, Imgproc.LINE_AA, 0);
                System.out.println(across1.vector() + " len=" + Math.sqrt(across1.len2()));
                System.out.println(across2.vector() + " len=" + Math.sqrt(across2.len2()));
                System.out.println(across1.directionDiff(across2));

                Card card = new Card(line1, line2, across1, across2);
                cards.add(card);
            }
        }
        cards.get(0).warp(dest, img, imgFile);

        ImageIO.write(Utils.matToBufferedImage(dest), "png", new File(imgFile + ".edges.png"));
    }

    private boolean isBetween(Line l, Line line1, Line line2) {
        return line1.side(l.p1) * line2.side(l.p1) < 0 && line1.side(l.p2) * line2.side(l.p2) < 0;
    }

    private static boolean isBetween(Point p, Line line1, Line line2) {
        return line1.side(p) * line2.side(p) < 0;
    }

    static class Card {
        Line line1, line2;
        Line across1, across2;

        public Card(Line line1, Line line2, Line across1, Line across2) {
            this.line1 = line1;
            this.line2 = line2;
            this.across1 = across1;
            this.across2 = across2;
        }

        boolean intersects(Line line) {
            return (isBetween(line.p1, line1, line2) && isBetween(line.p1, across1, across2)) ||
                    (isBetween(line.p2, line1, line2) && isBetween(line.p2, across1, across2)) ||
                    line1.touchingDistance(line) < 50 ||
                    line2.touchingDistance(line) < 50 ||
                    across1.touchingDistance(line) < 50 ||
                    across2.touchingDistance(line) < 50
                    ;
        }

        void warp(Mat dest, Mat img, String imgFile) throws IOException {
            Point int00 = intersection(line2, across1);
            Imgproc.circle(dest, int00, 10, new Scalar(100, 255, 255));

            Point int10 = intersection(line1, across1);
            Imgproc.circle(dest, int10, 10, new Scalar(100, 255, 255));

            Point int01 = intersection(line2, across2);
            Imgproc.circle(dest, int01, 10, new Scalar(100, 255, 255));

            int w = 3 * 225;
            int h = 3 * 350;
            Mat transform = Imgproc.getAffineTransform(new MatOfPoint2f(int00, int10, int01),
                    new MatOfPoint2f(new Point(0, 0), new Point(w, 0), new Point(0, h)));

            Mat warp = new Mat(); // will hold the results of the detection
            Imgproc.warpAffine(img, warp, transform, new Size(w, h), INTER_CUBIC, BORDER_CONSTANT, new Scalar(0, 0, 0));

            ImageIO.write(Utils.matToBufferedImage(warp), "png", new File(imgFile + ".warp.png"));
        }
    }

    static Point intersection(Line l1, Line l2) {
        return intersection(l1.p1, l1.p2, l2.p1, l2.p2);
    }

    // Finds the intersection of two lines, or returns false.
// The lines are defined by (o1, p1) and (o2, p2).
    static Point intersection(Point o1, Point p1, Point o2, Point p2) {
        Point x = Util.sub(o2, o1);
        Point d1 = Util.sub(p1, o1);
        Point d2 = Util.sub(p2, o2);

        double cross = d1.x * d2.y - d1.y * d2.x;
        if (Math.abs(cross) < /*EPS*/1e-8)
            return null;

        double t1 = (x.x * d2.y - x.y * d2.x) / cross;
        return Util.add(o1, Util.mul(d1, t1));
    }

    Line closestDirection(List<Line> lines, Line first) {
        List<Line> byDirection = lines.stream()
                .filter(l -> l.touchingDistance(first) > 100)
                .collect(Collectors.toList());
        byDirection.sort(Comparator.comparingDouble(first::directionDiff));
        Line line2 = byDirection.get(0).directedAs(first);
        return line2;
    }

    private Mat doCanny(Mat frame) {
        double threshold = 20;
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(10, 10));

        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3);


        return detectedEdges;
    }

}
