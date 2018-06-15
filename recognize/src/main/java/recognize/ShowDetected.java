package recognize;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ShowDetected {
    public static void main(String[] args) throws Exception {
        String fname = "/home/denny/Pictures/card-detect/train/7.jpg";
        BufferedImage bi = ImageIO.read(new File(fname));
        Graphics2D graphics2D = (Graphics2D) bi.getGraphics();
        graphics2D.setColor(Color.red);
        graphics2D.setStroke(new BasicStroke(10));

        JsonElement all = new JsonParser().parse("[{'confidence': 0.27064518516525354, 'type': 'rectangle', 'coordinates': {'y': 174.96625334022974, 'x': 2525.120642529397, 'width': 118.50154759333691, 'height': 95.26541497157169}, 'label': '7'}, {'confidence': 0.25251345256113406, 'type': 'rectangle', 'coordinates': {'y': 184.23735994783206, 'x': 2342.555694310384, 'width': 125.99589597261865, 'height': 100.55144383357117}, 'label': '7'}]");
        for (JsonElement el : all.getAsJsonArray()) {
            JsonObject coord = el.getAsJsonObject().get("coordinates").getAsJsonObject();
            int x = coord.get("x").getAsInt();
            int y = coord.get("y").getAsInt();
            int w = coord.get("width").getAsInt();
            int h = coord.get("height").getAsInt();
            //graphics2D.fillRect(x,y, 100,100);
            //graphics2D.drawOval(x - 50, y - 50, 101, 101);
            graphics2D.drawRect(x - w / 2, y - h / 2, w, h);
            System.out.println(el.getAsJsonObject().get("confidence") + " " + el.getAsJsonObject().get("label")
                    + " x=" + x + " y=" + y);
            System.out.println(el.toString());
        }

        ImageIO.write(bi, "png", new File(fname + ".result.png"));
    }
}
