package com.actt.actt.utils;

import com.actt.actt.controls.DirectoryPicker;
import com.actt.actt.events.DirectoryChosenEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

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
        String defaultAppPath = FileSystemView.getFileSystemView().getDefaultDirectory().toString().replace("/", "\\");
        String defaultAcPath = (System.getenv("ProgramFiles(x86)") + "/Steam/steamappscommon/assettocorsa").replace("/", "\\");

        ButtonType closeButton = new ButtonType("Go", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configuration");
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        dialog.getDialogPane().setPrefWidth(dialogWidth);
        //noinspection SpellCheckingInspection
        Text mainText = new Text("""
                It seems like this is the first time you launch the app. Welcome!
                
                Please introduce your Assetto Corsa installation path (usually located in your Steam folder -> \
                steamapps -> common -> assettocorsa), as well as the folder where you want to save your tournaments.""");
        mainText.setWrappingWidth(dialogWidth);
        mainText.setFont(new Font(16));

        DirectoryPicker acPathPicker = new DirectoryPicker("AC Path", defaultAcPath);
        acPathPicker.addEventHandler(DirectoryChosenEvent.DIR_CHOSEN, onACPathPicked);
        DirectoryPicker appPathPicker = new DirectoryPicker("App Path", defaultAppPath);
        VBox fpContainer = new VBox(20.0, acPathPicker, appPathPicker);
        fpContainer.setPadding(new Insets(0.0, 0.0, 0.0, 20.0));
        VBox container = new VBox(20.0, mainText, fpContainer);
        dialog.getDialogPane().setContent(container);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) {
            System.exit(1);
        }
    }

    private static void showInvalidOSError() {
        ButtonType closeButton = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Unsupported OS");
        dialog.setContentText("Your Operating System is not currently supported!\nThe app only supports Windows at " +
                "this time. Support for other OSs will come soon!");
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
        System.exit(1);
    }

    // When the selected AC installation path changes, validates it as a correct AC installation
    private static final EventHandler<DirectoryChosenEvent> onACPathPicked = event -> {
        File dir = event.getDirectory();
        File acExe = new File(dir, "AssettoCorsa.exe");
        if (!acExe.exists()) {
            System.out.println("Invalid Assetto Corsa installation directory!");
        }
    };
}