package com.actt.actt.utils;

import com.actt.actt.controls.DirectoryPicker;
import com.actt.actt.events.DirectoryChosenEvent;
import com.actt.actt.models.AppConfig;
import com.actt.actt.models.ResultJSONModel;
import com.actt.actt.models.ScoringSystemModel;
import com.actt.actt.models.TournamentSettings;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
    private static final String CONFIG_PATH = String.valueOf(Path.of(System.getenv("LOCALAPPDATA"), "AC Tournament Manager"));
    private static final String POINTS_FOLDER = "points";
    private static AppConfig appConfig;

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

            Path log = Path.of("log");
            if (!Files.exists(configPath.resolve(log))) {
                try {
                    Files.createDirectory(configPath.resolve(log));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!Files.exists(configFilePath)) {
                showConfigDialog(true);
            }
            else {
                loadConfig();
            }
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // TODO check config on linux OS
        }
        else {
            showInvalidOSError();
        }
    }

    public static void showConfigDialog(boolean killOnExit) throws IOException {
        Dialog<ButtonType> dialog = showInitialConfigDialog(killOnExit);
        saveConfig(dialog, Path.of(CONFIG_PATH).resolve(Path.of(CONFIG_FILENAME)));
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

    public static AppConfig getAppConfig() {
        return appConfig;
    }

    public static TournamentSettings getTournamentSettings(String name) throws IOException {
        String path = appConfig.getAppPath() + "\\" + name + "\\config.json";
        if (!Files.exists(Path.of(path))) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        File settings = new File(path);
        return mapper.readValue(settings, TournamentSettings.class);
    }

    public static void saveTournamentSettings(TournamentSettings settings) throws IOException {
        String appPath = appConfig.getAppPath();
        Path pathAppPath = Path.of(appPath);

        if (!Files.exists(pathAppPath)) {
            Files.createDirectory(pathAppPath);
        }

        Path tournamentPath = Path.of(appPath + "\\" + settings.getName());
        Path absoluteFilePath = tournamentPath.resolve(Path.of(CONFIG_FILENAME));
        if (!Files.exists(tournamentPath)) {
            Files.createDirectory(tournamentPath);
        }
        Files.deleteIfExists(absoluteFilePath);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(String.valueOf(absoluteFilePath)), settings);
    }

    public static File[] getPointSystems() throws IOException {
        Path pointsPath = Path.of(CONFIG_PATH).resolve(POINTS_FOLDER);
        if (!Files.exists(pointsPath)) {
            Files.createDirectory(pointsPath);
        }

        File pointsFolder = new File(String.valueOf(pointsPath));
        return pointsFolder.listFiles();
    }

    public static File[] getTournaments() {
        String tourneysPath = appConfig.getAppPath();
        File tourneysFolder = new File(tourneysPath);

        return tourneysFolder.listFiles();
    }

    public static ResultJSONModel[] getRaceResultsFromTournament(String name) {
        String tournamentPath = appConfig.getAppPath() + "\\" + name;
        File tourneyFolder = new File(tournamentPath);
        File[] allFiles = tourneyFolder.listFiles();
        if (allFiles == null || allFiles.length == 0) {
            return new ResultJSONModel[0];
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return Arrays.stream(allFiles).map(f -> {
            try {
                var ob = mapper.readValue(f, ResultJSONModel.class);
                if (ob.getTrack() == null && ob.getPlayers() == null && ob.getSessions() == null) {
                    return null;
                }

                ob.setTrackId(f.getName().replace(".json", ""));

                return ob;
            } catch (Exception _) {
                return null;
            }
        })
            .filter(Objects::nonNull)
            .toArray(ResultJSONModel[]::new);
    }

    public static void savePointsSystem(ScoringSystemModel model) throws IOException {
        Path pointsPath = Path.of(CONFIG_PATH).resolve(POINTS_FOLDER);
        if (!Files.exists(pointsPath)) {
            Files.createDirectory(pointsPath);
        }

        ObjectMapper mapper = new ObjectMapper();
        String fileName = model.getId() + ".json";
        File file = new File(pointsPath + "\\" + fileName);

        mapper.writeValue(file, model);
    }

    public static boolean checkPointsFileExists(String fileName) {
        Path pointsPath = Path.of(CONFIG_PATH).resolve(POINTS_FOLDER);
        String fullFileName = "\\" + fileName + ".json";
        return Files.exists(Path.of(pointsPath + fullFileName));
    }

    public static void deleteTournament(String name) throws IOException {
        Path path = Path.of(appConfig.getAppPath() + "\\" + name);
        if (Files.exists(path)) {
            File[] files = new File(String.valueOf(path)).listFiles();
            if (files == null) {
                return;
            }

            for (File file : files) {
                Files.delete(file.toPath());
            }

            Files.delete(path);
        }
    }

    private static void setAppConfig(AppConfig config) {
        appConfig = config;
    }

    private static void loadConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        var json = mapper.readValue(new File(CONFIG_PATH + "\\" + CONFIG_FILENAME), AppConfig.class);

        setAppConfig(json);
    }


    private static String getLogFileName() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String year = String.valueOf(ts.toLocalDateTime().getYear());
        String month = String.valueOf(ts.toLocalDateTime().getMonthValue());
        String dayOfMonth = String.valueOf(ts.toLocalDateTime().getDayOfMonth());
        return "Log_" + year + "-" + month + "-" + dayOfMonth + ".txt";
    }

    private static Dialog<ButtonType> showInitialConfigDialog(boolean killOnExit) {
        Dialog<ButtonType> dialog = createInitialConfigDialog();
        Optional<ButtonType> res = dialog.showAndWait();
        if (killOnExit && (res.isEmpty() || res.get().getButtonData() != ButtonBar.ButtonData.OK_DONE)) {
            System.exit(1);
        }

        return dialog;
    }

    private static Dialog<ButtonType> createInitialConfigDialog() {
        double dialogWidth = 700;
        String defaultAppPath = FileSystemView.getFileSystemView().getDefaultDirectory().toString().replace("/", "\\") + "\\AC Tournament Manager";
        String defaultAcPath = (System.getenv("ProgramFiles(x86)") + "/Steam/steamapps/common/assettocorsa").replace("/", "\\");

        ButtonType closeButton = new ButtonType("Go", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(FileOperations.class.getResource("/styles.css")).toExternalForm());
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

    private static void saveConfig(Dialog<ButtonType> dialog, Path saveToPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<DirectoryPicker> dirPickers = GetNodesByType(dialog.getDialogPane(), DirectoryPicker.class);
        String acDirPath = Objects.requireNonNull(getDirPickerByText(dirPickers, AC_BUTTON_TEXT)).getPath();
        String appDirPath = Objects.requireNonNull(getDirPickerByText(dirPickers, APP_BUTTON_TEXT)).getPath();
        Path pathAppDirPath = Path.of(appDirPath);

        if (appConfig == null)
            setAppConfig(new AppConfig());

        appConfig.setAcPath(acDirPath);
        appConfig.setAppPath(appDirPath);
        mapper.writeValue(new File(String.valueOf(saveToPath)), appConfig);

        if (!Files.exists(pathAppDirPath)) {
            Files.createDirectory(pathAppDirPath);
        }
    }

    private static DirectoryPicker getDirPickerByText(List<DirectoryPicker> dirPickers, String buttonText) {
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

    private static final EventHandler<DirectoryChosenEvent> onACPathPicked = event -> {
        File dir = event.getDirectory();
        File acExe = new File(dir, "AssettoCorsa.exe");
        if (!acExe.exists()) {
            System.out.println("Invalid Assetto Corsa installation directory!");
        }
    };
}
