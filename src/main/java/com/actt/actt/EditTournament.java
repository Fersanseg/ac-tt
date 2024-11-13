package com.actt.actt;

import com.actt.actt.controls.CarClass;
import com.actt.actt.controls.cellFactories.CarListCell;
import com.actt.actt.controls.cellFactories.PointsSystemCell;
import com.actt.actt.controls.ScoringSystem;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.Car;
import com.actt.actt.models.CarClassSettings;
import com.actt.actt.models.ScoringSystemModel;
import com.actt.actt.models.TournamentSettings;
import com.actt.actt.utils.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class EditTournament implements Initializable {
    public Label editorMode;
    public Label carPickerClassName;
    public Button backButton;
    public Button carPickerBackButton;
    public Button addClassButton;
    public Button saveButton;
    public Button addScoringSystemButton;
    public Button editScoringSystemButton;
    public VBox carClassesContainer;
    public AnchorPane carPickerContainer;
    public ScrollPane brandListContainer;
    public ListView<Car> carList;
    public ListView<String> brandList;
    public TextField tournamentName;
    public TextField carSearchField;
    public ComboBox<ScoringSystemModel> pointsSystemComboBox;

    private SceneController sceneController;
    private Debouncer<ObservableList<Car>> searchDebouncer;
    private ObservableList<ScoringSystemModel> pointsSystems;

    private final EventHandler<ActionEvent> onAddClass = _ -> addClass();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneController = new SceneController();
        carSearchField.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((_, _, newWindow) -> {
                    if (newWindow != null && searchDebouncer != null) {
                        newWindow.addEventHandler(WindowEvent.WINDOW_HIDDEN, _ -> searchDebouncer.shutdown());
                        newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, _ -> searchDebouncer.shutdown());
                    }
                });
            }
        });
        setupBackButtons();
        setupAddButton();
        try {
            setupScoringSystemStuff();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadBrandList();
    }

    public void setTitle(String title) {
        editorMode.setText(title);
    }

    public void setTournament(TournamentSettings tournament) {
        String tName = tournament.getName();
        ScoringSystemModel pointsSystem = tournament.getPointsSystem();
        CarClassSettings[] classes = tournament.getClasses();

        if (tName != null) {
            tournamentName.setText(tName);
        }
        if (pointsSystem != null) {
            pointsSystemComboBox.setValue(pointsSystem);
        }
        if (classes != null) {
            for (CarClassSettings item : classes) {
                var cl = addClass();
                cl.setCarsFromSettings(item.getCars());
            }
        }
    }

    private void loadCarList(String brand) throws IOException {
        ObservableList<Car> list = AppData.getCarListByBrand(brand);
        loadCarList(list);
    }

    private void loadCarList(ObservableList<Car> list) {
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

    private void setupScoringSystemStuff() throws IOException {
        pointsSystemComboBox.getStyleClass().add("placeholder-value");
        pointsSystemComboBox.setOnAction(_ -> {
            if (pointsSystemComboBox.getValue() != null) {
                pointsSystemComboBox.getStyleClass().remove("placeholder-value");
                editScoringSystemButton.setVisible(true);
            }
        });
        pointsSystemComboBox.setCellFactory(_ -> new PointsSystemCell());
        pointsSystemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ScoringSystemModel model) {
                return model == null ? "" : model.getName();
            }

            @Override
            public ScoringSystemModel fromString(String s) {
                var model = new ScoringSystemModel();
                model.setName(s);
                return model;
            }
        });

        refreshPointsComboBox();
    }

    private void refreshPointsComboBox() throws IOException {
        pointsSystemComboBox.getItems().clear();
        if (pointsSystems == null) {
            pointsSystems = FXCollections.observableArrayList();
        }
        else {
            pointsSystems.clear();
        }

        var fs = FileOperations.getPointSystems();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (File f : fs) {
            try {
                ScoringSystemModel model = mapper.readValue(f, ScoringSystemModel.class);
                if (model.isValid()) {
                    pointsSystems.add(model);
                }
            }
            catch (Exception _) {

            }
        }

        pointsSystemComboBox.setItems(pointsSystems);
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

    @FXML
    private void showAddScoringSystem() throws IOException {
        ScoringSystemModel model = new ScoringSystemModel(Utils.getRandomID());
        model.setName(tournamentName.getText());
        Dialog<ScoringSystemModel> dialog = createScoringSystemDialog(model);
        handleScoringSystemDialogResult(dialog.showAndWait());
    }

    @FXML
    private void showEditScoringSystem() throws IOException {
        ScoringSystemModel selectedSystem = pointsSystemComboBox.getValue();
        Dialog<ScoringSystemModel> dialog = createScoringSystemDialog(selectedSystem);
        handleScoringSystemDialogResult(dialog.showAndWait());
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
        sceneController.showHomeScene((Stage) backButton.getScene().getWindow(), tournamentName.getText());
    }

    private CarClass addClass() {
        carPickerContainer.setVisible(false);

        var classesCount = carClassesContainer.getChildren().filtered(c -> c.getClass().getTypeName().contains("CarClass")).size();
        CarClass carClass = new CarClass(classesCount);
        carClass.addEventHandler(SendDataEvent.SEND_DATA, this::onCarClassSendData);
        carClass.addEventHandler(KeyEvent.ANY, _ -> carPickerContainer.setVisible(false));
        carClassesContainer.getChildren().addLast(carClass);

        return carClass;
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
        if (!carClass.carsList.getItems().stream().map(Car::getName).toList().contains(car.getName())) {
            carClass.addCar(car);
        }
    }

    private void onDeleteClass(SendDataEvent ev) {
        if (ev.getData("index").isPresent()) {
            int index = (int)(ev.getData("index").get());
            carClassesContainer.getChildren().remove(index);

            for (int i = 0; i < carClassesContainer.getChildren().size(); i++) {
                CarClass carClass = (CarClass) carClassesContainer.getChildren().get(i);
                carClass.setIndex(i);
            }
        }
    }

    private void onClickAddCarButton(SendDataEvent ev) {
        showCarPicker();

        if (ev.getData("className").isPresent()) {
            String className = ev.getData("className").get().toString();
            carPickerClassName.setText(className);
        }
    }

    private void showCarPicker() {
        carPickerContainer.setVisible(true);
        brandListContainer.setVisible(true);
    }

    private CarClass findCarClassByName(String name) {
        return (CarClass) carClassesContainer.getChildren().filtered(c -> {
            return Objects.equals(((CarClass) c).carClassName.getText(), name);
        }).getFirst();
    }

    private void validateTournament() throws InterruptedException, IOException {
        List<Task<Boolean>> checkTasks = new ArrayList<>();
        checkTasks.add(Utils.makeFunctionAsync(this::checkTournamentName));
        checkTasks.add(Utils.makeFunctionAsync(this::checkNoEmptyClasses));
        checkTasks.add(Utils.makeFunctionAsync(this::checkPointScoring));

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

            goHome();
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
        tournamentSettings.setPointsSystem(pointsSystemComboBox.getValue());
        return tournamentSettings;
    }

    private boolean checkTournamentName() {
        boolean valid = !tournamentName.getText().isEmpty() && tournamentName.getText() != null;
        if (!valid) {
            tournamentName.getStyleClass().add("text-field-error");
        }
        else tournamentName.getStyleClass().remove("text-field-error");

        return valid;
    }

    private boolean checkNoEmptyClasses() {
        var carClasses = carClassesContainer.getChildren();
        var emptyClasses = carClasses
                .stream()
                .filter(c -> ((CarClass) c).carsList.getItems().isEmpty())
                .map(c -> (CarClass)c)
                .toList();
        boolean valid = emptyClasses.isEmpty();
        if (!valid) {
            for (CarClass carClass : emptyClasses) {
                carClass.carClassName.getStyleClass().remove("text-color-normal");
                carClass.carClassName.getStyleClass().add("text-color-error");
                carClass.carClassName.getStyleClass().add("text-field-error");
            }
        }
        else {
            for (var carClass : carClasses) {
                ((CarClass)carClass).carClassName.getStyleClass().remove("text-color-error");
                ((CarClass)carClass).carClassName.getStyleClass().remove("text-field-error");
                ((CarClass)carClass).carClassName.getStyleClass().add("text-color-normal");
            }
        }

        return valid;
    }

    private boolean checkPointScoring() {
        boolean valid = pointsSystemComboBox.getValue() != null;
        if (!valid) {
            pointsSystemComboBox.getStyleClass().add("text-field-error");
        }
        else {
            pointsSystemComboBox.getStyleClass().remove("text-field-error");
        }
        return valid;
    }

    private Dialog<ScoringSystemModel> createScoringSystemDialog(ScoringSystemModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("controls/scoring-system.fxml"));
        VBox formPane = loader.load();
        ScoringSystem scoringSystem = loader.getController();
        scoringSystem.setData(model);

        String dialogMode = Objects.equals(scoringSystem.getName(), "") ? "New" : "Edit";
        Dialog<ScoringSystemModel> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog");
        dialog.setTitle(dialogMode + " Scoring System");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button closebutton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closebutton.setOnAction(_ -> dialog.setResult(null));
        closebutton.setPrefSize(1, 1);
        closebutton.setVisible(false);

        dialog.getDialogPane().setContent(formPane);
        scoringSystem.setDialog(dialog);

        return dialog;
    }

    private <T> void handleScoringSystemDialogResult(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<T> result) throws IOException {
        //noinspection OptionalGetWithoutIsPresent
        if (result.get() instanceof ScoringSystemModel model) {
            pointsSystemComboBox.setValue(null);
            FileOperations.savePointsSystem(model);
            refreshPointsComboBox();
            pointsSystemComboBox.setValue(model);
        }
    }


    @FXML
    private void save() throws InterruptedException, IOException {
        validateTournament();
    }

    @FXML
    private void search() {
        if (searchDebouncer == null) {
            searchDebouncer = new Debouncer<>();
        }

        if (carSearchField.getText().isEmpty()) {
            searchDebouncer.cancel();
            carPickerBackButton.fire();
            return;
        }

        searchDebouncer.runDelayed(
            () -> AppData.filterCarsByName(carSearchField.getText()),
            500,
            this::handleFilteredList);
    }

    private void handleFilteredList(ObservableList<Car> cars) {
        loadCarList(cars);
    }
}
