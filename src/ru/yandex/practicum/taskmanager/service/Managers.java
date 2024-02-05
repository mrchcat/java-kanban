package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryBase;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {

    private static final int HISTORY_SIZE = 10;
    public static TaskManager getDefault() {
        var tasks = new InMemoryBase<Integer, Task>();
        var subordinates = new InMemoryBase<Integer, ArrayList<Integer>>();
        Repository<Integer, Task> queueArray = new InMemoryBase<>();
        CircularQueue<Task> queue = new CircularQueue<>(HISTORY_SIZE, queueArray);
        Generator generator = new SerialGenerator(1);
        return new TaskManager(tasks, subordinates, generator, queue);
    }
}
