package com.actt.actt;

import com.actt.actt.controls.Dropdown;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private Dropdown tournamentsComboBox;
    private ObservableList<String> tournamentsList = FXCollections.observableArrayList(
            "2024 WEC",
            "2001 Formula One World Championship",
            "1980 BMW M1 Procar Championship"
    );

    @FXML
    protected void onHelloButtonClick() {
        if (Objects.equals(welcomeText.getText(), "")) {
            welcomeText.setText("Welcome to JavaFX Application!");
        }
        else {
            welcomeText.setText("");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resouces) {
        tournamentsComboBox.setItems(tournamentsList);
        tournamentsComboBox.setValue(tournamentsList.getFirst());
        tournamentsComboBox.setPrefWidth(600);
        tournamentsComboBox.setPrefHeight(50);
    }
}