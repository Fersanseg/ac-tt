package com.actt.actt.models;

import java.util.ArrayList;
import java.util.List;

public class Driver {
    public Driver(ResultsJSONDriver driver) {
        name = driver.getName();
        car = driver.getCar();
        driverResults = new ArrayList<>();
    }

    private final String name;
    public String getName() { return name; }

    private final String car;
    public String getCar() { return car; }

    private String carClass;
    public String getCarClass() { return carClass; }
    public void setCarClass(String cc) { carClass = cc; }

    private int totalPoints;
    public int getTotalPoints() { return totalPoints; }
    public void addPoints(int points) { totalPoints += Math.abs(points); }

    private List<DriverResults> driverResults;
    public List<DriverResults> getDriverResults() {
        return driverResults == null ? new ArrayList<>() : driverResults;
    }
    public void addDriverResults(DriverResults results) {
        if (driverResults == null) {
            driverResults = new ArrayList<>();
        }

        driverResults.add(results);
    }

    public static Driver find(List<Driver> drivers, String name) {
        for (var driver : drivers) {
            if (driver.getName().equals(name)) {
                return driver;
            }
        }

        return null;
    }
}
