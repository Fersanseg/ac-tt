package com.actt.actt;

import com.actt.actt.controls.CarClass;
import com.actt.actt.controls.CarListCell;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.Car;
import com.actt.actt.models.CarClassSettings;
import com.actt.actt.models.TournamentSettings;
import com.actt.actt.utils.AppData;
import com.actt.actt.utils.FileOperations;
import com.actt.actt.utils.Logger;
import com.actt.actt.utils.Utils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class EditTournament implements Initializable {
    public Label editorMode;
    public Label carPickerClassName;
    public Button backButton;
    public Button carPickerBackButton;
    public Button addClassButton;
    public Button saveButton;
    public VBox carClassesContainer;
    public AnchorPane carPickerContainer;
    public ScrollPane brandListContainer;
    public ListView<Car> carList;
    public ListView<String> brandList;
    public TextField tournamentName;

    private SceneController sceneController;
    private final EventHandler<ActionEvent> onAddClass = _ -> addClass();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneController = new SceneController();
        setupBackButtons();
        setupAddButton();

        loadBrandList();
    }

    public void setTitle(String title) {
        editorMode.setText(title);
    }

    private void loadCarList(String brand) throws IOException {
        ObservableList<Car> list = AppData.getCarListByBrand(brand);

        carList.setItems(list);
        carList.setCellFactory(_ -> new CarListCell());
        carList.setOnMouseClicked(click -> {
            if (click.getClickCount() >= 2) {
                var car = carList.getSelectionModel().getSelectedItem();
                addCarToClass(car);
            }
        });
        brandListContainer.setVisible(false);
        carPickerBackButton.setVisible(true);
    }

    private void loadBrandList() {
        ObservableList<String> list = AppData.getBrandList();
        brandList.setItems(list);
        brandList.setOnMouseClicked(click -> {
            if (click.getClickCount() >= 2) {
                var selected = brandList.getSelectionModel().getSelectedItem();
                try {
                    loadCarList(selected);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setupAddButton() {
        setAddButtonIcon();
        addClassButton.setOnAction(onAddClass);
    }

    private void setupBackButtons() {
        setBackButtonIcon();
        carPickerBackButton.setOnAction(_ -> {
            brandListContainer.setVisible(true);
            carPickerBackButton.setVisible(false);
        });
    }

    private void setBackButtonIcon() {
        assert backButton != null;
        backButton.setGraphic(getBackIcon());
        assert carPickerBackButton != null;
        carPickerBackButton.setGraphic(getBackIcon());
    }

    private SVGPath getBackIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/back.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(2.5, 2.5));
        svgPath.getTransforms().add(new Translate(-7, -7));

        return svgPath;
    }

    private void setAddButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/add.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(1, 1));
        svgPath.getTransforms().add(new Translate(-0, -0));

        assert addClassButton != null;
        addClassButton.setGraphic(svgPath);
    }

    public void goHome() throws IOException {
        sceneController.showScene(SceneController.SCENES.MAIN, (Stage) backButton.getScene().getWindow());
    }

    private void addClass() {
        var classesCount = carClassesContainer.getChildren().filtered(c -> c.getClass().getTypeName().contains("CarClass")).size();
        CarClass carClass = new CarClass(classesCount);
        carClass.addEventHandler(SendDataEvent.SEND_DATA, this::onCarClassSendData);
        carClassesContainer.getChildren().addLast(carClass);
    }

    private void onCarClassSendData(SendDataEvent ev) {
        switch (ev.getAction()) {
            case "addCar":
                onClickAddCarButton(ev);
                break;
            case "deleteClass":
                onDeleteClass(ev);
                break;
        }
    }

    private void addCarToClass(Car car) {
        String carClassName = carPickerClassName.getText();
        CarClass carClass = findCarClassByName(carClassName);
        carClass.addCar(car);
    }

    private void onDeleteClass(SendDataEvent ev) {
        if (ev.getData("index").isPresent()) {
            int index = (int)(ev.getData("index").get());
            carClassesContainer.getChildren().remove(index);
        }
    }

    private void onClickAddCarButton(SendDataEvent ev) {
        carPickerContainer.setVisible(true);
        if (ev.getData("className").isPresent()) {
            String className = ev.getData("className").get().toString();
            carPickerClassName.setText(className);
        }
    }

    private CarClass findCarClassByName(String name) {
        return (CarClass) carClassesContainer.getChildren().filtered(c -> {
            return Objects.equals(((CarClass) c).carClassName.getText(), name);
        }).getFirst();
    }

    private void validateTournament() throws InterruptedException, IOException {
        List<Task<Boolean>> checkTasks = new ArrayList<>();
        checkTasks.add(Utils.makeFunctionAsync(this::checkTournamentName));
        /*checkTasks.add(Utils.makeFunctionAsync(this::checkPointScoring));
        checkTasks.add(Utils.makeFunctionAsync(this::checkAllCarsExist));
        checkTasks.add(Utils.makeFunctionAsync(this::checkNoEmptyClasses));
        checkTasks.add(Utils.makeFunctionAsync(this::checkNoRepeatedCars));*/

        var results = Utils.waitAllTasks(checkTasks);
        boolean failed = results.stream().anyMatch(t -> {
            try {
                return !t.get();
            } catch (InterruptedException | ExecutionException e) {
                try {
                    Logger.log(e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                return false;
            }
        });

        if (!failed) {
            TournamentSettings tournamentSettings = createTournamentSettingsModel();
            FileOperations.saveTournamentSettings(tournamentSettings);
        }
    }

    private TournamentSettings createTournamentSettingsModel() {
        TournamentSettings tournamentSettings = new TournamentSettings();
        List<CarClassSettings> carClassSettings = new ArrayList<>();
        for (var carClass : carClassesContainer.getChildren()) {
            if (carClass.getClass().getTypeName().contains("CarClass")) {
                CarClassSettings settings = new CarClassSettings();
                String name = ((CarClass)carClass).carClassName.getText();
                Car[] cars = ((CarClass)carClass).carsList.getItems().toArray(new Car[0]);

                settings.setName(name);
                settings.setCars(cars);
                carClassSettings.add(settings);
            }
        }

        tournamentSettings.setName(tournamentName.getText());
        tournamentSettings.setClasses(carClassSettings.toArray(carClassSettings.toArray(new CarClassSettings[0])));
        return tournamentSettings;
    }

    private boolean checkTournamentName() {
        return !tournamentName.getText().isEmpty() && tournamentName.getText() != null;
    }


    @FXML
    private void save() throws InterruptedException, IOException {
        validateTournament();
    }
}
