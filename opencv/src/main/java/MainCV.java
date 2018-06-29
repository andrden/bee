import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import tutorial1.FXHelloCV;
import tutorial1.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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
        Imgproc.HoughLinesP(edges, linesP, 3, Math.PI / 180 , 100, 100, 40); // runs the actual detection
        // Draw the lines
        System.out.println("linesP.rows()=" + linesP.rows());
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(dest, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }

        ImageIO.write(Utils.matToBufferedImage(dest), "png", new File(imgFile + ".edges.png"));
    }

    private Mat doCanny(Mat frame) {
        double threshold = 20;
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(7, 7));

        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3);


        return detectedEdges;
    }

}
