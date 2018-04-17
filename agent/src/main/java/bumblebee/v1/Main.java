package bumblebee.v1;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bumblebee");
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.TRANSPARENT);
        primaryStage.setScene(scene);

        final Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLUE);
        gc.fillRect(75, 75, 100, 100);

        root.getChildren().add(canvas);

        primaryStage.show();

        //DoubleProperty x  = new SimpleDoubleProperty();
        LongProperty t = new SimpleLongProperty();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(t, 50)/*,
                        new KeyValue(x, 0),
                        new KeyValue(y, 0)*/
                ),
                new KeyFrame(Duration.seconds(25), new KeyValue(t, 350)/*,
                        new KeyValue(x, W - D),
                        new KeyValue(y, H - D)*/
                )
        );
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);

        Bumblebee bumblebee = new Bumblebee((int) canvas.getWidth(), (int) canvas.getHeight());
        Image image = SwingFXUtils.toFXImage(bumblebee.bufferedImage, null);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
//                gc.setFill(Color.WHITE);
//                gc.fillRect(0,0, canvas.getHeight(), canvas.getHeight());
                gc.drawImage(image, 0, 0);
                //drawBumblebee(gc, t.doubleValue(), t.doubleValue(), t.doubleValue() / 100);
                bumblebee.next();
                drawBumblebee(gc, bumblebee.xy.getX(), bumblebee.xy.getY(), bumblebee.rotation);
                System.out.println(t);
//                gc.setFill(Color.FORESTGREEN);
//                gc.fillOval(
//                        x.doubleValue(),
//                        y.doubleValue(),
//                        D,
//                        D
//                );
            }
        };

        timeline.setOnFinished((e) -> timer.stop());
        timer.start();
        timeline.play();
    }

    void drawBumblebee(GraphicsContext gc, double x, double y, double rotation) {
        int radius = 10;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.strokeLine(x, y, x + 15 * Math.cos(rotation), y + 15 * Math.sin(rotation));

    }

    public static void main(String[] args) {
        System.out.println("Started");
        launch(args);
    }
}