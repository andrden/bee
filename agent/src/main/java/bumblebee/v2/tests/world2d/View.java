package bumblebee.v2.tests.world2d;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class View extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bumblebee");
        Group root = new Group();
        Scene scene = new Scene(root, World.SIZE, World.SIZE, Color.TRANSPARENT);
        primaryStage.setScene(scene);

        final Canvas canvas = new Canvas(World.SIZE, World.SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

//        gc.setFill(Color.BLUE);
//        gc.fillRect(75, 75, 100, 100);

        root.getChildren().add(canvas);

        primaryStage.show();

        //DoubleProperty x  = new SimpleDoubleProperty();
        LongProperty t = new SimpleLongProperty();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(t, 0)/*,
                        new KeyValue(x, 0),
                        new KeyValue(y, 0)*/
                ),
                new KeyFrame(Duration.seconds(250), new KeyValue(t, 250)/*,
                        new KeyValue(x, W - D),
                        new KeyValue(y, H - D)*/
                )
        );
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);

        World world = new World();
        //Bumblebee bumblebee = new Bumblebee((int) canvas.getWidth(), (int) canvas.getHeight());
        //Image image = SwingFXUtils.toFXImage(bumblebee.bufferedImage, null);

        AtomicLong lastOp = new AtomicLong();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if( now - lastOp.get() < TimeUnit.MILLISECONDS.toNanos(80)) return;
                lastOp.set(now);

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0,0, canvas.getHeight(), canvas.getHeight());
                world.draw(gc);
                world.next();
                //gc.drawImage(image, 0, 0);
                //drawBumblebee(gc, t.doubleValue(), t.doubleValue(), t.doubleValue() / 100);
                //bumblebee.next();
                //drawBumblebee(gc, bumblebee.xy.getX(), bumblebee.xy.getY(), bumblebee.rotation);
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

    public static void main(String[] args) {
        System.out.println("Started");
        launch(args);
    }
}