package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Selftask extends Task {

    public Selftask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.SELF;
    }

    @Override
    public String toString() {
        return "<Standard: id=" + id + "; name=" + name + "; desc=" +
                description + "; status=" + status + ">";
    }

    @Override
    public Task copy() {
        Selftask copy = new Selftask(name, description, startTime, duration);
        copy.setId(id);
        copy.setStatus(status);
        return copy;
    }

    @Override
    public String convertToFileRecord() {
        return String.join(DELIMITER, super.convertToFileRecord(), "null");
    }

}
