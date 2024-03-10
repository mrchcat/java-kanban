package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.LinkedHashHistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    private static final int START_ID_BY_DEFAULT = 1;

    public static TaskManager getDefault() {
        Repository<Integer, Task> tasks = new InMemoryMap<>();
        Repository<Integer, ArrayList<Integer>> subordinates = new InMemoryMap<>();
        Generator generator = new SerialGenerator(START_ID_BY_DEFAULT);
        HistoryManager history = getDefaultHistory();
        return new RegularTaskManager(tasks, subordinates, generator, history);
    }

    public static HistoryManager getDefaultHistory() {
        return new LinkedHashHistoryManager();
    }

}
