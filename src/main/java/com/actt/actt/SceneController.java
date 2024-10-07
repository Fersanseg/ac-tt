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

    public void showScene(SCENES newScene, Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(sceneResources.get(newScene))));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles.css");
        stage.setScene(scene);
    }

}
