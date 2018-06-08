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

import static recognize.interestregions.InterestRegions.drawRegion;

public class InterestRegionsExtract {

    public static void main(String[] args) throws Exception {
        File dir = new File("/home/denny/Pictures/regionsOut");

//        String photoFile = "cards-crop1-5pct.png";
//        String photoFileFull = "cards-crop1.png";
//        BufferedImage imgMini = ImageIO.read(InterestRegionsExtract.class.getClassLoader().getResourceAsStream(photoFile));
//        BufferedImage imgFull = ImageIO.read(InterestRegionsExtract.class.getClassLoader().getResourceAsStream(photoFileFull));

        BufferedImage imgMini = ImageIO.read(new File("/home/denny/Downloads/IMG_20180519_134609-10pct.png"));
        BufferedImage imgFull = ImageIO.read(new File("/home/denny/Downloads/IMG_20180519_134609.jpg"));

        InterestRegions iregs = new InterestRegions(imgMini);

        int regNo = 0;
        for (Region r : iregs.regions) {
            regNo++;
            ImageIO.write(
                    r.getSubImage(imgMini.getWidth(), imgMini.getHeight(), imgFull),
                    "png",
                    new File(dir,"c8-" + regNo + ".png"));
        }
        for (Region r : iregs.regions) {
            Set<XY> enclosure = r.findEnclosure(imgMini.getWidth(), imgMini.getHeight());
            drawRegion(enclosure, imgMini, imgFull);
        }
        ImageIO.write(imgFull, "png", new File(dir,"FULL.png"));
    }

}
