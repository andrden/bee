package recognize;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawBoundingBoxes2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Image image2;
    double startX;
    double startY;

    String rectType;
    Map<Rectangle, String> rectangles = new HashMap<>();

    Color rectColor(String type) {
        if (type.equals("h")) return Color.RED;
        if (type.equals("d")) return Color.ORANGE;
        if (type.equals("s")) return Color.BLACK;
        if (type.equals("c")) return Color.GRAY;
        if (type.equals("7")) return Color.GREEN;
        throw new IllegalArgumentException(type);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        image2 = new Image(new FileInputStream("/home/denny/Pictures/card-detect/train/1.jpg"),
                800, 800, true, true);

        primaryStage.setTitle("Drawing Bounding Boxes");
        Group root = new Group();
        Canvas canvas = new Canvas(900, 900);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(keyEvent -> {
            rectType = keyEvent.getText();
            System.out.println("rectType=" + rectType);
        });
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();

            drawShapes(gc);
            gc.setStroke(rectColor(rectType));
            gc.setLineWidth(5);
            gc.strokeRect(event.getX(), event.getY(), 1, 1);
        });
        canvas.setOnMouseDragged(event -> {
            drawShapes(gc);
            gc.setStroke(rectColor(rectType));
            gc.setLineWidth(2);
            gc.strokeRect(startX, startY, event.getX() - startX, event.getY() - startY);
        });
        canvas.setOnMouseReleased(event -> {
            rectangles.put(
                    new Rectangle(startX, startY, event.getX() - startX, event.getY() - startY),
                    rectType);
            System.out.println("rectangles size = "+rectangles.size());
        });
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) {
        gc.drawImage(image2, 0, 0);
        rectangles.forEach((r, t) -> {
            gc.setStroke(rectColor(t));
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