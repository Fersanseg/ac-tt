package com.actt.actt;

import com.actt.actt.controls.CarClass;
import com.actt.actt.controls.CarListCell;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.Car;
import com.actt.actt.utils.AppData;
import com.actt.actt.utils.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
                onAddCar(ev);
                break;
            case "deleteClass":
                onDeleteClass(ev);
                break;
        }
    }

    private void onDeleteClass(SendDataEvent ev) {
        if (ev.getData("index").isPresent()) {
            int index = (int)(ev.getData("index").get());
            carClassesContainer.getChildren().remove(index);
        }
    }

    private void onAddCar(SendDataEvent ev) {
        carPickerContainer.setVisible(true);
        if (ev.getData("className").isPresent()) {
            String className = ev.getData("className").get().toString();
            carPickerClassName.setText(className);
        }
    }

    @FXML
    private void save() throws IOException {
        goHome();
    }
}
