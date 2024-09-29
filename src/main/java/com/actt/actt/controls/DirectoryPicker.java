package com.actt.actt.controls;

import com.actt.actt.events.DirectoryChosenEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

public class DirectoryPicker extends HBox {
    @FXML
    private Button button;
    @FXML
    private TextField pathTextField;

    public DirectoryPicker(String buttonText, String textFieldPlaceholder) {
        setup(buttonText, textFieldPlaceholder);
    }

    public Button getButton() {
        return button;
    }

    public TextField getPathTextField() {
        return pathTextField;
    }

    public String getPath() {
        return getPathTextField().getText();
    }

    private void setup(String buttonText, String textFieldPlaceholder) {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("directoryPicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            assert button != null;
            button.setText(buttonText);
            button.setDefaultButton(true);
            button.setOnAction(onShowDirectoryPicker);
            assert pathTextField != null;
            pathTextField.setDisable(true);
            pathTextField.setText(textFieldPlaceholder);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final EventHandler<ActionEvent> onShowDirectoryPicker = _ -> {
        DirectoryChooser filePicker = new DirectoryChooser();
        File folder = filePicker.showDialog(null);
        if (folder != null) {
            pathTextField.setText(folder.toString());
            fireEvent(new DirectoryChosenEvent(folder));
        }
    };
}
