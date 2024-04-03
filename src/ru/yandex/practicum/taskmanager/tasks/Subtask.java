package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Integer epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Duration duration, Integer epicId) {
        super(name, description, duration);
        this.epicId = epicId;
    }


    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.SUBTASK;
    }

    @Override
    public String toString() {
        return "<Subtask: id=" + super.getId() + "; name=" + super.getName()
                + "; desc=" + super.getDescription() + "; status=" + super.getStatus()
                + "; epicId=" + epicId + ">";
    }

    @Override
    public Subtask copy() {
        Subtask copy;
        if (isTimeDefined) {
            copy = new Subtask(name, description, startTime, duration, epicId);
        } else {
            copy = new Subtask(name, description, duration, epicId);
        }
        copy.setId(id);
        copy.setStatus(status);
        return copy;
    }

    @Override
    public String convertToFileRecord() {
        return String.join(DELIMITER, super.convertToFileRecord(), epicId.toString());
    }
}

