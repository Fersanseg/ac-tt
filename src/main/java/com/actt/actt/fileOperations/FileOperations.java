package com.actt.actt.fileOperations;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class FileOperations {
    public static void checkAppConfig() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            Path configPath = Path.of(System.getenv("LOCALAPPDATA"), "AC Tournament Tracker");
            if (!Files.exists(configPath)) {
                try {
                    Files.createDirectory(configPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!Files.exists(configPath.resolve("config.json"))) {
                showInitialConfigDialog();
            }
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // TODO check config on linux OS
        }
        else {
            showInvalidOSError();
        }
    }

    private static void showInitialConfigDialog() {
        double dialogWidth = 700;
        ButtonType closeButton = new ButtonType("Go", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configuration");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.getDialogPane().setPrefWidth(dialogWidth);

        /*
        DirectoryChooser filePicker = new DirectoryChooser();
        File folder = filePicker.showDialog(dialog.getOwner());*/

        //noinspection SpellCheckingInspection
        Text mainText = new Text("""
                It seems like this is the first time you launch the app. Welcome!
                
                Please introduce your Assetto Corsa installation path (usually located in your Steam folder -> \
                steamapps -> common -> assettocorsa), as well as the folder where you want to save your tournaments.""");
        mainText.setWrappingWidth(dialogWidth);
        mainText.setFont(new Font(16));
        TextField acPathField = new TextField();
        TextField appPathField = new TextField();
        VBox container = new VBox(20.0, mainText, acPathField, appPathField);
        dialog.getDialogPane().setContent(container);

        dialog.showAndWait();
    }

    private static void showInvalidOSError() {
        ButtonType closeButton = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Unsupported OS");
        dialog.setContentText("Your Operating System is not currently supported!\nThe app only supports Windows at " +
                "this time. Support for other OSs will come soon!");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.showAndWait().ifPresent(_ -> System.exit(1));
    }
}
