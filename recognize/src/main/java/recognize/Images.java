package recognize;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Images {

    static void fillPolygon(BufferedImage image, List<XY> curve, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.setPaint(color);
        Polygon p = new Polygon();
        curve.forEach(i -> p.addPoint(i.x, i.y));
        g.fillPolygon(p);
    }

    static void drawPolygon(BufferedImage image, List<XY> curve, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.setPaint(color);
        Polygon p = new Polygon();
        curve.forEach(i -> p.addPoint(i.x, i.y));
        g.drawPolygon(p);
    }

}
