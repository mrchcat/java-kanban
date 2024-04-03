package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epictask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        LocalDateTime dateTime = LocalDateTime.of(2024, 4, 1, 13, 20);
        Duration duration = Duration.ofDays(3);
        for (int i = 1; i <= 5; i++) {
            taskManager.add(new Epictask("name".concat(String.valueOf(i)), "desc".concat(String.valueOf(i))));
        }
        taskManager.get(1);
        taskManager.get(3);
    }
}