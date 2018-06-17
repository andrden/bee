package recognize;

import javafx.scene.paint.Color;

public class RectColors {
    static Color rectColor(String type) {
        if (type.equals("h")) return Color.RED;
        if (type.equals("d")) return Color.ORANGE;
        if (type.equals("s")) return Color.BLACK;
        if (type.equals("c")) return Color.GRAY;
        if (type.equals("4")) return Color.CYAN;
        if (type.equals("7")) return Color.GREEN;
        if (type.equals("8")) return Color.YELLOW;
        if (type.equals("k")) return Color.MAGENTA;
        throw new IllegalArgumentException(type);
    }

    static java.awt.Color toAwtColor(Color c){
        return new java.awt.Color((float)c.getRed(), (float)c.getGreen(), (float)c.getBlue());
    }
}
