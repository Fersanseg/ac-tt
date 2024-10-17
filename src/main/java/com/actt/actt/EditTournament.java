package com.actt.actt;

import com.actt.actt.utils.Utils;
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
import java.net.URL;
import java.util.ResourceBundle;

public class EditTournament implements Initializable {
    public Label editorMode;
    public Button backButton;
    public Button addClassButton;

    private SceneController sceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneController = new SceneController();
        setBackButtonIcon();
        setAddButtonIcon();
    }

    private void setBackButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/back.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(2.5, 2.5));
        svgPath.getTransforms().add(new Translate(-7, -7));

        assert backButton != null;
        backButton.setGraphic(svgPath);
    }

    private void setAddButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/add.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(1, 1));
        svgPath.getTransforms().add(new Translate(-0, -0));

        assert addClassButton != null;
        addClassButton.setGraphic(svgPath);
    }

    public void goHome(ActionEvent ev) throws IOException {
        sceneController.showScene(SceneController.SCENES.MAIN, (Stage) backButton.getScene().getWindow());
    }
}
