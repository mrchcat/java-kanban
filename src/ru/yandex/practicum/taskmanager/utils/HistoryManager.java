package ru.yandex.practicum.taskmanager.utils;

import java.util.List;

public interface HistoryManager<T> {
    void put(T item);

    List<T> getAll();

    void clear();
}
