package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Selftask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        LocalDateTime dateTime = LocalDateTime.of(2024, 04, 01, 13, 20);
        Duration duration = Duration.ofDays(3);
        for (int i = 1; i <= 5; i++) {
            taskManager.add(new Selftask("name".concat(String.valueOf(i)), "desc".concat(String.valueOf(i)), dateTime, duration));
        }
        taskManager.get(1);
        taskManager.get(3);
    }
}