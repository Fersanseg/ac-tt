package com.actt.actt.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Filepicker extends HBox {
    @FXML
    private Button button;
    @FXML
    private TextField textField;

    public Filepicker() {
        setup();
    }

    public Filepicker(String buttonText, String textFieldPlaceholder) {
        setup();
        button.setText(buttonText);
        textField.setText(textFieldPlaceholder);
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("filepicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            textField.setDisable(true);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
