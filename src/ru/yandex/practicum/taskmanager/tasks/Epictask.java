package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epictask extends Task {
    private boolean isTimeDefined;

    public Epictask(String name, String description) {
        super(name, description, LocalDateTime.MAX, Duration.ZERO);
        this.isTimeDefined = false;
    }

    public void switchOffTime() {
        setTimeDefined(false);
        setStartTime(LocalDateTime.MAX);
        setDuration(Duration.ZERO);
    }

    public boolean isTimeDefined() {
        return isTimeDefined;
    }

    public void setTimeDefined(boolean timeDefined) {
        isTimeDefined = timeDefined;
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
    public Task copy() {
        Epictask copy = new Epictask(name, description);
        copy.setId(id);
        copy.setStatus(status);
        copy.setTimeDefined(isTimeDefined);
        copy.setStartTime(startTime);
        copy.setDuration(duration);
        return copy;
    }

    @Override
    public String convertToFileRecord() {
        return String.join(DELIMITER,
                id.toString(),
                getSubordination().toString(),
                name,
                status.toString(),
                description,
                String.valueOf(isTimeDefined),
                String.valueOf(startTime.toLocalDate().toEpochDay()),
                String.valueOf(startTime.toLocalTime().toSecondOfDay()),
                String.valueOf(duration.toSeconds()),
                "null");
    }
}