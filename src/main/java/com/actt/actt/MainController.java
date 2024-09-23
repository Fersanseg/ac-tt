package com.actt.actt;

import com.actt.actt.controls.Dropdown;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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

    @FXML
    protected void onDropdownSelect(ActionEvent ev) {
        String selectedTournament = ((Dropdown)ev.getTarget()).getValue();
        System.out.println("SELECTED TOURNAMENT: " + selectedTournament);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tournamentsComboBox.setItems(tournamentsList);
        tournamentsComboBox.setPrefWidth(600);
        tournamentsComboBox.setPrefHeight(50);

        checkAppConfig();
    }

    public void checkAppConfig() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            // check config on AppData/Local
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // TODO check config on linux OS
        }
        else {
            showInvalidOSError();
        }
    }

    private void showInvalidOSError() {
        ButtonType closeButton = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Unsupported OS");
        dialog.setContentText("Your Operating System is not currently supported!\nThe app only supports Windows at " +
                "this time. Support for other OSs will come soon!");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.showAndWait().ifPresent(r -> {
            if (r.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                System.exit(1);
            }
        });
    }
}