package com.actt.actt.controls;

import com.actt.actt.models.ScoringSystemModel;
import com.actt.actt.utils.FileOperations;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoringSystem extends VBox {
    private final int MAX_ITEMS_PER_COLUMN = 25;
    private ScoringSystemModel model;
    private Dialog<ScoringSystemModel> dialog;

    public GridPane grid;
    public TextField pointsSystemName;
    public Button addButton;

    private int counter = 0;
    private final List<TextField> tfStack = new ArrayList<>();

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
        counter++;

        TextField tf = new TextField("0");
        tf.textProperty().addListener((_, _, t1) -> {
            if (!t1.matches("\\d*")) {
                tf.setText(t1.replaceAll("\\D", ""));
            }
        });
        tf.setPadding(new Insets(0.5, 0.5, 0.5, 4.5));
        tf.setPrefWidth(50);
        tfStack.add(tf);

        Label posLabel = new Label(String.valueOf(counter));
        HBox hbox =  new HBox();
        hbox.setSpacing(15);
        hbox.getChildren().add(posLabel);
        hbox.getChildren().add(tf);
        hbox.setAlignment(Pos.BASELINE_CENTER);


        int col = counter <= MAX_ITEMS_PER_COLUMN ? 0 : 1;
        int row = counter > MAX_ITEMS_PER_COLUMN ? (counter - 1 - MAX_ITEMS_PER_COLUMN) : (counter - 1);

        grid.add(hbox, col, row);
    }

    @FXML
    private void removePosition() {
        if (counter <= 0) {
            return;
        }

        counter--;

        int col = counter >= MAX_ITEMS_PER_COLUMN ? 1 : 0;
        int row = counter >= MAX_ITEMS_PER_COLUMN ? (counter - MAX_ITEMS_PER_COLUMN) : (counter);

        if (grid.getChildren().removeIf(i -> GridPane.getRowIndex(i) == row && GridPane.getColumnIndex(i) == col)) {
            tfStack.removeLast();
        }
    }

    @FXML
    private void submit() {
        int[] points = new int[tfStack.size()];
        for (int i = tfStack.size() - 1; i >= 0 ; i--) {
            points[i] = Integer.parseInt(tfStack.get(i).getText());
        }

        boolean valid = true;
        if (pointsSystemName.getText().isEmpty()) {
            pointsSystemName.getStyleClass().add("text-field-error");
            valid = false;
        }
        else pointsSystemName.getStyleClass().remove("text-field-error");

        if (points.length == 0) {
            addButton.getStyleClass().add("text-field-error");
            valid = false;
        }
        else addButton.getStyleClass().remove("text-field-error");

        if (!valid) {
            return;
        }

        model.setName(pointsSystemName.getText());
        model.setPoints(points);

        if (!checkExistingFile(model.getName())) {
            return;
        }

        dialog.setResult(model);
        dialog.close();
    }

    private boolean checkExistingFile(String fileName) {
        if (!FileOperations.checkPointsFileExists(fileName)) {
            return true;
        }

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        Dialog<ButtonType> overwriteDialog = new Dialog<>();
        overwriteDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        overwriteDialog.getDialogPane().getStyleClass().add("dialog");
        overwriteDialog.setTitle("Overwrite?");
        overwriteDialog.getDialogPane().setContent(new Label("A scoring system with the same name already exists. " +
                "Do you want to overwrite it?"));
        overwriteDialog.getDialogPane().getButtonTypes().add(yesButton);
        overwriteDialog.getDialogPane().getButtonTypes().add(noButton);

        overwriteDialog.getDialogPane().lookupButton(yesButton).getStyleClass().add("button--blue-border");
        overwriteDialog.getDialogPane().lookupButton(noButton).getStyleClass().add("button--cancel");

        var r = overwriteDialog.showAndWait();
        return r.isPresent() && r.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
