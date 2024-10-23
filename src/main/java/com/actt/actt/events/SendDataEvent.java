package com.actt.actt.events;

import javafx.event.Event;
import javafx.event.EventType;

import java.util.Map;
import java.util.Optional;

public class SendDataEvent extends Event {
    public static final EventType<SendDataEvent> SEND_DATA = new EventType<>(Event.ANY, "SEND_DATA");
    private final String action;
    private final Map<String, Object> data;

    public SendDataEvent(String action, Map<String, Object> d) {
        super(SEND_DATA);
        this.action = action;
        data = d;
    }

    public Optional<Object> getData(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public String getAction() { return this.action; }
}
