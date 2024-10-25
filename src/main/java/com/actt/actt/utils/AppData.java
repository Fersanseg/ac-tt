package com.actt.actt.utils;

import com.actt.actt.models.Car;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AppData {
    private static ObservableList<Car> cars;

    public static ObservableList<Car> getCarList() throws IOException {
        if (cars == null) {
            loadCars();
        }

        return cars;
    }

    public static void loadCars() throws IOException {
        if (cars == null)
            cars = FXCollections.observableArrayList();

        String acPath = FileOperations.getAppConfig().getAcPath();

        File carDir = new File(acPath + "\\content" + "\\cars");
        var carFolders = carDir.listFiles();
        if (carFolders != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (File carFolder : carFolders) {
                try {
                    File carConfig = getCarConfigFile(carFolder);
                    String jsonContent = new String(Files.readAllBytes(carConfig.toPath()));
                    jsonContent = jsonContent.replace("\n", "");
                    jsonContent = jsonContent.replace("\r", "");
                    jsonContent = jsonContent.replace("\t", "");

                    Car car = mapper.readValue(jsonContent.getBytes(StandardCharsets.UTF_8), Car.class);
                    car.setFolderName(carFolder.getName());
                    cars.add(car);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
