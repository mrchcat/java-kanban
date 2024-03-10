package ru.yandex.practicum.taskmanager.tasks;

public class Epictask extends Task {
    public Epictask(String name, String description) {
        super(name, description);
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.EPIC;

    }

    @Override
    public String toString() {
        return "<Epic: id=" + super.getId() + "; name=" + super.getName() + "; desc=" +
                super.getDescription() + "; status=" + super.getStatus() + ">";
    }

    @Override
    public Task copy() {
        Epictask copy = new Epictask(getName(), getDescription());
        copy.setId(getId());
        copy.setStatus(getStatus());
        return copy;
    }
}