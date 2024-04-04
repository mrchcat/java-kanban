package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epictask extends Task {

    public Epictask(String name, String description) {
        super(name, description, Duration.ZERO);
    }

    public void switchOffTime() {
        setTimeDefined(false);
        setStartTime(LocalDateTime.MAX);
        setDuration(Duration.ZERO);
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.EPIC;
    }

    @Override
    public String toString() {
        return "<Epic: id=" + id + "; name=" + name + "; desc=" +
                description + "; status=" + status + ">";
    }

    @Override
    public Epictask copy() {
        Epictask copy = new Epictask(name, description);
        copy.setId(id);
        copy.setStatus(status);
        copy.setTimeDefined(isTimeDefined);
        copy.setStartTime(startTime);
        copy.setDuration(duration);
        return copy;
    }
}