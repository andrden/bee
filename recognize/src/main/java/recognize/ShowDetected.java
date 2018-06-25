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
        String fname = "/home/denny/Pictures/card-detect/train/18.jpg";
        BufferedImage bi = ImageIO.read(new File(fname));
        Graphics2D graphics2D = (Graphics2D) bi.getGraphics();
        graphics2D.setStroke(new BasicStroke(10));

        JsonElement all = new JsonParser().parse("[{'confidence': 0.48028843612018063, 'type': 'rectangle', 'coordinates': {'y': 1059.2818553570273, 'x': 1539.5112655807272, 'width': 179.59075927734375, 'height': 128.92359726245593}, 'label': '7'}, {'confidence': 0.46066933433624313, 'type': 'rectangle', 'coordinates': {'y': 1056.8242746321628, 'x': 1857.029966107385, 'width': 157.07266352726833, 'height': 121.69501854823204}, 'label': '7'}, {'confidence': 0.44351783574596165, 'type': 'rectangle', 'coordinates': {'y': 2933.370295026938, 'x': 3644.4561791863407, 'width': 214.22377131535404, 'height': 143.02959691561182}, 'label': '7'}, {'confidence': 0.39841541124390545, 'type': 'rectangle', 'coordinates': {'y': 2668.1264815859104, 'x': 3141.4962802827276, 'width': 203.56839752197266, 'height': 155.81484845968407}, 'label': '7'}, {'confidence': 0.3647215711125525, 'type': 'rectangle', 'coordinates': {'y': 2705.280340482705, 'x': 1864.4474136068943, 'width': 267.50082514836254, 'height': 156.32875310457712}, 'label': '4'}, {'confidence': 0.3454812777263981, 'type': 'rectangle', 'coordinates': {'y': 2705.280340482705, 'x': 1864.4474136068943, 'width': 267.50082514836254, 'height': 156.32875310457712}, 'label': '7'}, {'confidence': 0.33684782597520363, 'type': 'rectangle', 'coordinates': {'y': 1055.6403566741672, 'x': 2295.341005482245, 'width': 162.04636265681347, 'height': 115.7186049314646}, 'label': '7'}, {'confidence': 0.3344842990682328, 'type': 'rectangle', 'coordinates': {'y': 2732.5335433361715, 'x': 2209.6443019275475, 'width': 249.0557732215293, 'height': 147.18306673490088}, 'label': '7'}, {'confidence': 0.32777084654505567, 'type': 'rectangle', 'coordinates': {'y': 137.67947404093678, 'x': 1892.3615939537096, 'width': 210.09869384765625, 'height': 115.25568866729733}, 'label': '7'}, {'confidence': 0.2977565879767563, 'type': 'rectangle', 'coordinates': {'y': 1756.0489310547234, 'x': 3951.4367171157046, 'width': 207.31325472318213, 'height': 134.81689739227295}, 'label': '7'}, {'confidence': 0.288237468371123, 'type': 'rectangle', 'coordinates': {'y': 1118.460462611899, 'x': 3045.615923784154, 'width': 180.4530610304614, 'height': 118.4745217103225}, 'label': '4'}, {'confidence': 0.28141318427023243, 'type': 'rectangle', 'coordinates': {'y': 2673.1519139605643, 'x': 1555.14518962406, 'width': 261.40692373422485, 'height': 162.24307250976562}, 'label': '7'}, {'confidence': 0.27408340053224417, 'type': 'rectangle', 'coordinates': {'y': 351.8647788061327, 'x': 3063.394757006121, 'width': 154.1078180166387, 'height': 105.91035384398236}, 'label': '7'}, {'confidence': 0.260971343538155, 'type': 'rectangle', 'coordinates': {'y': 142.29087402672798, 'x': 2230.4056593467803, 'width': 206.74933917705812, 'height': 116.60297430478609}, 'label': '7'}]");
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
