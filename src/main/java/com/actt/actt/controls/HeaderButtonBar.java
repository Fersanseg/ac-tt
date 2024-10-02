package com.actt.actt.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HeaderButtonBar extends HBox {
    @FXML
    private Button refreshButton;

    public HeaderButtonBar() {
        setup();
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("headerButtonBar.fxml"));
        getStyleClass().add("custom-button-bar");
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            setButtonIcons();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setButtonIcons() {
        String svgContent = loadSVGFromFile("/com/actt/actt/images/reload.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.getTransforms().add(new Translate(1, 0));

        svgPath.setFill(Color.SILVER);

        refreshButton.setGraphic(svgPath);
    }

    private String loadSVGFromFile(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SVG file", e);
        }
    }
}
