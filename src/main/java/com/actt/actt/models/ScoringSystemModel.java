package com.actt.actt.models;

public class ScoringSystemModel {
    private String name;
    private int[] points;

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

    public boolean isValid() {
        boolean validPoints = getPoints() != null && getPoints().length != 0;
        boolean validName = getName() != null && !getName().isEmpty();
        return validPoints && validName;
    }
}