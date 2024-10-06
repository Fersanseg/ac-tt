package com.actt.actt.utils;

import com.actt.actt.controls.DirectoryPicker;
import com.actt.actt.events.DirectoryChosenEvent;
import com.actt.actt.models.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;

import static com.actt.actt.utils.ControlOperations.GetNodesByType;

public class FileOperations {
    private static final String AC_BUTTON_TEXT = "AC Path";
    private static final String APP_BUTTON_TEXT = "App Path";
    private static final String CONFIG_FILENAME = "config.json";
    private static final String CONFIG_PATH = String.valueOf(Path.of(System.getenv("LOCALAPPDATA"), "AC Tournament Tracker"));

    public static void checkAppConfig() throws IOException {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            Path configPath = Path.of(CONFIG_PATH);
            Path configFilePath = configPath.resolve(CONFIG_FILENAME);

            if (!Files.exists(configPath)) {
                try {
                    Files.createDirectory(configPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!Files.exists(configPath.resolve(Path.of("log")))) {
                try {
                    Files.createDirectory(configPath.resolve(Path.of("log")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!Files.exists(configFilePath)) {
                Dialog<ButtonType> dialog = showInitialConfigDialog();
                SaveConfig(dialog, configFilePath);
            }
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // TODO check config on linux OS
        }
        else {
            showInvalidOSError();
        }
    }

    public static FileInputStream getLogFile() throws IOException {
        String logFileName = getLogFileName();

        String strFilePath = CONFIG_PATH + "\\log\\" + logFileName;
        Path filePath = Path.of(strFilePath);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

       return new FileInputStream(strFilePath);
    }

    public static void saveLogFile(byte[] bytes) throws IOException {
        String logFileName = getLogFileName();

        String strFilePath = CONFIG_PATH + "\\log\\" + logFileName;

        File file = new File(strFilePath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    private static String getLogFileName() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String year = String.valueOf(ts.toLocalDateTime().getYear());
        String month = String.valueOf(ts.toLocalDateTime().getMonthValue());
        String dayOfMonth = String.valueOf(ts.toLocalDateTime().getDayOfMonth());
        return "Log_" + year + "-" + month + "-" + dayOfMonth + ".txt";
    }

    private static Dialog<ButtonType> showInitialConfigDialog() {
        Dialog<ButtonType> dialog = CreateInitialConfigDialog();
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
            System.exit(1);
        }

        return dialog;
    }

    private static Dialog<ButtonType> CreateInitialConfigDialog() {
        double dialogWidth = 700;
        String defaultAppPath = FileSystemView.getFileSystemView().getDefaultDirectory().toString().replace("/", "\\");
        String defaultAcPath = (System.getenv("ProgramFiles(x86)") + "/Steam/steamappscommon/assettocorsa").replace("/", "\\");

        ButtonType closeButton = new ButtonType("Go", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(FileOperations.class.getResource("/styles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog");
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
        mainText.setFill(Color.GHOSTWHITE);

        DirectoryPicker acPathPicker = new DirectoryPicker(AC_BUTTON_TEXT, defaultAcPath);
        acPathPicker.addEventHandler(DirectoryChosenEvent.DIR_CHOSEN, onACPathPicked);
        DirectoryPicker appPathPicker = new DirectoryPicker(APP_BUTTON_TEXT, defaultAppPath);
        VBox fpContainer = new VBox(20.0, acPathPicker, appPathPicker);
        fpContainer.setPadding(new Insets(0.0, 0.0, 0.0, 20.0));
        VBox container = new VBox(20.0, mainText, fpContainer);
        dialog.getDialogPane().setContent(container);
        return dialog;
    }

    private static void SaveConfig(Dialog<ButtonType> dialog, Path saveToPath) throws IOException {
        AppConfig configJson = new AppConfig();
        ObjectMapper mapper = new ObjectMapper();
        List<DirectoryPicker> dirPickers = GetNodesByType(dialog.getDialogPane(), DirectoryPicker.class);
        String acDirPath = Objects.requireNonNull(GetDirPickerByText(dirPickers, AC_BUTTON_TEXT)).getPath();
        String appDirPath = Objects.requireNonNull(GetDirPickerByText(dirPickers, APP_BUTTON_TEXT)).getPath();

        configJson.setAcPath(acDirPath);
        configJson.setAppPath(appDirPath);
        mapper.writeValue(new File(String.valueOf(saveToPath)), configJson);
    }

    private static DirectoryPicker GetDirPickerByText(List<DirectoryPicker> dirPickers, String buttonText) {
        return dirPickers != null
                ? dirPickers.stream()
                    .filter(p -> Objects.equals(p.getButton().getText(), buttonText))
                    .toList()
                    .getFirst()
                : null;
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
            // TODO validate and show error msg if not AC installation directory
            System.out.println("Invalid Assetto Corsa installation directory!");
        }
    };
}
