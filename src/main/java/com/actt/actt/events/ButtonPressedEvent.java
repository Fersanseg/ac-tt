package com.actt.actt.events;

import javafx.event.Event;
import javafx.event.EventType;

public class ButtonPressedEvent extends Event {
    public static final EventType<ButtonPressedEvent> BUTTON_PRESSED = new EventType<>(Event.ANY, "BUTTON_PRESSED");
    private final String id;

    public ButtonPressedEvent(String id) {
        super(BUTTON_PRESSED);
        this.id = id;
    }

    public String getButtonId() {
        return id;
    }
}
