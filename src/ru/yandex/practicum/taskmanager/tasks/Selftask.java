package ru.yandex.practicum.taskmanager.tasks;

import ru.yandex.practicum.taskmanager.enums.*;

public class Selftask extends Task {

    public Selftask(String name, String description) {
        super(name, description);
    }

    @Override
    public Type getType() {
        return Type.SELF;
    }

    @Override
    public String toString() {
        return "<Standard: id=" + super.getId() + "; name=" + super.getName() + "; desc=" +
                super.getDescription() + "; status=" + super.getStatus() + ">";
    }
}
