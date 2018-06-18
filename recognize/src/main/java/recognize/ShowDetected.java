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
        String fname = "/home/denny/Pictures/card-detect/train/17.jpg";
        BufferedImage bi = ImageIO.read(new File(fname));
        Graphics2D graphics2D = (Graphics2D) bi.getGraphics();
        graphics2D.setStroke(new BasicStroke(10));

        JsonElement all = new JsonParser().parse("[{'confidence': 0.7200176260018464, 'type': 'rectangle', 'coordinates': {'y': 2691.6080350498814, 'x': 2847.4202736752986, 'width': 122.94602438119773, 'height': 102.04043626785278}, 'label': '4'}, {'confidence': 0.6406940915394648, 'type': 'rectangle', 'coordinates': {'y': 2640.5342480783993, 'x': 2928.5748053892003, 'width': 127.63590475229148, 'height': 90.29329567689138}, 'label': '4'}, {'confidence': 0.5509958498089159, 'type': 'rectangle', 'coordinates': {'y': 539.6351540131998, 'x': 1456.152027473851, 'width': 85.213984856239, 'height': 120.20844554901123}, 'label': '4'}, {'confidence': 0.47798988624605565, 'type': 'rectangle', 'coordinates': {'y': 1802.1541275261297, 'x': 3526.6318370062672, 'width': 137.95601771428028, 'height': 114.51110579417332}, 'label': '7'}, {'confidence': 0.43678297613297734, 'type': 'rectangle', 'coordinates': {'y': 793.7695918554734, 'x': 3184.048783484824, 'width': 75.70637688269926, 'height': 121.46230910374561}, 'label': '7'}, {'confidence': 0.403092169762869, 'type': 'rectangle', 'coordinates': {'y': 1889.1092383797495, 'x': 3539.2018600982965, 'width': 135.975399310772, 'height': 112.02311545151929}, 'label': '7'}, {'confidence': 0.36971695287962153, 'type': 'rectangle', 'coordinates': {'y': 2453.7216408442064, 'x': 1961.739778386289, 'width': 97.52958561823903, 'height': 99.92698001861572}, 'label': '4'}, {'confidence': 0.35129098403355385, 'type': 'rectangle', 'coordinates': {'y': 504.1582695634779, 'x': 1864.9388200852754, 'width': 77.00278736994801, 'height': 121.12573726360614}, 'label': '4'}, {'confidence': 0.3326973391685428, 'type': 'rectangle', 'coordinates': {'y': 1469.3796574031196, 'x': 1595.6906891082108, 'width': 98.71294432419995, 'height': 122.73641549623926}, 'label': '4'}, {'confidence': 0.3209205320138175, 'type': 'rectangle', 'coordinates': {'y': 1282.36544989698, 'x': 1853.7252407036979, 'width': 75.15725678663966, 'height': 142.74313303140502}, 'label': '4'}, {'confidence': 0.31114821021933087, 'type': 'rectangle', 'coordinates': {'y': 152.4036572695383, 'x': 2568.422949649269, 'width': 103.36132225623533, 'height': 117.12722220787634}, 'label': '7'}, {'confidence': 0.3080773509154397, 'type': 'rectangle', 'coordinates': {'y': 1894.3501679775477, 'x': 3006.1443165081396, 'width': 129.62040534386233, 'height': 118.4918113121621}, 'label': '7'}, {'confidence': 0.29487030992947283, 'type': 'rectangle', 'coordinates': {'y': 2652.071335069161, 'x': 1984.4746398797947, 'width': 113.48764624962428, 'height': 79.91642189025879}, 'label': '4'}, {'confidence': 0.28964287075772543, 'type': 'rectangle', 'coordinates': {'y': 2705.2627692771534, 'x': 2412.188066427664, 'width': 114.04215827354983, 'height': 98.64323054827219}, 'label': '4'}, {'confidence': 0.26522737561918536, 'type': 'rectangle', 'coordinates': {'y': 1334.9274632250742, 'x': 1612.0022059873995, 'width': 99.7454769427959, 'height': 133.3253191434419}, 'label': '4'}, {'confidence': 0.2589360616885025, 'type': 'rectangle', 'coordinates': {'y': 656.0376766901566, 'x': 3199.689252654065, 'width': 70.20821879460209, 'height': 133.30766905271093}, 'label': '4'}, {'confidence': 0.25133364725644997, 'type': 'rectangle', 'coordinates': {'y': 1058.086269101665, 'x': 2683.1461735921284, 'width': 77.43598409799415, 'height': 122.8561948629524}, 'label': '7'}, {'confidence': 0.25121033476259863, 'type': 'rectangle', 'coordinates': {'y': 1830.984681273621, 'x': 2991.9669767187197, 'width': 121.1629292414741, 'height': 110.44486735417308}, 'label': '7'}]");
        for (JsonElement el : all.getAsJsonArray()) {
            JsonObject coord = el.getAsJsonObject().get("coordinates").getAsJsonObject();
            int x = coord.get("x").getAsInt();
            int y = coord.get("y").getAsInt();
            int w = coord.get("width").getAsInt();
            int h = coord.get("height").getAsInt();
            //graphics2D.fillRect(x,y, 100,100);
            //graphics2D.drawOval(x - 50, y - 50, 101, 101);
            //graphics2D.setColor(Color.red);
            String label = el.getAsJsonObject().get("label").getAsString();
            graphics2D.setColor(RectColors.toAwtColor(RectColors.rectColor(label)));
            graphics2D.drawRect(x - w / 2, y - h / 2, w, h);
            System.out.println(el.getAsJsonObject().get("confidence") + " " + label
                    + " x=" + x + " y=" + y);
            System.out.println(el.toString());
        }

        ImageIO.write(bi, "png", new File(fname + ".result.png"));
    }
}
