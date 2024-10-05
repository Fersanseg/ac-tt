package com.actt.actt;

import com.actt.actt.controls.Dropdown;
import com.actt.actt.controls.HeaderButtonBar;
import com.actt.actt.events.ButtonPressedEvent;
import com.actt.actt.utils.FileOperations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private Dropdown tournamentsComboBox;

    @FXML
    private HeaderButtonBar headerButtonBar;
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

    @FXML
    protected void onDropdownSelect(ActionEvent ev) {
        String selectedTournament = ((Dropdown)ev.getTarget()).getValue();
        System.out.println("SELECTED TOURNAMENT: " + selectedTournament);
    }

    @FXML
    protected void onHeaderButtonPressed(String buttonId) {
        System.out.println("BUTTON PRESSED: " + buttonId);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tournamentsComboBox.setItems(tournamentsList);
        tournamentsComboBox.setPrefWidth(600);
        tournamentsComboBox.setPrefHeight(50);

        headerButtonBar.addEventHandler(ButtonPressedEvent.BUTTON_PRESSED, event -> onHeaderButtonPressed(event.getButtonId()));

        try {
            FileOperations.checkAppConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}