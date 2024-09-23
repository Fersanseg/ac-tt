package com.actt.actt.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class Dropdown extends ComboBox<String>  {
    @FXML
    private String defaultValue;
    private final EventHandler<ActionEvent> onSelectedValue = _ -> getStyleClass().remove("dropdown__placeholder-value");

    public Dropdown() {
        doSetup();
    }

    protected void doSetup() {
        FXMLLoader fxmlLoader = new FXMLLoader(Dropdown.class.getResource("dropdown.fxml"));
        getStyleClass().add("dropdown");
        getStyleClass().add("dropdown__placeholder-value");
        fxmlLoader.setController(this);
        addEventHandler(ActionEvent.ACTION, onSelectedValue);

        try {
            fxmlLoader.load();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unused")
    public String getDefaultValue() {
        return defaultValue;
    }
    @SuppressWarnings("unused")
    public void setDefaultValue(String value) {
        if (defaultValue == null) {
            setValue(value);
        }
        defaultValue = value;
    }

}
