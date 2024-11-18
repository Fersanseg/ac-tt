package com.actt.actt.models;

public class ResultJSONModel {
    private String track;
    public String getTrack() { return track; }

    private String trackId;
    public String getTrackId() { return trackId; }
    public void setTrackId(String id) { trackId = id; }

    private ResultsJSONDriver[] players;
    public ResultsJSONDriver[] getPlayers() { return players; }

    private ResultsJSONSessions[] sessions;
    public ResultsJSONSessions[] getSessions() { return sessions; }
}

