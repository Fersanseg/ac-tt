package com.actt.actt.controls.cellFactories;

import com.actt.actt.models.ScoringSystemModel;
import javafx.scene.control.ListCell;

public class PointsSystemCell extends ListCell<ScoringSystemModel> {
    public PointsSystemCell() {}

    @Override
    protected void updateItem(ScoringSystemModel item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            setText(item.getName());
        }
    }

}
