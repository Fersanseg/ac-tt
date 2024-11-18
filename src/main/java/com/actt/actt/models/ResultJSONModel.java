package com.actt.actt.models;

import java.util.ArrayList;
import java.util.List;

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

    public List<Driver> getDrivers() {
        List<Driver> out = new ArrayList<>();
        for (var player : getPlayers()) {
            out.add(new Driver(player));
        }

        return out;
    }
}

