package com.actt.actt.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Filepicker extends HBox {
    public Filepicker() {
        setup();
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("filepicker.fxml"));
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
