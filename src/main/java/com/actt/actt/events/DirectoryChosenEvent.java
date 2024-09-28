package com.actt.actt.events;

import javafx.event.Event;
import javafx.event.EventType;

import java.io.File;

public class DirectoryChosenEvent extends Event {
    public static final EventType<DirectoryChosenEvent> DIR_CHOSEN = new EventType<>(Event.ANY, "DIR_CHOSEN");

    private final File directory;

    public DirectoryChosenEvent(File directory) {
        super(DIR_CHOSEN);
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }
}
