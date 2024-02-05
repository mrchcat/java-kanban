package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryBase;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {

    private static final int HISTORY_SIZE = 10;
    private static final int START_ID = 1;
    public static TaskManager getDefault() {
        var tasks = new InMemoryBase<Integer, Task>();
        var subordinates = new InMemoryBase<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(START_ID);
        var queue = new CircularQueue<>(HISTORY_SIZE, new InMemoryBase<Integer, Task>());
        return new TaskManager(tasks, subordinates, generator, queue);
    }
}
