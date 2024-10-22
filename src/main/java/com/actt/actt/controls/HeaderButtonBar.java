package com.actt.actt.controls;

import com.actt.actt.events.SendDataEvent;
import com.actt.actt.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HeaderButtonBar extends HBox {
    @FXML
    private Button refreshButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button configButton;

    public HeaderButtonBar() {
        setup();
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("headerButtonBar.fxml"));
        getStyleClass().add("custom-button-bar");
        setSpacing(35);
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
        List<Button> buttons = Utils.getFieldsOfType(this, Button.class);
        for (Button button : buttons) {
            setButtonIcon(button);
        }
    }

    private void setButtonIcon(Button button) {
        String buttonName = button.getId();
        String iconFileName = buttonName.replace("Button", "") + ".svg";

        button.setPadding(new Insets(5, 5, 5, 5));
        button.setPrefSize(50, 50);
        button.setOnAction(_ -> {
            Map<String, Object> d = new HashMap<>();
            d.put("btnName", buttonName);
            fireEvent(new SendDataEvent(d));
        });

        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/" + iconFileName);
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        if (buttonName.equals("refreshButton") || buttonName.equals("editButton")) {
            svgPath.getTransforms().add(new Scale(1.5, 1.5));
            svgPath.getTransforms().add(new Translate(-3.5, -4.2));
        }

        svgPath.setFill(Color.SILVER);

        button.setGraphic(svgPath);
    }
}
