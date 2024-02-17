package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Selftask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Selftask selftask = new Selftask("сходить за продуктами", "купить сыр, молоко, творог");
        selftask = taskManager.add(selftask);

    }
}