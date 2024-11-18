package com.actt.actt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Driver {
    public Driver(ResultsJSONDriver driver) {
        name = driver.getName();
        car = driver.getCar();
        driverResults = new ArrayList<>();
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final String car;
    public String getCar() {
        return car;
    }

    private int totalPoints;
    public int getTotalPoints() {
        return totalPoints;
    }

    private List<DriverResults> driverResults;
    public List<DriverResults> getDriverResults() {
        return driverResults;
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
