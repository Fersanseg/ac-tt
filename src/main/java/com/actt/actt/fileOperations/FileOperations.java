package com.actt.actt.fileOperations;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

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
        ButtonType closeButton = new ButtonType("Go", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configuration");
        dialog.setContentText("It seems like this is the first time you launch the app. Welcome!\n\nPlease introduce " +
                "your Assetto Corsa installation path (usually located in your Steam folder -> " +
                "steamapps -> common -> assettocorsa), as well as the folder where you want to save your tournaments.");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.getDialogPane().setPrefWidth(800);
        dialog.showAndWait();
    }

    private static void showInvalidOSError() {
        ButtonType closeButton = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Unsupported OS");
        dialog.setContentText("Your Operating System is not currently supported!\nThe app only supports Windows at " +
                "this time. Support for other OSs will come soon!");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.showAndWait().ifPresent(_ -> {
                System.exit(1);
        });
    }
}
