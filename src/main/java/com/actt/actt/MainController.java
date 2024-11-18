package com.actt.actt;

import com.actt.actt.controls.Dropdown;
import com.actt.actt.controls.HeaderButtonBar;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.ResultJSONModel;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

    public void setTournamentsList(String name) {
        if (name != null) {
            tournamentsComboBox.getStyleClass().remove("dropdown__placeholder-value");
        }

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
    protected void onDropdownSelect(ActionEvent ev) throws IOException {
        TournamentSettings selectedTournament = ((Dropdown)ev.getTarget()).getValue();
        if (selectedTournament != null) {
            loadTournament();
            tournamentsComboBox.getStyleClass().remove("text-field-error");
        }
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

    private void loadTournament() throws IOException {
        String tourName = tournamentsComboBox.getValue().getName();
        TournamentSettings settings = FileOperations.getTournamentSettings(tourName);
        ResultJSONModel[] resultFiles = FileOperations.getRaceResultsFromTournament(tourName);
        TournamentLoader loader = new TournamentLoader(settings);
        var results = loader.loadTournament(resultFiles);
        System.out.println("LOAD TOURNAMENT");
    }

    private void openTournamentEditor(boolean isCreateMode) throws IOException {
        EditTournament controller = (EditTournament) sceneController.showScene(SceneController.SCENES.EDIT, (Stage) ap.getScene().getWindow());
        String label = isCreateMode ? "New tournament" : "Edit tournament";

        if (!isCreateMode) {
            if (tournamentsComboBox.getValue() == null) {
                tournamentsComboBox.getStyleClass().add("text-field-error");
                return;
            }
            else {
                tournamentsComboBox.getStyleClass().remove("text-field-error");
            }

            controller.setTournament(tournamentsComboBox.getValue());
        }

        controller.setTitle(label);
    }

    private void deleteTournament() {
        if (tournamentsComboBox.getValue() == null) {
            return;
        }

        createDeleteTournamentDialog().showAndWait().ifPresent(b -> {
            var bData = b.getButtonData();
            if (bData == ButtonBar.ButtonData.YES) {
                var tournamentName = tournamentsComboBox.getValue().getName();
                var list = tournamentsComboBox.getItems();
                var itemToRemove = list.filtered(t -> t.getName().equals(tournamentName)).getFirst();
                tournamentsComboBox.setValue(null);

                list.remove(itemToRemove);
                tournamentsComboBox.setItems(list);
                try {
                    FileOperations.deleteTournament(itemToRemove.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Dialog<ButtonType> createDeleteTournamentDialog() {
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog");
        dialog.setTitle("Delete tournament");
        dialog.getDialogPane().setContent(new Label("Delete this tournament?"));
        dialog.getDialogPane().getButtonTypes().add(yesButton);
        dialog.getDialogPane().getButtonTypes().add(noButton);

        dialog.getDialogPane().lookupButton(yesButton).getStyleClass().add("button--blue-border");
        dialog.getDialogPane().lookupButton(noButton).getStyleClass().add("button--cancel");

        return dialog;
    }

    private void config() throws IOException {
        FileOperations.showConfigDialog(false);
    }
}