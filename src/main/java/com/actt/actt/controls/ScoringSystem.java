package com.actt.actt.controls;

import com.actt.actt.models.ScoringSystemModel;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ScoringSystem extends VBox {
    private Dialog<ScoringSystemModel> dialog;

    public void setDialog(Dialog<ScoringSystemModel> dialog) {
        this.dialog = dialog;
    }

    public String getName() {
        return "";
    }

    public void setData(ScoringSystemModel model) {
        System.out.println("DATA SET");
    }

    @FXML
    private void submit() {
        System.out.println("sub");
        dialog.setResult(new ScoringSystemModel());
        dialog.close();
    }
}
