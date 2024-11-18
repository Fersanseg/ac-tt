package com.actt.actt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

public class CarClassSettings {
    private String name;
    private Car[] cars;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Car[] getCars() {
        return cars;
    }

    public void setCars(Car[] cars) {
        this.cars = cars;
    }

    @JsonIgnore
    public boolean isInClass(String car) {
        return Arrays.stream(getCars()).anyMatch(c -> c.getFolderName().equals(car));
    }
}
