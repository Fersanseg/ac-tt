package com.actt.actt.models;

public class ScoringSystemModel {
    private String name;
    private int[] points;
    private String id;

    public ScoringSystemModel() {}
    public ScoringSystemModel(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getPoints() {
        return points;
    }

    public void setPoints(int[] points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public boolean isValid() {
        boolean validPoints = getPoints() != null && getPoints().length != 0;
        boolean validName = getName() != null && !getName().isEmpty();
        return validPoints && validName;
    }
}
