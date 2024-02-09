package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void put(Task item);

    List<Task> getHistory();

    void clear();
}
