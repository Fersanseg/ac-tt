package com.actt.actt.models;

public class ResultJSONModel {
    private String track;
    public String getTrack() {
        return track;
    }

    private ResultsJSONDriver[] players;
    public ResultsJSONDriver[] getPlayers() {
        return players;
    }

    private ResultsJSONSessions[] sessions;
    public ResultsJSONSessions[] getSessions() {
        return sessions;
    }
}

