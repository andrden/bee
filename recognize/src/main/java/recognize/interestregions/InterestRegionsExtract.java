package recognize.interestregions;

import recognize.CurvesExtractor;
import recognize.KnownCurves;
import recognize.util.Colors;
import recognize.util.Images;
import recognize.util.Point3;
import recognize.util.XY;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class InterestRegionsExtract {

    public static void main(String[] args) throws Exception {
//        String photoFile = "cards-crop1-5pct.png";
//        String photoFileFull = "cards-crop1.png";
//        BufferedImage imgMini = ImageIO.read(InterestRegionsExtract.class.getClassLoader().getResourceAsStream(photoFile));
//        BufferedImage imgFull = ImageIO.read(InterestRegionsExtract.class.getClassLoader().getResourceAsStream(photoFileFull));

        BufferedImage imgMini = ImageIO.read(new File("/home/denny/Pictures/IMG_20180515_204247-8pct.png"));
        BufferedImage imgFull = ImageIO.read(new File("/home/denny/Pictures/IMG_20180515_204247.jpg"));


        InterestRegions iregs = new InterestRegions(imgMini);

        int regNo = 0;
        for (Region r : iregs.regions) {
            regNo++;
            ImageIO.write(
                    r.getSubImage(imgMini.getWidth(), imgMini.getHeight(), imgFull),
                    "png",
                    new File("/home/denny/Pictures/regionsOut/c6-" + regNo + ".png"));
        }
    }

}
