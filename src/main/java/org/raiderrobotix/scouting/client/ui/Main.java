package org.raiderrobotix.scouting.client.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point to the program
 * Responsible for creating the main scene and initializing the TBA API
 */
public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/main.fxml"));

            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/team_25_logo.png")));
            primaryStage.setTitle("Raider Robotix Scouting Client");
            primaryStage.setScene(new Scene(root, 820, 400));
            primaryStage.setResizable(false);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (NullPointerException np) {
            np.printStackTrace();
        }


    }
}