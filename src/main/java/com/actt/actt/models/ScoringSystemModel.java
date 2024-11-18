package com.actt.actt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    public boolean isValid() {
        boolean validPoints = getPoints() != null && getPoints().length != 0;
        boolean validName = getName() != null && !getName().isEmpty();
        return validPoints && validName;
    }
}
