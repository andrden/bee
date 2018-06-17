package recognize;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DrawBoundingBoxes2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    String fname = "/home/denny/Pictures/card-detect/train/13.jpg";

    Image imageView;
    BufferedImage bufferedImage;

    double startX;
    double startY;

    String rectType;
    Map<Rectangle, String> rectangles = new LinkedHashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        imageView = new Image(new FileInputStream(fname), 800, 800, true, true);
        bufferedImage = ImageIO.read(new File(fname));

        primaryStage.setTitle("Drawing Bounding Boxes");
        Group root = new Group();
        Canvas canvas = new Canvas(900, 900);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(keyEvent -> {
            rectType = keyEvent.getText();
            System.out.println("rectType=" + rectType);
            printShapes();
        });
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();

            drawShapes(gc);
            gc.setStroke(RectColors.rectColor(rectType));
            gc.setLineWidth(5);
            gc.strokeRect(event.getX(), event.getY(), 1, 1);
        });
        canvas.setOnMouseDragged(event -> {
            drawShapes(gc);
            gc.setStroke(RectColors.rectColor(rectType));
            gc.setLineWidth(2);
            gc.strokeRect(startX, startY, event.getX() - startX, event.getY() - startY);
        });
        canvas.setOnMouseReleased(event -> {
            Rectangle rectangle = new Rectangle(startX, startY, event.getX() - startX, event.getY() - startY);
            if (rectangle.getHeight() > 5 && rectangle.getWidth() > 5) {
                rectangles.put(rectangle, rectType);
            }
            System.out.println("rectangles size = " + rectangles.size());
        });
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    void printShapes() {
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        double kx = bufferedImage.getWidth() / imageView.getWidth();
        double ky = bufferedImage.getHeight() / imageView.getHeight();
        System.out.println("[");
        rectangles.forEach((r, t) ->
                {
                    int h = (int) Math.round(r.getHeight() * ky);
                    int w = (int) Math.round(r.getWidth() * kx);
                    int x = (int) Math.round((r.getX() + r.getWidth() / 2) * kx);
                    int y = (int) Math.round((r.getY() + r.getHeight() / 2) * ky);
                    System.out.printf("{'coordinates': {'height': %s, 'width': %s, 'x': %s, 'y': %s},  'label': '%s'},\n",
                            h, w,
                            x, y,
                            t);
                    Color c = RectColors.rectColor(t);
                    graphics2D.setColor(new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue()));
                    graphics2D.setStroke(new BasicStroke(3));
                    graphics2D.drawRect(x - w / 2, y - h / 2, w, h);
                }
        );
        System.out.println("]");
        try {
            File output = new File(fname + ".rect.png");
            System.out.println("saving image with recatangles: " + output);
            ImageIO.write(bufferedImage, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawShapes(GraphicsContext gc) {
        gc.drawImage(imageView, 0, 0);
        rectangles.forEach((r, t) -> {
            gc.setStroke(RectColors.rectColor(t));
            gc.setLineWidth(2);
            gc.strokeRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        });

//        gc.setFill(Color.GREEN);
//        gc.setStroke(Color.BLUE);
//        gc.setLineWidth(5);
//        gc.strokeLine(40, 10, 10, 40);
//        gc.fillOval(10, 60, 30, 30);
//        gc.strokeOval(60, 60, 30, 30);
//        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
//        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
//        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
//        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
//        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
//        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
//        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
//        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
//        gc.fillPolygon(new double[]{10, 40, 10, 40},
//                new double[]{210, 210, 240, 240}, 4);
//        gc.strokePolygon(new double[]{60, 90, 60, 90},
//                new double[]{210, 210, 240, 240}, 4);
//        gc.strokePolyline(new double[]{110, 140, 110, 140},
//                new double[]{210, 210, 240, 240}, 4);
    }
}