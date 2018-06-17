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
        String fname = "/home/denny/Pictures/card-detect/train/13.jpg";
        BufferedImage bi = ImageIO.read(new File(fname));
        Graphics2D graphics2D = (Graphics2D) bi.getGraphics();
        graphics2D.setStroke(new BasicStroke(10));

        JsonElement all = new JsonParser().parse("[{'confidence': 0.7578293020016177, 'type': 'rectangle', 'coordinates': {'y': 1974.9165887278984, 'x': 3395.0186471494444, 'width': 153.54332322340724, 'height': 118.75277753976661}, 'label': '7'}, {'confidence': 0.618090118774687, 'type': 'rectangle', 'coordinates': {'y': 991.502550568883, 'x': 3405.573837485959, 'width': 147.19228539100004, 'height': 116.35032705160302}, 'label': '7'}, {'confidence': 0.5861128608034328, 'type': 'rectangle', 'coordinates': {'y': 1016.2144059249724, 'x': 3178.8128878386133, 'width': 135.93003317026023, 'height': 110.70767762110779}, 'label': '7'}, {'confidence': 0.540511689757704, 'type': 'rectangle', 'coordinates': {'y': 2747.927791156006, 'x': 2953.154297795345, 'width': 174.5182184072637, 'height': 116.85276537675145}, 'label': '7'}, {'confidence': 0.5355087127866173, 'type': 'rectangle', 'coordinates': {'y': 262.8736883359065, 'x': 1075.9187025246956, 'width': 166.2910361656776, 'height': 161.85129040938156}, 'label': '7'}, {'confidence': 0.5307645592935113, 'type': 'rectangle', 'coordinates': {'y': 261.08338505357847, 'x': 3398.676035070819, 'width': 153.82355557955225, 'height': 125.37249051607574}, 'label': '7'}, {'confidence': 0.5301525316346084, 'type': 'rectangle', 'coordinates': {'y': 1966.4132231718397, 'x': 1101.6357050666284, 'width': 165.31252758319567, 'height': 118.88829363309424}, 'label': '7'}, {'confidence': 0.4833900786957755, 'type': 'rectangle', 'coordinates': {'y': 2237.0728433625304, 'x': 2392.8962470025854, 'width': 149.26824540358348, 'height': 125.87359780531642}, 'label': '7'}, {'confidence': 0.463721959611154, 'type': 'rectangle', 'coordinates': {'y': 1050.7971846018686, 'x': 1453.2069412587484, 'width': 126.96155019906882, 'height': 109.75423996265124}, 'label': '7'}, {'confidence': 0.46038840031588857, 'type': 'rectangle', 'coordinates': {'y': 1483.8867359687579, 'x': 2191.5914271854763, 'width': 123.89722207876366, 'height': 118.95035318227906}, 'label': '7'}, {'confidence': 0.4479875309080017, 'type': 'rectangle', 'coordinates': {'y': 1044.601964855009, 'x': 1175.5659395473651, 'width': 136.25804725060107, 'height': 103.93607403681813}, 'label': '7'}, {'confidence': 0.43810582852182245, 'type': 'rectangle', 'coordinates': {'y': 1528.2116807295163, 'x': 2005.7250546919427, 'width': 116.3496322631836, 'height': 113.98034136111937}, 'label': '7'}, {'confidence': 0.4173398665873882, 'type': 'rectangle', 'coordinates': {'y': 205.04525573596746, 'x': 3419.2342654632484, 'width': 183.83668987567626, 'height': 127.20528536576495}, 'label': '7'}, {'confidence': 0.37287172435012117, 'type': 'rectangle', 'coordinates': {'y': 1327.6522383272118, 'x': 2222.8043748447, 'width': 132.5557585496167, 'height': 121.87764776670019}, 'label': '7'}, {'confidence': 0.37256896059925615, 'type': 'rectangle', 'coordinates': {'y': 1083.6435407791218, 'x': 2278.690009003456, 'width': 124.4047041672925, 'height': 108.77555421682496}, 'label': '7'}, {'confidence': 0.3498891280132971, 'type': 'rectangle', 'coordinates': {'y': 346.3296231610416, 'x': 2310.376236182944, 'width': 133.6285488422095, 'height': 125.22312333033636}, 'label': '7'}, {'confidence': 0.34677014839840825, 'type': 'rectangle', 'coordinates': {'y': 831.5859235064831, 'x': 1868.757658935171, 'width': 122.12443014291603, 'height': 92.26231373273401}, 'label': '7'}, {'confidence': 0.332680189917853, 'type': 'rectangle', 'coordinates': {'y': 873.6948081916731, 'x': 3432.3695443029255, 'width': 132.71815725473243, 'height': 113.0988212365371}, 'label': '7'}, {'confidence': 0.322802217360602, 'type': 'rectangle', 'coordinates': {'y': 1746.3741917947643, 'x': 3086.969998193881, 'width': 139.50545443021338, 'height': 115.83628375713624}, 'label': '7'}, {'confidence': 0.3078413132634628, 'type': 'rectangle', 'coordinates': {'y': 836.7496878364618, 'x': 2248.617612857018, 'width': 114.91493577223582, 'height': 96.54395815042346}, 'label': '7'}, {'confidence': 0.2964718673100982, 'type': 'rectangle', 'coordinates': {'y': 1236.9291060342834, 'x': 1508.104676060706, 'width': 128.8658805260293, 'height': 102.46263808470508}, 'label': '7'}, {'confidence': 0.2796532405098615, 'type': 'rectangle', 'coordinates': {'y': 876.5931897445387, 'x': 3177.3075322719183, 'width': 129.29394413874707, 'height': 119.60454346583435}, 'label': '7'}, {'confidence': 0.26393710133250764, 'type': 'rectangle', 'coordinates': {'y': 1074.0250347229091, 'x': 2778.0659110839724, 'width': 121.02568465012746, 'height': 114.45950735532313}, 'label': '7'}, {'confidence': 0.2575687142777809, 'type': 'rectangle', 'coordinates': {'y': 1222.613779432219, 'x': 1127.3966596179757, 'width': 142.08482947716334, 'height': 102.3873028388391}, 'label': '7'}, {'confidence': 0.2567079290880115, 'type': 'rectangle', 'coordinates': {'y': 1983.3740435270558, 'x': 1421.5052641391403, 'width': 153.30569634070775, 'height': 114.4299126038186}, 'label': '7'}, {'confidence': 0.25286086299243615, 'type': 'rectangle', 'coordinates': {'y': 1063.053119812203, 'x': 1825.919087939903, 'width': 130.01834810697142, 'height': 106.89016265135558}, 'label': '7'}]");
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
