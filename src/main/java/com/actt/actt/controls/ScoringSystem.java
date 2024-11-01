package com.actt.actt.controls;

import com.actt.actt.models.ScoringSystemModel;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ScoringSystem extends VBox {
    private final int MAX_ITEMS_PER_COLUMN = 25;
    private ScoringSystemModel model;
    private Dialog<ScoringSystemModel> dialog;

    public GridPane grid;

    private int counter = 0;

    public void setDialog(Dialog<ScoringSystemModel> dialog) {
        this.dialog = dialog;
    }

    public String getName() {
        return model.getName();
    }

    public void setData(ScoringSystemModel model) {
        this.model = model;
    }

    @FXML
    private void addPosition() {
        if (counter >= (MAX_ITEMS_PER_COLUMN * 2)) {
            return;
        }

        Label label = new Label("text " + counter);
        counter++;

        int col = counter <= MAX_ITEMS_PER_COLUMN ? 0 : 1;
        int row = counter > MAX_ITEMS_PER_COLUMN ? (counter - 1 - MAX_ITEMS_PER_COLUMN) : (counter - 1);

        grid.add(label, col, row);
    }

    @FXML
    private void removePosition() {
        if (counter <= 0) {
            return;
        }

        counter--;

        int col = counter >= MAX_ITEMS_PER_COLUMN ? 1 : 0;
        int row = counter >= MAX_ITEMS_PER_COLUMN ? (counter - MAX_ITEMS_PER_COLUMN) : (counter);

        grid.getChildren().removeIf(i -> GridPane.getRowIndex(i) == row && GridPane.getColumnIndex(i) == col);
    }

    @FXML
    private void submit() {
        dialog.setResult(new ScoringSystemModel());
        dialog.close();
    }
}
