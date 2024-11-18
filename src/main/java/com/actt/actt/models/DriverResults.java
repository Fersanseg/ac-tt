package com.actt.actt.models;

public class DriverResults {
    private String raceId; // the name of the file
    public String getRaceId() {
        return raceId;
    }

    private int position;
    public int getPosition() {
        return position;
    }

    private int points; // points gained in this race
    public int getPoints() {
        return points;
    }
}
