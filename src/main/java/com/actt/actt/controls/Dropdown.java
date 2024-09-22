package com.actt.actt.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class Dropdown extends ComboBox<String> {

    public Dropdown() {
        doSetup();
    }

    protected void doSetup() {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("dropdown.fxml"));
        getStyleClass().add("dropdown");
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
