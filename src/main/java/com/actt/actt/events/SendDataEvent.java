package com.actt.actt.events;

import javafx.event.Event;
import javafx.event.EventType;

import java.util.Map;
import java.util.Optional;

public class SendDataEvent extends Event {
    public static final EventType<SendDataEvent> SEND_DATA = new EventType<>(Event.ANY, "SEND_DATA");
    private final Map<String, Object> data;

    public SendDataEvent(Map<String, Object> d) {
        super(SEND_DATA);
        data = d;
    }

    public Optional<Object> getData(String key) {
        return Optional.ofNullable(data.get(key));
    }
}
