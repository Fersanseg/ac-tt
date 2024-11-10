package com.actt.actt;

import com.actt.actt.controls.Dropdown;
import com.actt.actt.controls.HeaderButtonBar;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.TournamentSettings;
import com.actt.actt.utils.AppData;
import com.actt.actt.utils.FileOperations;
import com.actt.actt.utils.Logger;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private SceneController sceneController;

    @FXML
    private AnchorPane ap;

    @FXML
    private Label welcomeText;

    @FXML
    private Dropdown tournamentsComboBox;

    @FXML
    private HeaderButtonBar headerButtonBar;
    private ObservableList<TournamentSettings> tournamentsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneController = new SceneController();

        tournamentsComboBox.setPrefWidth(600);
        tournamentsComboBox.setPrefHeight(50);

        headerButtonBar.addEventHandler(SendDataEvent.SEND_DATA, event -> {
            try {
                if (event.getData("btnName").isPresent()) {
                    String btnName = (String)(event.getData("btnName").get());
                    onHeaderButtonPressed(btnName);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            FileOperations.checkAppConfig();
            AppData.loadCars();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTournamentsList(String name) throws IOException {
        File[] tournamentsFolders = FileOperations.getTournaments();
        ObjectMapper mapper = new ObjectMapper();
        TournamentSettings objToSelect = null;
        ObservableList<TournamentSettings> tournamentList = FXCollections.observableArrayList();

        AppData.setTournamentFolders(tournamentsFolders);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        tournamentsComboBox.getItems().clear();
        tournamentsComboBox.setValue(null);


        for (var folder : tournamentsFolders) {
            Optional<File> configOpt = Arrays.stream(folder.listFiles(f -> f.getName().equals("config.json"))).findFirst();
            if (configOpt.isEmpty()) {
                continue;
            }

            File config = configOpt.get();
            try {
                TournamentSettings settings = mapper.readValue(config, TournamentSettings.class);
                if (settings.getName().equals(name)) {
                    objToSelect = settings;
                }

                tournamentList.add(settings);
            }
            catch (Exception _) {

            }
        }

        tournamentsComboBox.setItems(tournamentList);
        if (objToSelect != null) {
            tournamentsComboBox.setValue(objToSelect);
        }
    }

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
        String selectedTournament = ((Dropdown)ev.getTarget()).getValue().getName();
        System.out.println("SELECTED TOURNAMENT: " + selectedTournament);
    }

    @FXML
    protected void onHeaderButtonPressed(String buttonId) throws IOException {
        switch (buttonId) {
            case "addButton":
                openTournamentEditor(true);
                break;
            case "editButton":
                openTournamentEditor(false);
                break;
            case "deleteButton":
                deleteTournament();
                break;
            case "refreshButton":
                loadTournament();
                break;
            case "configButton":
                config();
                break;
            default:
                Logger.log("Unrecognized button press ("+buttonId+")");
                break;
        }
    }

    private void loadTournament() {
        System.out.println("LOAD TOURNAMENT");
    }

    private void openTournamentEditor(boolean isCreateMode) throws IOException {
        System.out.println("EDIT/CREATE TOURNAMENT ("+isCreateMode+")");
        var controller = sceneController.showScene(SceneController.SCENES.EDIT, (Stage) ap.getScene().getWindow());
        if (controller instanceof EditTournament) {
            String label = isCreateMode ? "New tournament" : "Edit tournament";
            ((EditTournament) controller).setTitle(label);
        }
    }

    private void deleteTournament() {
        System.out.println("DELETE TOURNAMENT");
    }

    private void config() throws IOException {
        FileOperations.showConfigDialog(false);
    }

}