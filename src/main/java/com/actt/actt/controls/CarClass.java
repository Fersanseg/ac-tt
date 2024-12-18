package com.actt.actt.controls;

import com.actt.actt.controls.cellFactories.CarListCell;
import com.actt.actt.events.SendDataEvent;
import com.actt.actt.models.Car;
import com.actt.actt.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CarClass extends AnchorPane implements Initializable {
    public Button addCarButton;
    public Button deleteClassButton;
    public TextField carClassName;
    public ListView<Car> carsList;

    private int index;

    public CarClass() {
        setup();
    }

    public CarClass(int index) {
        setup();
        setIndex(index);
        String DEFAULT_CLASS_NAME = "Class ";
        carClassName.setText(DEFAULT_CLASS_NAME + (index + 1));
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void addCar(Car car) {
        carsList.getItems().add(car);
    }

    public void setCarsFromSettings(Car[] cars) {
        for (Car car : cars) {
            addCar(car);
        }
    }

    private void setup() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("car-class.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            carsList.setCellFactory(_ -> new CarListCell());
            carsList.setOnMouseClicked(click -> {
                if (click.getClickCount() >= 2) {
                    Car car = carsList.getSelectionModel().getSelectedItem();
                    carsList.getItems().remove(car);
                }
            });

            carClassName.setOnKeyPressed(this::fireEvent);
        }
        catch (
                IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAddCarButtonIcon();
        setDeleteClassButtonIcon();
    }

    private void setAddCarButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/add.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(0.8, 0.8));
        svgPath.getTransforms().add(new Translate(-0, -0));

        assert addCarButton != null;
        addCarButton.setGraphic(svgPath);
        addCarButton.setOnAction(_ -> {
            Map<String, Object> d = new HashMap<>();
            d.put("className", carClassName.getText());
            fireEvent(new SendDataEvent("addCar", d));
        });
    }

    private void setDeleteClassButtonIcon() {
        String svgContent = Utils.loadSVGFromFile("/com/actt/actt/images/delete.svg");
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgContent);
        svgPath.setFill(Color.AZURE);
        svgPath.getTransforms().add(new Scale(0.8, 0.8));
        svgPath.getTransforms().add(new Translate(-10, -0));

        assert deleteClassButton != null;
        deleteClassButton.setGraphic(svgPath);
        deleteClassButton.setOnAction(_ -> {
            Map<String, Object> d = new HashMap<>();
            d.put("index", index);
            fireEvent(new SendDataEvent("deleteClass", d));
        });
    }
}
