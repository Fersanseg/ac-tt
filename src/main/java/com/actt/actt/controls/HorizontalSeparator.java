package com.actt.actt.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HorizontalSeparator extends VBox {
    @FXML
    private Pane separator;

    public HorizontalSeparator() {
        setup();
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("horizontalSeparator.fxml"));
        getStyleClass().add("separator-horizontal--container");
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
