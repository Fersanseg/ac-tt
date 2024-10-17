package com.actt.actt.controls;

import com.actt.actt.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CarClass extends AnchorPane implements Initializable {
    public Button addCarButton;
    public Button deleteClassButton;

    public CarClass() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("car-class.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            try {
            fxmlLoader.load();
        }
            catch (
        IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAddCarButtonIcon();
        setDeleteClassButtonIcon();
    }

    private void setAddCarButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/add.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(0.8, 0.8));
        svgPath.getTransforms().add(new Translate(-0, -0));

        assert addCarButton != null;
        addCarButton.setGraphic(svgPath);
    }

    private void setDeleteClassButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/delete.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(0.8, 0.8));
        svgPath.getTransforms().add(new Translate(-10, -0));

        assert deleteClassButton != null;
        deleteClassButton.setGraphic(svgPath);

    }
}
