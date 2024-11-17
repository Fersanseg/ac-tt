package com.actt.actt.models;

public class ResultJSONModel {
    private String track;
    private ResultsJSONDriver[] players;
    private ResultsJSONSessions[] sessions;

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public ResultsJSONDriver[] getPlayers() {
        return players;
    }

    public void setPlayers(ResultsJSONDriver[] players) {
        this.players = players;
    }

    public ResultsJSONSessions[] getSessions() {
        return sessions;
    }

    public void setSessions(ResultsJSONSessions[] sessions) {
        this.sessions = sessions;
    }
}

