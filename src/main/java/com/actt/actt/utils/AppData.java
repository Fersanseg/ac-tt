package com.actt.actt.utils;

import com.actt.actt.models.Car;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AppData {
    private static ObservableList<Car> cars;

    public static ObservableList<Car> getCarList() {
        if (cars == null) {
            loadCars();
        }

        return cars;
    }

    public static void loadCars() {
        String acPath = FileOperations.getAppConfig().getAcPath();

        File carDir = new File(acPath + "\\content" + "\\cars");
        var carFolders = carDir.listFiles();
        if (carFolders != null) {
            for (File carFolder : carFolders) {
                File carConfig = getCarConfigFile(carFolder);
                // TODO read the json into a Car instance and add it to carList
            }
        }
    }

    private static File getCarConfigFile(File folder) {
        File[] innerFolders = folder.listFiles();
        if (innerFolders == null)
            return null;

        Optional<File> carConfigFile = Arrays.stream(Objects.requireNonNull(folder.listFiles((_, name) -> name.equals("ui_car.json")))).findFirst();
        if (carConfigFile.isPresent()) {
            return carConfigFile.get();
        }

        for (File innerFolder : innerFolders) {
            File file = getCarConfigFile(innerFolder);
            if (file != null) {
                return file;
            }
        }

        return null;
    }
}
