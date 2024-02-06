package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryArray;
import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    private static final int HISTORY_SIZE = 10;
    private static final int START_ID_BY_DEFAULT = 1;

    public static TaskManager getDefault() {
        var tasks = new InMemoryMap<Integer, Task>();
        var subordinates = new InMemoryMap<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(START_ID_BY_DEFAULT);
        var queue = new CircularQueue<>(HISTORY_SIZE, new InMemoryArray<Integer, Task>(HISTORY_SIZE));
        return new TaskManager(tasks, subordinates, generator, queue);
    }
}
