package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epictask extends Task {

    public Epictask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, null, null);
    }

    public void switchOffTime() {
        setStartTime(null);
        setDuration(null);
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.EPIC;
    }

    @Override
    public Epictask copy() {
        Epictask copy = new Epictask(name, description, null, null);
        copy.setId(id);
        copy.setStatus(status);
        copy.setStartTime(startTime);
        copy.setDuration(duration);
        return copy;
    }
}