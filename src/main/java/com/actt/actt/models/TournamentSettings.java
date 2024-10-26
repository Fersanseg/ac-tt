package com.actt.actt.models;

public class TournamentSettings {
    private String name;
    private CarClassSettings[] classes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CarClassSettings[] getClasses() {
        return classes;
    }

    public void setClasses(CarClassSettings[] classes) {
        this.classes = classes;
    }
}
