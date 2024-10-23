package com.actt.actt;

import com.actt.actt.controls.CarClass;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    public ListView<String> list;

    private SceneController sceneController;
    private final EventHandler<ActionEvent> onAddClass = _ -> addClass();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneController = new SceneController();
        setBackButtonIcon();
        setupAddButton();

        for (int i = 0; i < 400; i++) {
            list.getItems().add("CAR " + (i+1));
        }
    }

    public void setTitle(String title) {
        editorMode.setText(title);
    }

    private void setupAddButton() {
        setAddButtonIcon();
        addClassButton.setOnAction(onAddClass);
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

    public void goHome(ActionEvent ev) throws IOException {
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
        goHome(null);
    }
}
