package ru.yandex.practicum.taskmanager.tasks;

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
        Subtask copy = new Subtask(getName(), getDescription(), getEpicId());
        copy.setId(getId());
        copy.setStatus(getStatus());
        return copy;
    }

    @Override
    public String convertToFileRecord() {
        return String.join(",",
                getId().toString(),
                getSubordination().toString(),
                getName(),
                getStatus().toString(),
                getDescription(),
                epicId.toString());
    }
}

