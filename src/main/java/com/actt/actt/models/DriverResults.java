package com.actt.actt.models;

public class DriverResults {
    private String raceId; // the name of the file
    public String getRaceId() { return raceId; }
    public void setRaceId(String raceId) { this.raceId = raceId; }

    private int position; // CLASS position, not OVERALL race position
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    private int points; // points gained in this race
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
