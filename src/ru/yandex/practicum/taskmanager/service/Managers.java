package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.CacheBase;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    public static TaskManager getDefault() {
        Repository tasks = new CacheBase<Integer, Task>();
        Repository subordinates = new CacheBase<Integer, ArrayList<Integer>>();
        Generator generator = new SerialGenerator(1);
        return new TaskManager(tasks, subordinates, generator);
    }
}
