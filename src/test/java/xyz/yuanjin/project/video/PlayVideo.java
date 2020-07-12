package xyz.yuanjin.project.video;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * @author yuanjin
 */
public class PlayVideo extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Locate the media content in the CLASSPATH
//        URL mediaUrl = getClass().getResource("");
//        String mediaStringUrl = mediaUrl.toExternalForm();

        String mediaStringUrl = new File("F:\\900_电影\\愤怒的小鸟\\愤怒的小鸟-中文.mp4").getCanonicalPath();


        // Create a Media
        Media media = new Media(mediaStringUrl);

        // Create a Media Player
        final MediaPlayer player = new MediaPlayer(media);
        // Automatically begin the playback
        player.setAutoPlay(true);

        // Create a 400X300 MediaView
        MediaView mediaView = new MediaView(player);

        mediaView.setFitWidth(400);
        mediaView.setFitHeight(300);
        mediaView.setSmooth(true);
        mediaView.setLayoutX(200);
        mediaView.setLayoutY(200);
        // Create the DropShadow effect
        DropShadow dropshadow = new DropShadow();
        dropshadow.setOffsetY(5.0);
        dropshadow.setOffsetX(5.0);
        dropshadow.setColor(Color.RED);

        mediaView.setEffect(dropshadow);

        Rectangle rect4 = new Rectangle(35, 55, 95, 25);
        rect4.setFill(Color.RED);
        rect4.setStroke(Color.BLACK);
        rect4.setStrokeWidth(1);

        // Create the HBox
        // HBox controlBox = new HBox(5, null, null);

        // Create the VBox
        VBox root = new VBox(1, mediaView);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(95));
        gridpane.setHgap(1);
        gridpane.setVgap(10);

        GridPane.setHalignment(rect4, HPos.CENTER);

        Group grp = new Group();
        gridpane.add(root, 1, 1);

        grp.getChildren().add(gridpane);

        // Create the Scene
        Scene scene = new Scene(grp);

        // Add the scene to the Stage
        stage.setScene(scene);
        // Set the title of the Stage
        stage.setTitle("A simple Media Example");
        // Display the Stage
        stage.show();
    }
}
