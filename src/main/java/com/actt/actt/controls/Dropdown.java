package com.actt.actt.controls;

import com.actt.actt.controls.cellFactories.TournamentCell;
import com.actt.actt.models.TournamentSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.io.IOException;

public class Dropdown extends ComboBox<TournamentSettings>  {
    @FXML
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
        setCellFactory(_ -> new TournamentCell());
        setConverter(new StringConverter<>() {
            @Override
            public String toString(TournamentSettings model) {
                return model == null ? "" : model.getName();
            }

            @Override
            public TournamentSettings fromString(String s) {
                var model = new TournamentSettings();
                model.setName(s);
                return model;
            }
        });

        try {
            fxmlLoader.load();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
