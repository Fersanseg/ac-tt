package com.actt.actt;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Scanner;

public class EditTournament implements Initializable {
    public Label editorMode;
    public Button backButton;

    private SceneController sceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneController = new SceneController();
        setBackButtonIcon();
    }

    private void setBackButtonIcon() {
        String svgContent = loadSVGFromFile("/com/actt/actt/images/back.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(2.5, 2.5));
        svgPath.getTransforms().add(new Translate(-7, -7));

        assert backButton != null;
        backButton.setGraphic(svgPath);
        backButton.setOnAction(null);
    }

    private String loadSVGFromFile(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            assert inputStream != null;
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SVG file", e);
        }
    }


    public void goHome(ActionEvent ev) throws IOException {
        sceneController.showScene(SceneController.SCENES.MAIN, (Stage) backButton.getScene().getWindow());
    }
}
