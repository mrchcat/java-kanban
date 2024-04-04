package ru.yandex.practicum.taskmanager.tasks;

public class Epictask extends Task {

    public Epictask(String name, String description) {
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
        Epictask copy = new Epictask(name, description);
        copy.setId(id);
        copy.setStatus(status);
        copy.setStartTime(startTime);
        copy.setDuration(duration);
        return copy;
    }
}