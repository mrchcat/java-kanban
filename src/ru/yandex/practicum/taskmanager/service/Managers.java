package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryArray;
import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    private final static int HISTORY_SIZE = 10;
    private final static int START_ID_BY_DEFAULT = 1;

    public static TaskManager getDefault() {
        var tasks = new InMemoryMap<Integer, Task>();
        var subordinates = new InMemoryMap<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(START_ID_BY_DEFAULT);
        var history = getDefaultHistory();
        return new RegularTaskManager(tasks, subordinates, generator, history);
    }

    public static HistoryManager getDefaultHistory() {
        var repositoryForHistoryManager = new InMemoryArray<Integer, Task>(HISTORY_SIZE);
        return new CircularQueue(HISTORY_SIZE, repositoryForHistoryManager);
    }

}
