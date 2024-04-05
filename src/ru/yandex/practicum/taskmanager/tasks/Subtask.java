package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Integer epicId) {
        super(name, description, startTime, duration);
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
    public Subtask copy() {
        Subtask copy = new Subtask(name, description, startTime, duration, epicId);
        copy.setId(id);
        copy.setStatus(status);
        return copy;
    }

    @Override
    public Object[] convertToObjectArray() {
        Object[] fields = super.convertToObjectArray();
        fields[fields.length - 1] = epicId;
        return fields;
    }
}

