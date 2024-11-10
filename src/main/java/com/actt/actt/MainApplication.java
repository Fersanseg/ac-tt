package com.actt.actt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        var controller = fxmlLoader.getController();

        scene.getStylesheets().add("styles.css");
        stage.setTitle("Assetto Corsa Tournament Tracker");
        stage.setScene(scene);
        stage.show();

        if (controller instanceof MainController) {
            ((MainController)controller).setTournamentsList(null);
        }
    }
}
