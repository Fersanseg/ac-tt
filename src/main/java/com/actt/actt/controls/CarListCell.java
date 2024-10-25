package com.actt.actt.controls;

import com.actt.actt.models.Car;
import javafx.scene.control.ListCell;

public class CarListCell extends ListCell<Car> {
    public CarListCell() {}

    @Override
    protected void updateItem(Car car, boolean empty) {
        super.updateItem(car, empty);
        if (empty || car == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            setText(car.getName());
        }
    }
}
