package recognize;

import com.google.common.collect.HashMultiset;
import recognize.util.Colors;
import recognize.util.Images;
import recognize.util.XY;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class MainImageRecognize {
    public static void main(String[] args) throws Exception {
        new MainImageRecognize().run();
    }

    Random rand = new Random(1);
    BufferedImage image;

    void run() throws Exception {
        KnownCurves knownCurves = new KnownCurves();
        //String photoFile = "cards-crop1.png";
        String photoFile = "cards-angle45.png";
        image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(photoFile));
        String outFilesPrefix = "/home/denny/proj/bee/recognize/card";
        System.out.println(image.getWidth() + " x " + image.getHeight());

        CurvesExtractor curvesExtractor = new CurvesExtractor("", image, 3);
        curvesExtractor.extract();
        curvesExtractor.aroundRed.keySet().forEach(p -> {
            image.setRGB(p.x, p.y, Color.blue.getRGB());
        });
//        curvesExtractor.aroundRed.keySet().forEach(p -> {
//            if(rand.nextDouble()<0.04) line(image, p, aroundRed.get(p), Color.green);
//        });
//        aroundRedAvg.forEach(p -> image.setRGB(p.x, p.y, Color.green.getRGB()));
        //curvesExtractor.excludedByCurves.forEach(p -> image.setRGB(p.x, p.y, Color.yellow.getRGB()));

        for (int ci = 0; ci < curvesExtractor.finalCurves.size(); ci++) {
            var curve = curvesExtractor.finalCurves.get(ci);
            XY curveMin = XY.min(curve.curveRescaled);
            XY curveMax = XY.max(curve.curveRescaled);

            int width = curveMax.x - curveMin.x + 1;
            BufferedImage sub = new BufferedImage(2 * width, curveMax.y - curveMin.y + 1, image.getType());
            Images.fillPolygon(sub, curve.curveRescaled, Color.red);
            for (int line = 0; line < 100; line++) {
                line(sub, new XY(width, line), new XY(width + curve.lines[line], line), Color.lightGray);
            }
            //            for (int i = 1; i < curve.size(); i++) {
//                line(sub, curve.get(i - 1).subtract(curveMin), curve.get(i).subtract(curveMin), Color.red);
//            }
            for (int i = 0; i < curve.curveRescaled.size(); i++) {
                sub.setRGB(curve.curveRescaled.get(i).x - curveMin.x, curve.curveRescaled.get(i).y - curveMin.y, Color.blue.getRGB());
            }
            String fname = "sub" + ci + ".png";
            ImageIO.write(sub, "png", new File(outFilesPrefix + fname));
            knownCurves.recognize(fname, curve);

            Images.drawPolygon(image, curve.curveLocation, Color.yellow);
//            if( min(knownDistances).equals("hearts") ) {
//                Images.fillPolygon(image, curve.curveLocation, Color.yellow);
//            }
//            if( min(knownDistances).equals("diamonds") ) {
//                Images.fillPolygon(image, curve.curveLocation, Color.green);
//            }
        }
        for( int i=0; i<curvesExtractor.histogramsGrid; i++ ){
            for( int j=0; j<curvesExtractor.histogramsGrid; j++ ){
                image.getGraphics().drawRect(i*curvesExtractor.histW, j*curvesExtractor.histH, curvesExtractor.histW, curvesExtractor.histH);
            }
        }
        ImageIO.write(image, "png", new File(outFilesPrefix + ".png"));

        //displayRedScaledSteps(image);
    }

    String min(Map<String, Double> map) {
        return map.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).map(Map.Entry::getKey).findFirst().get();
    }


    void line(BufferedImage image, XY from, XY to, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        BasicStroke bs = new BasicStroke(1);
        g.setStroke(bs);
        g.drawLine(from.x, from.y, to.x, to.y);
        //        for( int i=0; i<=points; i++){
//
//        }
    }


    private void displayRedGrayScaledSteps(BufferedImage image) throws IOException {
        var multiset = HashMultiset.create();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double fromRed = Colors.distance(Color.red.getRGB(), image.getRGB(x, y));
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

}
