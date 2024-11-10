package com.actt.actt.controls.cellFactories;

import com.actt.actt.models.TournamentSettings;
import javafx.scene.control.ListCell;

public class TournamentCell extends ListCell<TournamentSettings> {
    public TournamentCell() {}

    @Override
    protected void updateItem(TournamentSettings item, boolean empty) {
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
