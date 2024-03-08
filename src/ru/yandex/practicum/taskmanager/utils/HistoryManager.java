package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task item);

    void remove(int id);

    List<Task> getHistory();

    void clear();
}
