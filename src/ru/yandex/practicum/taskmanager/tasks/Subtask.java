package ru.yandex.practicum.taskmanager.tasks;

import ru.yandex.practicum.taskmanager.enums.Type;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "<Subtask: id=" + super.getId() + "; name=" + super.getName()
                + "; desc=" + super.getDescription() + "; status=" + super.getStatus()
                + "; epicId=" + epicId + ">";
    }

}

