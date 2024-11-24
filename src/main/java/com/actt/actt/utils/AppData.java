package com.actt.actt.utils;

import com.actt.actt.models.Car;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AppData {
    private static ObservableList<Car> cars;
    private static ObservableList<String> brands;
    private static Map<String, ObservableList<Car>> carsByBrand;
    private static File[] tournamentFolders;
    private static ConcurrentHashMap<String, String> trackNames; // K: foldername-layoutname. V: layout UI name

    public static ObservableList<Car> getCarList() throws IOException {
        if (cars == null) {
            loadCars();
        }

        return cars;
    }

    public static ObservableList<String> getBrandList() {
        return brands;
    }

    public static ObservableList<Car> getCarListByBrand(String brand) throws IOException {
        if (carsByBrand == null) {
            createCarBrandsMap();
        }

        if (carsByBrand.containsKey(brand)) {
            return carsByBrand.get(brand);
        }

        return null;
    }

    public static void loadCars() throws IOException {
        if (cars == null) {
            cars = FXCollections.observableArrayList();
        }
        if (brands == null) {
            brands = FXCollections.observableArrayList();
        }

        String acPath = FileOperations.getAppConfig().getAcPath();

        File carDir = new File(acPath + "\\content" + "\\cars");
        var carFolders = carDir.listFiles();
        if (carFolders != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ConcurrentLinkedDeque<Car> threadSafeCarList = new ConcurrentLinkedDeque<>();
            Arrays.stream(carFolders).parallel().forEach(carFolder -> {
                try {
                    File carConfig = getCarConfigFile(carFolder);
                    assert carConfig != null;
                    String jsonContent = new String(Files.readAllBytes(carConfig.toPath()));
                    jsonContent = jsonContent.replace("\n", "");
                    jsonContent = jsonContent.replace("\r", "");
                    jsonContent = jsonContent.replace("\t", "");

                    Car car = mapper.readValue(jsonContent.getBytes(StandardCharsets.UTF_8), Car.class);
                    car.setFolderName(carFolder.getName());
                    threadSafeCarList.add(car);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            List<Car> sorted = threadSafeCarList.stream().sorted(Comparator.comparing(Car::getName)).toList();
            Platform.runLater(() -> {
                List<String> auxBrandsList = new ArrayList<>();
                for (Car car : sorted) {
                    cars.add(car);
                    String brandText = car.getBrand();
                    if (!auxBrandsList.contains(brandText)) {
                        auxBrandsList.add(brandText);
                    }
                }

                brands.addAll(auxBrandsList.stream().sorted().toList());
            });
        }
    }

    public static ObservableList<Car> filterCarsByName(String searchTerm) {
        var filtered = cars.filtered(c -> c.getName().toLowerCase(Locale.ROOT).contains(searchTerm));

        return FXCollections.observableArrayList(filtered);
    }

    public static File getCarConfigFile(File folder) {
        return getConfigFile(folder, "ui_car.json");
    }

    public static File getTrackConfigFile(File folder) {
        return getConfigFile(folder, "ui_track.json");
    }

    private static File getConfigFile(File folder, String fileName) {
        Queue<File> folders = new LinkedList<>();
        folders.add(folder);

        while (!folders.isEmpty()) {
            File currentFolder = folders.poll();
            File[] children = currentFolder.listFiles();

            if (children == null) continue;

            for (File file : children) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    return file;
                }

                if (file.isDirectory()) {
                    folders.add(file);
                }
            }
        }

        return null;
    }

    private static void createCarBrandsMap() throws IOException {
        if (cars == null) {
            loadCars();
        }
        if (carsByBrand == null) {
            carsByBrand = new HashMap<>();
        }

        Map<String, ObservableList<Car>> aux = new HashMap<>();
        for (Car car : cars) {
            String brand = car.getBrand();
            if (aux.containsKey(brand)) {
                aux.get(brand).add(car);
            }
            else {
                ObservableList<Car> newList = FXCollections.observableArrayList(car);
                aux.put(brand, newList);
            }
        }

        carsByBrand = aux;
    }

    public static File[] getTournamentFolders() {
        return tournamentFolders;
    }

    public static void setTournamentFolders(File[] tournamentFolders) {
        AppData.tournamentFolders = tournamentFolders;
    }

    public static ConcurrentHashMap<String, String> getTrackNames() {
        if (trackNames == null) {
            trackNames = new ConcurrentHashMap<>();
        }

        return trackNames;
    }

    public static String getTrackName(String folderName) {
        if (trackNames == null) {
            trackNames = new ConcurrentHashMap<>();
        }

        return trackNames.get(folderName);
    }
}
