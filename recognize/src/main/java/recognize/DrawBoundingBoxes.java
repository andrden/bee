package recognize;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
        import javafx.scene.Group;
        import javafx.scene.Scene;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
        import javafx.scene.layout.HBox;
        import javafx.scene.paint.Color;
        import javafx.stage.Stage;

import java.io.FileInputStream;

public class DrawBoundingBoxes extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Title");
        Group root = new Group();
        Scene scene = new Scene(root, 900, 900, Color.WHITE);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        final ImageView imv = new ImageView(){

        };
        //final Image image2 = new Image(Main.class.getResourceAsStream("button.png"));
        final Image image2 = new Image(new FileInputStream("/home/denny/Pictures/card-detect/IMG_20180609_100907.jpg"),
                800, 800, true, true);
        imv.setImage(image2);

        imv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 1, 1);


        root.getChildren().add(gridpane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

