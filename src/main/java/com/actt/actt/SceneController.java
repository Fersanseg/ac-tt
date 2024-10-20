package com.actt.actt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SceneController {
    public enum SCENES {
        MAIN,
        EDIT
    }

    private final Map<SCENES, String> sceneResources;

    public SceneController() {
        sceneResources = new HashMap<>();
        sceneResources.put(SCENES.MAIN, "main-view.fxml");
        sceneResources.put(SCENES.EDIT, "edit-tournament.fxml");
    }

    public Object showScene(SCENES newScene, Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneResources.get(newScene)));
        Parent root = loader.load();

        var controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles.css");
        stage.setScene(scene);

        return controller;
    }

}
