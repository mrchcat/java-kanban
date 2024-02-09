package ru.yandex.practicum.taskmanager.tasks;

import ru.yandex.practicum.taskmanager.enums.Type;

public class Epictask extends Task {
    public Epictask(String name, String description) {
        super(name, description);
    }

    @Override
    public Type getType() {
        return Type.EPIC;

    }

    @Override
    public String toString() {
        return "<Epic: id=" + super.getId() + "; name=" + super.getName() + "; desc=" +
                super.getDescription() + "; status=" + super.getStatus() + ">";
    }
}