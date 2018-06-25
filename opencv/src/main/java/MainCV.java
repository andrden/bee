import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import tutorial1.FXHelloCV;

/*
To run:
-Djava.library.path=/home/denny/opencv-3.4.1/build/lib
*/
public class MainCV {
    public static void main(String[] args) {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
        System.out.println( "mat = " + mat.dump() );

        FXHelloCV.main(args);
    }
}
